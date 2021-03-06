/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ************************************************************************
 */

package org.lamsfoundation.lams.authoring.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.tomcat.util.json.JSONArray;
import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;
import org.lamsfoundation.lams.authoring.ObjectExtractorException;
import org.lamsfoundation.lams.authoring.service.IAuthoringFullService;
import org.lamsfoundation.lams.integration.ExtCourseClassMap;
import org.lamsfoundation.lams.integration.ExtServer;
import org.lamsfoundation.lams.integration.service.IIntegrationService;
import org.lamsfoundation.lams.integration.util.LoginRequestDispatcher;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.BranchingActivity;
import org.lamsfoundation.lams.learningdesign.LearningDesign;
import org.lamsfoundation.lams.learningdesign.LearningDesignAccess;
import org.lamsfoundation.lams.learningdesign.LearningLibrary;
import org.lamsfoundation.lams.learningdesign.LearningLibraryGroup;
import org.lamsfoundation.lams.learningdesign.dto.AuthoringActivityDTO;
import org.lamsfoundation.lams.learningdesign.dto.LearningDesignDTO;
import org.lamsfoundation.lams.learningdesign.dto.ValidationErrorDTO;
import org.lamsfoundation.lams.learningdesign.service.ILearningDesignService;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.monitoring.service.IMonitoringService;
import org.lamsfoundation.lams.security.ISecurityService;
import org.lamsfoundation.lams.tool.IToolVO;
import org.lamsfoundation.lams.tool.ToolContentManager;
import org.lamsfoundation.lams.tool.ToolOutputDefinition;
import org.lamsfoundation.lams.tool.dto.ToolOutputDefinitionDTO;
import org.lamsfoundation.lams.tool.service.ILamsToolService;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.WorkspaceFolder;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.exception.UserException;
import org.lamsfoundation.lams.usermanagement.exception.WorkspaceFolderException;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.CentralConstants;
import org.lamsfoundation.lams.util.FileUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.workspace.web.WorkspaceAction;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Manpreet Minhas
 */
public class AuthoringAction extends LamsDispatchAction {

    private static Logger log = Logger.getLogger(AuthoringAction.class);

    private static IMonitoringService monitoringService;
    private static IUserManagementService userManagementService;
    private static ILamsToolService toolService;
    private static IAuthoringFullService authoringFullService;
    private static ILearningDesignService learningDesignService;
    private static ISecurityService securityService;
    private static IIntegrationService integrationService;

    private static int LEARNING_DESIGN_ACCESS_ENTRIES_LIMIT = 7;

    private Integer getUserId() {
	HttpSession ss = SessionManager.getSession();
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	return user != null ? user.getUserID() : null;
    }

    private String getUserLanguage() {
	HttpSession ss = SessionManager.getSession();
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	return user != null ? user.getLocaleLanguage() : "";
    }

    public ActionForward openAuthoring(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, JSONException {
	request.setAttribute(AttributeNames.PARAM_CONTENT_FOLDER_ID, FileUtil.generateUniqueContentFolderID());

	request.setAttribute("tools", getLearningDesignService().getToolDTOs(true, true, request.getRemoteUser()));
	// build list of existing learning library groups
	List<LearningLibraryGroup> groups = getLearningDesignService().getLearningLibraryGroups();
	JSONArray groupsJSON = new JSONArray();
	for (LearningLibraryGroup group : groups) {
	    JSONObject groupJSON = new JSONObject();
	    groupJSON.put("name", group.getName());
	    for (LearningLibrary learningLibrary : group.getLearningLibraries()) {
		groupJSON.append("learningLibraries", learningLibrary.getLearningLibraryId());
	    }
	    groupsJSON.put(groupJSON);
	}
	request.setAttribute("learningLibraryGroups", groupsJSON.toString());

	List<LearningDesignAccess> accessList = getAuthoringService().updateLearningDesignAccessByUser(getUserId());
	accessList = accessList.subList(0,
		Math.min(accessList.size(), AuthoringAction.LEARNING_DESIGN_ACCESS_ENTRIES_LIMIT - 1));
	Gson gson = new GsonBuilder().create();
	request.setAttribute("access", gson.toJson(accessList));

	request.setAttribute("licenses", getAuthoringService().getAvailableLicenses());

	boolean canSetReadOnly = getUserManagementService().isUserSysAdmin()
		|| getUserManagementService().isUserGlobalGroupAdmin();
	request.setAttribute("canSetReadOnly", canSetReadOnly);

	return mapping.findForward("openAutoring");
    }

    public ActionForward generateSVG(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	request.setAttribute("tools", getLearningDesignService().getToolDTOs(true, true, request.getRemoteUser()));

	return mapping.findForward("svgGenerator");
    }

    public ActionForward getToolOutputDefinitions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException, JSONException {
	IAuthoringFullService authoringFullService = getAuthoringService();
	Long toolContentID = WebUtil.readLongParam(request, "toolContentID");
	Integer definitionType = ToolOutputDefinition.DATA_OUTPUT_DEFINITION_TYPE_CONDITION;

	List<ToolOutputDefinitionDTO> defnDTOList = authoringFullService.getToolOutputDefinitions(toolContentID,
		definitionType);

	Gson gson = new GsonBuilder().create();
	response.setContentType("application/json;charset=utf-8");
	response.getWriter().write(gson.toJson(defnDTOList));
	return null;
    }

    @SuppressWarnings("unchecked")
    public ActionForward openLearningDesign(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException, JSONException {
	long learningDesignID = WebUtil.readLongParam(request, AttributeNames.PARAM_LEARNINGDESIGN_ID);
	LearningDesignDTO learningDesignDTO = getLearningDesignService().getLearningDesignDTO(learningDesignID,
		getUserLanguage());

	// some old LDs may not have learning library IDs filled in, try to find them
	for (AuthoringActivityDTO activity : (List<AuthoringActivityDTO>) learningDesignDTO.getActivities()) {
	    if (activity.getLearningLibraryID() == null) {
		getLearningDesignService().fillLearningLibraryID(activity);
	    }
	}

	Integer userId = getUserId();
	if (learningDesignDTO.getWorkspaceFolderID() != null) {
	    getAuthoringService().storeLearningDesignAccess(learningDesignID, userId);
	}

	response.setContentType("application/json;charset=utf-8");
	JSONObject responseJSON = new JSONObject();
	Gson gson = new GsonBuilder().create();
	JSONObject ldJSON = new JSONObject(gson.toJson(learningDesignDTO));

	// get all parent folders of the LD
	List<Integer> folderPath = new LinkedList<Integer>();
	WorkspaceFolder folder = (WorkspaceFolder) getUserManagementService().findById(WorkspaceFolder.class,
		learningDesignDTO.getWorkspaceFolderID());
	while (folder != null) {
	    Integer folderID = folder.getWorkspaceFolderId();
	    if (folderID.equals(WorkspaceAction.ROOT_ORG_FOLDER_ID)) {
		// we reached the top folder, finish
		folder = null;
	    } else {
		folderPath.add(folderID);
		folder = folder.getParentWorkspaceFolder();
	    }
	}
	// we'll go from top to bottom
	Collections.reverse(folderPath);
	ldJSON.put("folderPath", new JSONArray(gson.toJson(folderPath)));

	ldJSON.put("readOnlyEnabled", true);
	responseJSON.put("ld", ldJSON);

	List<LearningDesignAccess> accessList = getAuthoringService().updateLearningDesignAccessByUser(userId);
	accessList = accessList.subList(0,
		Math.min(accessList.size(), AuthoringAction.LEARNING_DESIGN_ACCESS_ENTRIES_LIMIT - 1));
	responseJSON.put("access", new JSONArray(gson.toJson(accessList)));

	response.getWriter().write(responseJSON.toString());
	return null;
    }

    public ActionForward finishLearningDesignEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	IAuthoringFullService authoringFullService = getAuthoringService();
	Long learningDesignID = WebUtil.readLongParam(request, "learningDesignID", false);
	boolean cancelled = WebUtil.readBooleanParam(request, "cancelled", false);

	try {
	    authoringFullService.finishEditOnFly(learningDesignID, getUserId(), cancelled);
	} catch (Exception e) {
	    String errorMsg = "Error occured ending EditOnFly" + e.getMessage() + " learning design id "
		    + learningDesignID;
	    log.error(errorMsg, e);
	    throw new IOException(e);
	}

	return null;
    }

    /**
     * Copy some existing content. Used when the user copies an activity in authoring. Expects one parameters -
     * toolContentId (the content to be copied)
     */
    public ActionForward copyToolContent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	IAuthoringFullService authoringFullService = getAuthoringService();
	try {
	    String customCSV = WebUtil.readStrParam(request, AttributeNames.PARAM_CUSTOM_CSV, true);
	    long toolContentID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID, false);
	    Long newToolContentID = authoringFullService.copyToolContent(toolContentID, customCSV);
	    response.setContentType("text/plain;charset=utf-8");
	    response.getWriter().write(newToolContentID.toString());
	} catch (Exception e) {
	    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	return null;
    }

    /**
     * Creates a copy of default tool content.
     */
    public ActionForward createToolContent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException, JSONException {
	IAuthoringFullService authoringFullService = getAuthoringService();
	Long toolID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_ID);
	Long toolContentID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID, true);
	if (toolContentID == null) {
	    // if the tool content ID was not provided, generate the next unique content ID for the tool
	    toolContentID = authoringFullService.insertToolContentID(toolID);
	}

	if (toolContentID != null) {
	    String contentFolderID = request.getParameter(AttributeNames.PARAM_CONTENT_FOLDER_ID);
	    if (StringUtils.isBlank(contentFolderID)) {
		contentFolderID = FileUtil.generateUniqueContentFolderID();
	    }

	    String authorUrl = authoringFullService.getToolAuthorUrl(toolID, toolContentID, contentFolderID);
	    if (authorUrl != null) {
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("authorURL", authorUrl);
		// return the generated values
		responseJSON.put(AttributeNames.PARAM_TOOL_CONTENT_ID, toolContentID);
		responseJSON.put(AttributeNames.PARAM_CONTENT_FOLDER_ID, contentFolderID);
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(responseJSON.toString());
	    }
	}
	return null;
    }

    /**
     * Creates a LD with the given activity and starts a lesson with default class and settings.
     */
    @SuppressWarnings("unchecked")
    public ActionForward createSingleActivityLesson(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	IAuthoringFullService authoringFullService = getAuthoringService();
	Long toolID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_ID);
	Long toolContentID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	Long learningLibraryID = WebUtil.readLongParam(request, AttributeNames.PARAM_LEARNING_LIBRARY_ID, true);
	String contentFolderID = request.getParameter(AttributeNames.PARAM_CONTENT_FOLDER_ID);
	Integer organisationID = WebUtil.readIntParam(request, AttributeNames.PARAM_ORGANISATION_ID, true);
	if (organisationID == null) {
	    // if organisation ID is not set explicitly, derived it from external course
	    String serverID = request.getParameter(LoginRequestDispatcher.PARAM_SERVER_ID);
	    String courseID = request.getParameter(LoginRequestDispatcher.PARAM_COURSE_ID);
	    ExtServer extServer = getIntegrationService().getExtServer(serverID);
	    ExtCourseClassMap orgMap = getIntegrationService().getExtCourseClassMap(extServer.getSid(), courseID);
	    organisationID = orgMap.getOrganisation().getOrganisationId();
	}
	Integer userID = getUserId();

	if (!getSecurityService().isGroupMonitor(organisationID, userID, "create single activity lesson", false)) {
	    response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is not a monitor in the organisation");
	    return null;
	}

	// get title from tool content
	IToolVO tool = getToolService().getToolByID(toolID);
	if (learningLibraryID == null) {
	    // if learning library ID is not set explicitly, derive it from tool
	    learningLibraryID = tool.getLearningLibraryId();
	}
	WebApplicationContext wac = WebApplicationContextUtils
		.getRequiredWebApplicationContext(getServlet().getServletContext());
	ToolContentManager toolManager = (ToolContentManager) wac.getBean(tool.getServiceName());
	String title = toolManager.getToolContentTitle(toolContentID);
	if (title == null || title.trim().length() == 0) {
	    title = getLearningDesignService().internationaliseActivityTitle(learningLibraryID);
	}
	// create the LD and put it in Run Sequences folder in the given organisation
	Long learningDesignID = authoringFullService.insertSingleActivityLearningDesign(title, toolID, toolContentID,
		learningLibraryID, contentFolderID, organisationID);
	if (learningDesignID != null) {
	    User user = (User) getUserManagementService().findById(User.class, userID);
	    Lesson lesson = getMonitoringService().initializeLessonWithoutLDcopy(title, "", learningDesignID, user,
		    null, false, false, false, false, true, true, false, false, true, null, null);
	    Organisation organisation = getMonitoringService().getOrganisation(organisationID);

	    List<User> staffList = new LinkedList<User>();
	    staffList.add(user);

	    // add organisation's learners as lesson participants
	    List<User> learnerList = new LinkedList<User>();
	    Vector<User> learnerVector = getUserManagementService().getUsersFromOrganisationByRole(organisationID,
		    Role.LEARNER, true);
	    learnerList.addAll(learnerVector);
	    getMonitoringService().createLessonClassForLesson(lesson.getLessonId(), organisation,
		    organisation.getName() + " learners", learnerList, organisation.getName() + " staff", staffList,
		    userID);

	    getMonitoringService().startLesson(lesson.getLessonId(), userID);

	    if (AuthoringAction.log.isDebugEnabled()) {
		AuthoringAction.log.debug("Created a single activity lesson with ID: " + lesson.getLessonId());
	    }

	    response.setContentType("text/plain;charset=utf-8");
	    response.getWriter().write(learningDesignID.toString());
	}

	return null;
    }

    /**
     * Stores Learning Desing created in Authoring.
     */
    public ActionForward saveLearningDesign(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws JSONException, UserException, WorkspaceFolderException, IOException,
	    ObjectExtractorException, ParseException {
	JSONObject ldJSON = new JSONObject(request.getParameter("ld"));

	LearningDesign learningDesign = getAuthoringService().saveLearningDesignDetails(ldJSON);

	JSONObject responseJSON = new JSONObject();
	if (learningDesign != null) {
	    Long learningDesignID = learningDesign.getLearningDesignId();
	    if (learningDesignID != null) {
		responseJSON.put("learningDesignID", learningDesignID);

		Gson gson = new GsonBuilder().create();
		Vector<ValidationErrorDTO> validationDTOs = getAuthoringService()
			.validateLearningDesign(learningDesignID);
		String validationJSON = gson.toJson(validationDTOs);
		responseJSON.put("validation", new JSONArray(validationJSON));
	    }
	}

	response.setContentType("application/json;charset=utf-8");
	response.getWriter().write(responseJSON.toString());
	return null;
    }

    /**
     * Gets a list of recently used Learning Designs for currently logged in user.
     */
    public ActionForward getLearningDesignAccess(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	Integer userId = getUserId();

	List<LearningDesignAccess> accessList = getAuthoringService().updateLearningDesignAccessByUser(userId);
	accessList = accessList.subList(0,
		Math.min(accessList.size(), AuthoringAction.LEARNING_DESIGN_ACCESS_ENTRIES_LIMIT - 1));
	Gson gson = new GsonBuilder().create();

	response.setContentType("application/json;charset=utf-8");
	response.getWriter().write(gson.toJson(accessList));
	return null;
    }

    /**
     * Stores the binary code of an Learning Design thumbnail, created in Authoring.
     *
     * @throws IOException
     */
    public ActionForward saveLearningDesignImage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	Long learningDesignID = WebUtil.readLongParam(request, AttributeNames.PARAM_LEARNINGDESIGN_ID);
	String image = request.getParameter("image");
	boolean saveSuccesful = AuthoringAction.saveLearningDesignImage(learningDesignID, image);
	if (!saveSuccesful) {
	    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	return null;
    }

    /**
     * Updates an existing activity coordinates.
     * It is run when SVG gets recreated in Monitoring or Add Lesson dialog
     * and activities need to be rearranged as one of them is a branching designed in the old Flash Authoring.
     */
    public ActionForward saveActivityCoordinates(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	try {
	    JSONObject activityJSON = new JSONObject(request.getParameter("activity"));
	    Activity activity = getMonitoringService().getActivityById(activityJSON.getLong("activityID"));
	    activity.setXcoord(activityJSON.optInt("xCoord"));
	    activity.setYcoord(activityJSON.optInt("yCoord"));
	    if (activity.isBranchingActivity()) {
		BranchingActivity branchingActivity = (BranchingActivity) activity;
		branchingActivity.setStartXcoord(activityJSON.getInt("startXCoord"));
		branchingActivity.setEndXcoord(activityJSON.getInt("endXCoord"));
		branchingActivity.setStartYcoord(activityJSON.getInt("startYCoord"));
		branchingActivity.setEndYcoord(activityJSON.getInt("endYCoord"));
	    }
	    getUserManagementService().save(activity);
	} catch (Exception e) {
	    log.error("Exception while saving activity coordinates", e);
	    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	return null;
    }

    /**
     * Stores the binary code of an Learning Design thumbnail, created in Authoring.
     */
    private static boolean saveLearningDesignImage(long learningDesignID, String image) {
	if (StringUtils.isBlank(image)) {
	    AuthoringAction.log.error("No SVG code to save for LD: " + learningDesignID);
	    return false;
	}

	// prepare the dir and the file
	File thumbnailDir = new File(ILearningDesignService.LD_SVG_TOP_DIR);
	String thumbnailSubdir = String.valueOf(learningDesignID);
	if (thumbnailSubdir.length() % 2 == 1) {
	    thumbnailSubdir = "0" + thumbnailSubdir;
	}
	for (int charIndex = 0; charIndex < thumbnailSubdir.length(); charIndex += 2) {
	    thumbnailDir = new File(thumbnailDir,
		    "" + thumbnailSubdir.charAt(charIndex) + thumbnailSubdir.charAt(charIndex + 1));
	}
	thumbnailDir.mkdirs();

	String absoluteFilePath = FileUtil.getFullPath(thumbnailDir.getAbsolutePath(), learningDesignID + ".svg");

	// write out the content
	try (FileOutputStream fos = new FileOutputStream(absoluteFilePath);
		Writer writer = new OutputStreamWriter(fos, "UTF8")) {
	    // encoding is important, especially for Raphael-generated SVGs
	    writer.write(image);
	} catch (IOException e) {
	    AuthoringAction.log.error("Error while writing SVG thumbnail of LD " + learningDesignID + ".", e);
	    return false;
	}
	return true;
    }

    private IMonitoringService getMonitoringService() {
	if (AuthoringAction.monitoringService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(getServlet().getServletContext());
	    AuthoringAction.monitoringService = (IMonitoringService) ctx.getBean("monitoringService");
	}
	return AuthoringAction.monitoringService;
    }

    private IUserManagementService getUserManagementService() {
	if (AuthoringAction.userManagementService == null) {
	    WebApplicationContext wac = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(getServlet().getServletContext());
	    AuthoringAction.userManagementService = (IUserManagementService) wac
		    .getBean(CentralConstants.USER_MANAGEMENT_SERVICE_BEAN_NAME);
	}
	return AuthoringAction.userManagementService;
    }

    public IAuthoringFullService getAuthoringService() {
	if (AuthoringAction.authoringFullService == null) {
	    WebApplicationContext wac = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(getServlet().getServletContext());
	    AuthoringAction.authoringFullService = (IAuthoringFullService) wac
		    .getBean(AuthoringConstants.AUTHORING_SERVICE_BEAN_NAME);
	}
	return AuthoringAction.authoringFullService;
    }

    public ILamsToolService getToolService() {
	if (AuthoringAction.toolService == null) {
	    WebApplicationContext wac = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(getServlet().getServletContext());
	    AuthoringAction.toolService = (ILamsToolService) wac.getBean(AuthoringConstants.TOOL_SERVICE_BEAN_NAME);
	}
	return AuthoringAction.toolService;
    }

    private ILearningDesignService getLearningDesignService() {
	if (AuthoringAction.learningDesignService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(getServlet().getServletContext());
	    AuthoringAction.learningDesignService = (ILearningDesignService) ctx.getBean("learningDesignService");
	}
	return AuthoringAction.learningDesignService;
    }

    private IIntegrationService getIntegrationService() {
	if (AuthoringAction.integrationService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(getServlet().getServletContext());
	    AuthoringAction.integrationService = (IIntegrationService) ctx.getBean("integrationService");
	}
	return AuthoringAction.integrationService;
    }

    private ISecurityService getSecurityService() {
	if (AuthoringAction.securityService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(getServlet().getServletContext());
	    AuthoringAction.securityService = (ISecurityService) ctx.getBean("securityService");
	}
	return AuthoringAction.securityService;
    }
}