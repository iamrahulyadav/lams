/**
 * @author ozgurd
 * 
 * Created on 12/10/2005
 * 
 * initializes the tool's authoring mode  
 */

/**
 * Tool path The URL path for the tool should be <lamsroot>/tool/$TOOL_SIG.
 * 
 * CONTENT_LOCKED refers to content being in use or not: Any students answered that content?
 * For future CONTENT_LOCKED ->CONTENT_IN_USE 
 * 
 * McStarterAction loads the default content and initializes the presentation Map
 * Requests can come either from authoring envuironment or from the monitoring environment for Edit Activity screen
 * 
 * Check McUtils.createAuthoringUser again User Management Service is ready
 * 
 * */

/**
 *
 * Tool Content:
 *
 * While tool's manage their own content, the LAMS core and the tools work together to create and use the content. 
 * The tool content id (toolContentId) is the key by which the tool and the LAMS core discuss data - 
 * it is generated by the LAMS core and supplied to the tool whenever content needs to be stored. 
 * The LAMS core will refer to the tool content id whenever the content needs to be used. 
 * Tool content will be covered in more detail in following sections.
 *
 * Each tool will have one piece of content that is the default content. 
 * The tool content id for this content is created as part of the installation process. 
 * Whenever a tool is asked for some tool content that does not exist, it should supply the default tool content. 
 * This will allow the system to render the normal screen, albeit with useless information, rather than crashing. 
*/

/**
*
* Authoring URL: 
*
* The tool must supply an authoring module, which will be called to create new content or edit existing content. It will be called by an authoring URL using the following format: ?????
* The initial data displayed on the authoring screen for a new tool content id may be the default tool content.
*
* Authoring UI data consists of general Activity data fields and the Tool specific data fields.
* The authoring interface will have three tabs. The mandatory (and suggested) fields are given. Each tool will have its own fields which it will add on any of the three tabs, as appropriate to the tabs' function.
*
* Basic: Displays the basic set of fields that are needed for the tool, and it could be expected that a new LAMS user would use. Mandatory fields: Title, Instructions.
* Advanced: Displays the extra fields that would be used by experienced LAMS users. Optional fields: Lock On Finish, Make Responses Anonymous
* Instructions: Displays the "instructions" fields for teachers. Mandatory fields: Online instructions, Offline instructions, Document upload.
* The "Define Later" and "Run Offline" options are set on the Flash authoring part, and not on the tool's authoring screens.
*
* Preview The tool must be able to show the specified content as if it was running in a lesson. It will be the learner url with tool access mode set to ToolAccessMode.AUTHOR.
* Export The tool must be able to export its tool content for part of the overall learning design export.
*
* The format of the serialization for export is XML. Tool will define extra namespace inside the <Content> element to add a new data element (type). Inside the data element, it can further define more structures and types as it seems fit.
* The data elements must be "version" aware. The data elements must be "type" aware if they are to be shared between Tools.
*
* LAMS Xpress (Ernie, could you put something in here. You explain it better than I do!)
* Data Exchange At present, there is no data exchange format between tools. Therefore if a tool needs to work with another tool, they will need to be combined in a new tool. We plan to have a data exchange method in a future version of LAMS.
*
*/

/*
 * check back McUtils.configureContentRepository(request);
 */

package org.lamsfoundation.lams.tool.mc.web;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McApplicationException;
import org.lamsfoundation.lams.tool.mc.McComparator;
import org.lamsfoundation.lams.tool.mc.McContent;
import org.lamsfoundation.lams.tool.mc.McOptsContent;
import org.lamsfoundation.lams.tool.mc.McQueContent;
import org.lamsfoundation.lams.tool.mc.McUtils;
import org.lamsfoundation.lams.tool.mc.service.IMcService;
import org.lamsfoundation.lams.tool.mc.service.McServiceProxy;


public class McStarterAction extends Action implements McAppConstants {
	static Logger logger = Logger.getLogger(McAppConstants.class.getName());
	
	/**
	 * A Map  data structure is used to present the UI.
	 * It is fetched by subsequent Action classes to manipulate its content and gets parsed in the presentation layer for display.
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
  								throws IOException, ServletException, McApplicationException {
	
		Map mapQuestionsContent= new TreeMap(new McComparator());
		Map mapOptionsContent= new TreeMap(new McComparator());
		Map mapDefaultOptionsContent= new TreeMap(new McComparator());
		
		McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;
		mcAuthoringForm.resetRadioBoxes();
		
		/**
		 * retrive the service
		 */
		IMcService mcService = McUtils.getToolService(request);
		logger.debug("retrieving mcService from session: " + mcService);
		if (mcService == null)
		{
			mcService = McServiceProxy.getMcService(getServlet().getServletContext());
		    logger.debug("retrieving mcService from proxy: " + mcService);
		    request.getSession().setAttribute(TOOL_SERVICE, mcService);		
		}
		
		/**
		 * retrieve the default content id based on tool signature
		 */
		long contentId=0;
		try
		{
			logger.debug("attempt retrieving tool with signatute : " + MY_SIGNATURE);
			contentId=mcService.getToolDefaultContentIdBySignature(MY_SIGNATURE);
			logger.debug("retrieved tool default contentId: " + contentId);
			if (contentId == 0) 
			{
				logger.debug("default content id has not been setup");
				request.setAttribute(USER_EXCEPTION_DEFAULTCONTENT_NOTSETUP, new Boolean(true));
				persistError(request,"error.defaultContent.notSetup");
				return (mapping.findForward(LOAD_QUESTIONS));	
			}
		}
		catch(Exception e)
		{
			logger.debug("error getting the default content id: " + e.getMessage());
			request.setAttribute(USER_EXCEPTION_DEFAULTCONTENT_NOTSETUP, new Boolean(true));
			persistError(request,"error.defaultContent.notSetup");
			return (mapping.findForward(LOAD_QUESTIONS));
		}

		
		/**
		 * retrieve uid of the content based on default content id determined above
		 */
		long contentUID=0;
		try
		{
			logger.debug("retrieve uid of the content based on default content id determined above: " + contentId);
			McContent mcContent=mcService.retrieveMc(new Long(contentId));
			if (mcContent == null)
			{
				logger.debug("Exception occured: No default content");
	    		request.setAttribute(USER_EXCEPTION_DEFAULTCONTENT_NOTSETUP, new Boolean(true));
	    		persistError(request,"error.defaultContent.notSetup");
				return (mapping.findForward(LOAD_QUESTIONS));
			}
			logger.debug("using mcContent: " + mcContent);
			logger.debug("using mcContent uid: " + mcContent.getUid());
			contentUID=mcContent.getUid().longValue();
		}
		catch(Exception e)
		{
			logger.debug("Exception occured: No default question content");
			request.setAttribute(USER_EXCEPTION_DEFAULTCONTENT_NOTSETUP, new Boolean(true));
    		persistError(request,"error.defaultContent.notSetup");
			return (mapping.findForward(LOAD_QUESTIONS));
		}
		
		
		/**
		 * retrieve uid of the default question content
		 */
		long queContentUID=0;
		try
		{
			logger.debug("retrieve the default question content based on default content UID: " + contentId);
			McQueContent mcQueContent=mcService.getToolDefaultQuestionContent(contentUID);
			logger.debug("using mcQueContent: " + mcQueContent);
			if (mcQueContent == null)
			{
				logger.debug("Exception occured: No default question content");
	    		request.setAttribute(USER_EXCEPTION_DEFAULTQUESTIONCONTENT_NOT_AVAILABLE, new Boolean(true));
				persistError(request,"error.defaultQuestionContent.notAvailable");
				return (mapping.findForward(LOAD_QUESTIONS));
			}
			logger.debug("using mcQueContent uid: " + mcQueContent.getUid());
			queContentUID=mcQueContent.getUid().longValue();
		}
		catch(Exception e)
		{
			logger.debug("Exception occured: No default question content");
    		request.setAttribute(USER_EXCEPTION_DEFAULTQUESTIONCONTENT_NOT_AVAILABLE, new Boolean(true));
			persistError(request,"error.defaultQuestionContent.notAvailable");
			return (mapping.findForward(LOAD_QUESTIONS));
		}
		
		
		/**
		 * retrieve default options content
		 */
		try
		{
			logger.debug("retrieve the default options content based on default question content UID: " + queContentUID);
			List list=mcService.findMcOptionsContentByQueId(new Long(queContentUID));
			logger.debug("using options list: " + list);
			if (list == null)
			{
				logger.debug("Exception occured: No default options content");
	    		request.setAttribute(USER_EXCEPTION_DEFAULTOPTIONSCONTENT_NOT_AVAILABLE, new Boolean(true));
				persistError(request,"error.defaultOptionsContent.notAvailable");
				return (mapping.findForward(LOAD_QUESTIONS));
			}
		}
		catch(Exception e)
		{
			logger.debug("Exception occured: No default options content");
    		request.setAttribute(USER_EXCEPTION_DEFAULTOPTIONSCONTENT_NOT_AVAILABLE, new Boolean(true));
			persistError(request,"error.defaultOptionsContent.notAvailable");
			return (mapping.findForward(LOAD_QUESTIONS));
		}
		
	
		
		/**
	     * mark the http session as an authoring activity 
	     */
	    request.getSession().setAttribute(TARGET_MODE,TARGET_MODE_AUTHORING);
	    
	    /**
	     * define tab controllers for jsp
	     */
	    request.getSession().setAttribute(CHOICE_TYPE_BASIC,CHOICE_TYPE_BASIC);
	    request.getSession().setAttribute(CHOICE_TYPE_ADVANCED,CHOICE_TYPE_ADVANCED);
	    request.getSession().setAttribute(CHOICE_TYPE_INSTRUCTIONS,CHOICE_TYPE_INSTRUCTIONS);
	
	    
	    logger.debug("will render authoring screen");
	    String strToolContentId="";
	    strToolContentId=request.getParameter(TOOL_CONTENT_ID);
	    
	    /**
	     * Process incoming tool content id
	     * Either exists or not exists in the db yet, a toolContentId must be passed to the tool from the container 
	     */
	    long toolContentId=0;
    	try
		{
	    	toolContentId=new Long(strToolContentId).longValue();
	    	logger.debug("passed TOOL_CONTENT_ID : " + toolContentId);
	    	request.getSession().setAttribute(TOOL_CONTENT_ID, new Long(strToolContentId));
    	}
    	catch(NumberFormatException e)
		{
	    	persistError(request,"error.numberFormatException");
			request.setAttribute(USER_EXCEPTION_NUMBERFORMAT, new Boolean(true));
			logger.debug("forwarding to: " + LOAD_QUESTIONS);
			return (mapping.findForward(LOAD_QUESTIONS));
		}
	
    	/**
		 * find out if the passed tool content id exists in the db 
		 * present user either a first timer screen with default content data or fetch the existing content.
		 * 
		 * if the toolcontentid does not exist in the db, create the default Map,
		 * there is no need to check if the content is in use in this case.
		 * It is always unlocked -> not in use since it is the default content.
		*/
		if (!existsContent(toolContentId, request))
		{
			logger.debug("getting default content");
			contentId=mcService.getToolDefaultContentIdBySignature(MY_SIGNATURE);
			McContent mcContent=mcService.retrieveMc(new Long(contentId));
			logger.debug("mcContent:" + mcContent);
			
			if (mcContent == null)
			{
				logger.debug("Exception occured: No default content");
				request.setAttribute(USER_EXCEPTION_DEFAULTCONTENT_NOT_AVAILABLE, new Boolean(true));
				persistError(request,"error.defaultContent.notAvailable");
				return (mapping.findForward(LOAD_QUESTIONS));
			}
			
			request.getSession().setAttribute(TITLE,mcContent.getTitle());
			request.getSession().setAttribute(INSTRUCTIONS,mcContent.getInstructions());
			request.getSession().setAttribute(QUESTIONS_SEQUENCED,new Boolean(mcContent.isQuestionsSequenced()));
			request.getSession().setAttribute(USERNAME_VISIBLE,new Boolean(mcContent.isUsernameVisible()));
			request.getSession().setAttribute(CREATED_BY, new Long(mcContent.getCreatedBy()));
			request.getSession().setAttribute(MONITORING_REPORT_TITLE,mcContent.getMonitoringReportTitle());
			request.getSession().setAttribute(REPORT_TITLE,mcContent.getReportTitle());
			request.getSession().setAttribute(RUN_OFFLINE, new Boolean(mcContent.isRunOffline()));
			request.getSession().setAttribute(DEFINE_LATER, new Boolean(mcContent.isDefineLater()));
			request.getSession().setAttribute(SYNCH_IN_MONITOR, new Boolean(mcContent.isSynchInMonitor()));
			request.getSession().setAttribute(OFFLINE_INSTRUCTIONS,mcContent.getOfflineInstructions());
			request.getSession().setAttribute(ONLINE_INSTRUCTIONS,mcContent.getOnlineInstructions());
			request.getSession().setAttribute(END_LEARNING_MESSAGE,mcContent.getEndLearningMessage());
			request.getSession().setAttribute(CONTENT_IN_USE, new Boolean(mcContent.isContentInUse()));
			request.getSession().setAttribute(RETRIES, new Boolean(mcContent.isRetries()));
			request.getSession().setAttribute(PASSMARK, mcContent.getPassMark()); //Integer
			request.getSession().setAttribute(SHOW_FEEDBACK, new Boolean(mcContent.isShowFeedback())); 
			
			
			McUtils.setDefaultSessionAttributes(request, mcContent, mcAuthoringForm);
			logger.debug("RICHTEXT_TITLE:" + request.getSession().getAttribute(RICHTEXT_TITLE));
			logger.debug("getting default content");
			/**
			 * this is a new content creation, the content must not be in use.
			 * relevant attribute: CONTENT_IN_USE  
			 */
			request.getSession().setAttribute(CONTENT_IN_USE, new Boolean(false));
		    logger.debug("CONTENT_IN_USE: " + request.getSession().getAttribute(CONTENT_IN_USE));
		    
		    mcAuthoringForm.setUsernameVisible(OFF);
		    mcAuthoringForm.setQuestionsSequenced(OFF);
			mcAuthoringForm.setSynchInMonitor(OFF);
			mcAuthoringForm.setRetries(OFF);
			mcAuthoringForm.setShowFeedback(OFF);
			
			/** collect options for the default question content into a Map*/
			McQueContent mcQueContent=mcService.getToolDefaultQuestionContent(mcContent.getUid().longValue());
			System.out.print("mcQueContent:" + mcQueContent);
			if (mcQueContent == null)
			{
				logger.debug("Exception occured: No default question content");
				request.setAttribute(USER_EXCEPTION_DEFAULTQUESTIONCONTENT_NOT_AVAILABLE, new Boolean(true));
				persistError(request,"error.defaultQuestionContent.notAvailable");
				return (mapping.findForward(LOAD_QUESTIONS));
			}
			/**
        	 * display a single sample question
        	 */
    		request.getSession().setAttribute(DEFAULT_QUESTION_CONTENT, mcQueContent.getQuestion());
    		mapQuestionsContent.put(new Long(1).toString(), mcQueContent.getQuestion());
    		request.getSession().setAttribute(MAP_QUESTIONS_CONTENT, mapQuestionsContent);
    		logger.debug("starter initialized the Questions Map: " + request.getSession().getAttribute(MAP_QUESTIONS_CONTENT));
    		
			
			/** hold all he options for this question*/
			List list=mcService.findMcOptionsContentByQueId(mcQueContent.getUid());
	    	logger.debug("options list:" + list);

	    	Iterator listIterator=list.iterator();
	    	Long mapIndex=new Long(1);
	    	while (listIterator.hasNext())
	    	{
	    		McOptsContent mcOptsContent=(McOptsContent)listIterator.next();
	    		logger.debug("option text:" + mcOptsContent.getMcQueOptionText());
	    		mapOptionsContent.put(mapIndex.toString(),mcOptsContent.getMcQueOptionText());
	    		mapIndex=new Long(mapIndex.longValue()+1);
	    	}
	    	request.getSession().setAttribute(MAP_OPTIONS_CONTENT, mapOptionsContent);
	    	mapDefaultOptionsContent=mapOptionsContent;
	    	request.getSession().setAttribute(MAP_DEFAULTOPTIONS_CONTENT, mapDefaultOptionsContent);
			logger.debug("starter initialized the Options Map: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
			logger.debug("starter initialized the Default Options Map: " + request.getSession().getAttribute(MAP_DEFAULTOPTIONS_CONTENT));
		}
		else
		{
			logger.debug("getting existing content with id:" + toolContentId);
		}
    	
	
	mcAuthoringForm.resetUserAction();
	return (mapping.findForward(LOAD_QUESTIONS));
  } 
	

	/**
	 * existsContent(long toolContentId)
	 * @param long toolContentId
	 * @return boolean
	 * determine whether a specific toolContentId exists in the db
	 */
	protected boolean existsContent(long toolContentId, HttpServletRequest request)
	{
		/**
		 * retrive the service
		 */
		IMcService mcService =McUtils.getToolService(request);
		McContent mcContent=mcService.retrieveMc(new Long(toolContentId));
	    if (mcContent == null) 
	    	return false;
	    
		return true;	
	}
	
	/**
	 * find out if the content is locked or not. If it is a locked content, the author can not modify it.
	 * The idea of content being locked is, once any one learner starts using a particular content
	 * that content should become unmodifiable. 
	 * @param mcContent
	 * @return
	 */
	protected boolean isContentInUse(McContent mcContent)
	{
		logger.debug("is content inuse: " + mcContent.isContentInUse());
		return  mcContent.isContentInUse();
	}
	
	
	
	/**
     * persists error messages to request scope
     * @param request
     * @param message
     */
	public void persistError(HttpServletRequest request, String message)
	{
		ActionMessages errors= new ActionMessages();
		errors.add(Globals.ERROR_KEY, new ActionMessage(message));
		logger.debug("add " + message +"  to ActionMessages:");
		saveErrors(request,errors);	    	    
	}
}  
