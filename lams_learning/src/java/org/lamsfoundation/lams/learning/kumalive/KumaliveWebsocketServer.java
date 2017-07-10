package org.lamsfoundation.lams.learning.kumalive;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.json.JSONArray;
import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;
import org.lamsfoundation.lams.learning.kumalive.model.Kumalive;
import org.lamsfoundation.lams.learning.service.ILearnerService;
import org.lamsfoundation.lams.security.ISecurityService;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Processes messages for Kumalive
 *
 * @author Marcin Cieslak
 */
@ServerEndpoint("/kumaliveWebsocket")
public class KumaliveWebsocketServer {

    private class KumaliveUser {
	private UserDTO userDTO;
	private Session websocket;
	private boolean isTeacher;
	private boolean roleTeacher;

	private KumaliveUser(User user, Session websocket, boolean isTeacher, boolean roleTeacher) {
	    this.userDTO = user.getUserDTO();
	    this.websocket = websocket;
	    this.isTeacher = isTeacher;
	    this.roleTeacher = roleTeacher;
	}
    }

    private class KumaliveDTO {
	private Long id;
	private String name;
	private UserDTO createdBy;
	private boolean raiseHandPrompt;
	private final List<Integer> raisedHand = new CopyOnWriteArrayList<>();
	private Integer speaker;
	private final Map<String, KumaliveUser> learners = new ConcurrentHashMap<>();

	private KumaliveDTO(Kumalive kumalive) {
	    this.id = kumalive.getKumaliveId();
	    this.name = kumalive.getName();
	    this.createdBy = kumalive.getCreatedBy().getUserDTO();
	}
    }

    private static Logger log = Logger.getLogger(KumaliveWebsocketServer.class);

    private static ILearnerService learnerService;
    private static ISecurityService securityService;
    private static IUserManagementService userManagementService;
    // mapping org ID -> Kumalive
    private static final Map<Integer, KumaliveDTO> kumalives = new TreeMap<>();

    @OnOpen
    public void registerUser(Session websocket) throws IOException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	Integer userId = getUser(websocket).getUserId();
	if (!getSecurityService().hasOrgRole(organisationId, userId,
		new String[] { Role.GROUP_MANAGER, Role.MONITOR, Role.LEARNER }, "register on kumalive", false)) {
	    // prevent unauthorised user from accessing Kumalive
	    String warning = "User " + userId + " is not a monitor nor a learner of organisation " + organisationId;
	    log.warn(warning);
	    websocket.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, warning));
	}
    }

    @OnClose
    public void unregisterUser(Session websocket, CloseReason reason) throws JSONException, IOException {
	String login = websocket.getUserPrincipal().getName();
	if (login == null) {
	    return;
	}

	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = KumaliveWebsocketServer.kumalives.get(organisationId);
	if (kumalive == null) {
	    return;
	}
	KumaliveUser user = kumalive.learners.remove(login);
	if (user != null) {
	    Integer userId = user.userDTO.getUserID();
	    if (kumalive.raisedHand != null) {
		kumalive.raisedHand.remove(userId);
	    }
	    if (userId.equals(kumalive.speaker)) {
		kumalive.speaker = null;
	    }
	}

	sendRefresh(kumalive);
    }

    @OnMessage
    public void receiveRequest(String input, Session session) throws JSONException, IOException {
	if (StringUtils.isBlank(input)) {
	    return;
	}
	if (input.equalsIgnoreCase("ping")) {
	    // just a ping every few minutes
	    return;
	}

	JSONObject requestJSON = new JSONObject(input);
	switch (requestJSON.getString("type")) {
	    case "start":
		start(requestJSON, session);
		break;
	    case "join":
		join(requestJSON, session);
		break;
	    case "raiseHandPrompt":
		raiseHandPrompt(requestJSON, session);
		break;
	    case "downHandPrompt":
		downHandPrompt(requestJSON, session);
		break;
	    case "raiseHand":
		raiseHand(requestJSON, session);
		break;
	    case "downHand":
		downHand(requestJSON, session);
		break;
	    case "speak":
		speak(requestJSON, session);
		break;
	    case "score":
		score(requestJSON, session);
		break;
	    case "finish":
		finish(requestJSON, session);
		break;
	}
    }

    /**
     * Fetches an existing Kumalive, creates it or tells teacher to create it
     */
    private void start(JSONObject requestJSON, Session websocket) throws JSONException, IOException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumaliveDTO = kumalives.get(organisationId);
	boolean isTeacher = false;
	if (kumaliveDTO == null) {
	    String name = requestJSON.optString("name");
	    String role = websocket.getRequestParameterMap().get(AttributeNames.PARAM_ROLE).get(0);
	    User user = getUser(websocket);
	    Integer userId = user.getUserId();
	    isTeacher = !Role.LEARNER.equalsIgnoreCase(role)
		    && (getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_MANAGER)
			    || getUserManagementService().isUserInRole(userId, organisationId, Role.MONITOR));
	    // if it kumalive does not exists and the user is not a teacher or he did not provide a name yet,
	    // kumalive will not get created
	    Kumalive kumalive = KumaliveWebsocketServer.getLearnerService().startKumalive(organisationId, userId, name,
		    isTeacher && StringUtils.isNotBlank(name));
	    if (kumalive != null) {
		kumaliveDTO = new KumaliveDTO(kumalive);
		kumalives.put(organisationId, kumaliveDTO);
	    }
	}

	// tell teacher to provide a name for Kumalive and create it
	// or tell learner to join
	websocket.getBasicRemote()
		.sendText("{ \"type\" : \"" + (kumaliveDTO == null && isTeacher ? "create" : "join") + "\" }");
    }

    /**
     * Adds a learner or a teacher to Kumalive
     */
    private void join(JSONObject requestJSON, Session websocket) throws JSONException, IOException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = kumalives.get(organisationId);
	if (kumalive == null) {
	    websocket.getBasicRemote().sendText("{ \"type\" : \"start\"}");
	    return;
	}

	User user = getUser(websocket);
	Integer userId = user.getUserId();
	String login = user.getLogin();
	boolean isTeacher = getUserManagementService().isUserInRole(userId, organisationId, Role.GROUP_MANAGER)
		|| getUserManagementService().isUserInRole(userId, organisationId, Role.MONITOR);
	String role = websocket.getRequestParameterMap().get(AttributeNames.PARAM_ROLE).get(0);

	KumaliveUser learner = kumalive.learners.get(login);
	boolean roleTeacher = isTeacher && !Role.LEARNER.equalsIgnoreCase(role)
		&& ("teacher".equalsIgnoreCase(role) || learner == null || learner.roleTeacher);
	if (learner != null && !learner.websocket.getId().equals(websocket.getId())) {
	    // only one websocket per user
	    learner.websocket.close(
		    new CloseReason(CloseCodes.NOT_CONSISTENT, "Another websocket for same user was estabilished"));
	}

	learner = new KumaliveUser(user, websocket, isTeacher, roleTeacher);
	kumalive.learners.put(login, learner);

	sendRefresh(kumalive);
    }

    /**
     * Send full Kumalive state to all learners and teachers
     */
    private void sendRefresh(KumaliveDTO kumalive) throws JSONException, IOException {
	JSONObject responseJSON = new JSONObject();
	responseJSON.put("type", "refresh");
	// Kumalive title
	responseJSON.put("name", kumalive.name);

	// teacher details
	responseJSON.put("teacherId", kumalive.createdBy.getUserID());
	responseJSON.put("teacherName", kumalive.createdBy.getFirstName() + " " + kumalive.createdBy.getLastName());
	responseJSON.put("teacherPortraitUuid", kumalive.createdBy.getPortraitUuid());

	// current state of question and speaker
	responseJSON.put("raiseHandPrompt", kumalive.raiseHandPrompt);
	if (!kumalive.raisedHand.isEmpty()) {
	    responseJSON.put("raisedHand", new JSONArray(kumalive.raisedHand));
	}
	responseJSON.put("speaker", kumalive.speaker);

	// each learner's details
	JSONArray learnersJSON = new JSONArray();
	JSONObject logins = new JSONObject();
	for (KumaliveUser participant : kumalive.learners.values()) {
	    UserDTO participantDTO = participant.userDTO;

	    JSONObject learnerJSON = new JSONObject();
	    learnerJSON.put("id", participantDTO.getUserID());
	    learnerJSON.put("firstName", participantDTO.getFirstName());
	    learnerJSON.put("lastName", participantDTO.getLastName());
	    learnerJSON.put("portraitUuid", participantDTO.getPortraitUuid());
	    learnerJSON.put("roleTeacher", participant.roleTeacher);

	    logins.put("user" + participantDTO.getUserID(), participantDTO.getLogin());

	    learnersJSON.put(learnerJSON);
	}
	responseJSON.put("learners", learnersJSON);

	String learnerResponse = responseJSON.toString();
	JSONObject teacherResponseJSON = null;

	// send refresh to everyone
	for (KumaliveUser participant : kumalive.learners.values()) {
	    Basic channel = participant.websocket.getBasicRemote();
	    if (participant.isTeacher) {
		// send extra information to teachers
		if (teacherResponseJSON == null) {
		    responseJSON.put("isTeacher", true);
		    responseJSON.put("logins", logins);
		    teacherResponseJSON = responseJSON;
		}
		teacherResponseJSON.put("roleTeacher", participant.roleTeacher);
		channel.sendText(teacherResponseJSON.toString());
	    } else {
		channel.sendText(learnerResponse);
	    }
	}
    }

    /**
     * Tell learners that the teacher asked
     */
    private void raiseHandPrompt(JSONObject requestJSON, Session websocket) throws IOException, JSONException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = kumalives.get(organisationId);

	User user = getUser(websocket);
	Integer userId = user.getUserId();

	if (!getSecurityService().hasOrgRole(organisationId, userId, new String[] { Role.GROUP_MANAGER, Role.MONITOR },
		"kumalive raise hand prompt", false)) {
	    String warning = "User " + userId + " is not a monitor of organisation " + organisationId;
	    log.warn(warning);
	    return;
	}

	kumalive.raiseHandPrompt = true;
	sendRefresh(kumalive);
    }

    /**
     * Tell learners that the teacher finished a question
     */
    private void downHandPrompt(JSONObject requestJSON, Session websocket) throws IOException, JSONException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = kumalives.get(organisationId);

	User user = getUser(websocket);
	Integer userId = user.getUserId();

	if (!getSecurityService().hasOrgRole(organisationId, userId, new String[] { Role.GROUP_MANAGER, Role.MONITOR },
		"kumalive down hand prompt", false)) {
	    String warning = "User " + userId + " is not a monitor of organisation " + organisationId;
	    log.warn(warning);
	    return;
	}

	kumalive.raiseHandPrompt = false;
	kumalive.raisedHand.clear();
	sendRefresh(kumalive);
    }

    /**
     * Tell learners that a learner raised hand
     */
    private void raiseHand(JSONObject requestJSON, Session websocket) throws IOException, JSONException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = kumalives.get(organisationId);

	User user = getUser(websocket);
	Integer userId = user.getUserId();

	if (!getSecurityService().hasOrgRole(organisationId, userId,
		new String[] { Role.GROUP_MANAGER, Role.MONITOR, Role.LEARNER }, "kumalive raise hand", false)) {
	    String warning = "User " + userId + " is not a monitor nor a learner of organisation " + organisationId;
	    log.warn(warning);
	    return;
	}

	if (!kumalive.raiseHandPrompt) {
	    log.warn("Raise hand prompt was not sent by teacher yet for organisation " + organisationId);
	    return;
	}

	if (kumalive.raisedHand.contains(userId)) {
	    return;
	}

	kumalive.raisedHand.add(userId);
	sendRefresh(kumalive);
    }

    /**
     * Tell learners that a learner put hadn down
     */
    private void downHand(JSONObject requestJSON, Session websocket) throws IOException, JSONException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = kumalives.get(organisationId);

	User user = getUser(websocket);
	Integer userId = user.getUserId();

	if (!getSecurityService().hasOrgRole(organisationId, userId,
		new String[] { Role.GROUP_MANAGER, Role.MONITOR, Role.LEARNER }, "kumalive down hand", false)) {
	    String warning = "User " + userId + " is not a monitor nor a learner of organisation " + organisationId;
	    log.warn(warning);
	    return;
	}

	if (kumalive.raisedHand == null) {
	    return;
	}

	kumalive.raisedHand.remove(userId);

	sendRefresh(kumalive);
    }

    /**
     * Set up a speaker or remove him
     */
    private void speak(JSONObject requestJSON, Session websocket) throws IOException, JSONException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = kumalives.get(organisationId);

	User user = getUser(websocket);
	Integer userId = user.getUserId();

	if (!getSecurityService().hasOrgRole(organisationId, userId, new String[] { Role.GROUP_MANAGER, Role.MONITOR },
		"kumalive speak", false)) {
	    String warning = "User " + userId + " is not a monitor of organisation " + organisationId;
	    log.warn(warning);
	    return;
	}

	kumalive.speaker = requestJSON.optInt("speaker");
	sendRefresh(kumalive);
    }

    /**
     * Save score for a learner
     */
    private void score(JSONObject requestJSON, Session websocket) throws IOException, JSONException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = kumalives.get(organisationId);

	User user = getUser(websocket);
	Integer userId = user.getUserId();

	if (!getSecurityService().hasOrgRole(organisationId, userId, new String[] { Role.GROUP_MANAGER, Role.MONITOR },
		"kumalive score", false)) {
	    String warning = "User " + userId + " is not a monitor of organisation " + organisationId;
	    log.warn(warning);
	    return;
	}

	KumaliveWebsocketServer.getLearnerService().scoreKumalive(kumalive.id,
		requestJSON.getInt(AttributeNames.PARAM_USER_ID), Short.valueOf(requestJSON.getString("score")));
	sendRefresh(kumalive);
    }

    /**
     * End Kumalive
     */
    private void finish(JSONObject requestJSON, Session websocket) throws IOException, JSONException {
	Integer organisationId = Integer
		.valueOf(websocket.getRequestParameterMap().get(AttributeNames.PARAM_ORGANISATION_ID).get(0));
	KumaliveDTO kumalive = kumalives.get(organisationId);

	User user = getUser(websocket);
	Integer userId = user.getUserId();

	if (!getSecurityService().hasOrgRole(organisationId, userId, new String[] { Role.GROUP_MANAGER, Role.MONITOR },
		"kumalive finish", false)) {
	    String warning = "User " + userId + " is not a monitor of organisation " + organisationId;
	    log.warn(warning);
	    return;
	}

	KumaliveWebsocketServer.getLearnerService().finishKumalive(kumalive.id);
	kumalives.remove(organisationId);
	for (KumaliveUser participant : kumalive.learners.values()) {
	    participant.websocket.getBasicRemote().sendText("{ \"type\" : \"finish\"}");
	}
    }

    private User getUser(Session websocket) {
	return getUserManagementService().getUserByLogin(websocket.getUserPrincipal().getName());
    }

    private static ILearnerService getLearnerService() {
	if (learnerService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils
		    .getWebApplicationContext(SessionManager.getServletContext());
	    learnerService = (ILearnerService) ctx.getBean("learnerService");
	}
	return learnerService;
    }

    private ISecurityService getSecurityService() {
	if (securityService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(SessionManager.getServletContext());
	    securityService = (ISecurityService) ctx.getBean("securityService");
	}
	return securityService;
    }

    private IUserManagementService getUserManagementService() {
	if (userManagementService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(SessionManager.getServletContext());
	    userManagementService = (IUserManagementService) ctx.getBean("userManagementService");
	}
	return userManagementService;
    }
}