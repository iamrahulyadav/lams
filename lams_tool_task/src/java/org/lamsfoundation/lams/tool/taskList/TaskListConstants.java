/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
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
 * ****************************************************************
 */
/* $Id$ */
package org.lamsfoundation.lams.tool.taskList;

public class TaskListConstants {
	public static final String TOOL_SIGNATURE = "latask10";
	public static final String RESOURCE_SERVICE = "lataskTaskListService";
	public static final String TOOL_CONTENT_HANDLER_NAME = "lataskTaskListToolContentHandler";

	public static final int COMPLETED = 1;
	
	//for action forward name
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String DEFINE_LATER = "definelater";
	
	//for parameters' name
	public static final String PARAM_TOOL_CONTENT_ID = "toolContentID";
	public static final String PARAM_TOOL_SESSION_ID = "toolSessionID"; 
	public static final String PARAM_FILE_VERSION_ID = "fileVersionId";
	public static final String PARAM_FILE_UUID = "fileUuid";
	public static final String PARAM_ITEM_INDEX = "itemIndex";
	public static final String PARAM_SEQUENCE_ID = "sequenceId";
	public static final String PARAM_RESOURCE_ITEM_UID = "itemUid";
	public static final String PARAM_CURRENT_INSTRUCTION_INDEX = "insIdx";
	public static final String PARAM_RUN_OFFLINE = "runOffline";
	public static final String PARAM_OPEN_URL_POPUP = "popupUrl";
	public static final String PARAM_TITLE = "title";
	
	//for request attribute name
	public static final String ATTR_TOOL_CONTENT_ID = "toolContentID";
	public static final String ATTR_TOOL_SESSION_ID = "toolSessionID"; 
	public static final String ATTR_RESOURCE_ITEM_LIST = "taskListList";
	public static final String ATTR_CONDITION_LIST = "conditionList";
	public static final String ATT_ATTACHMENT_LIST = "instructionAttachmentList";
	public static final String ATTR_DELETED_RESOURCE_ITEM_LIST = "deleteTaskListList";
	public static final String ATTR_DELETED_CONDITION_LIST = "deleteConditionList";
	public static final String ATTR_DELETED_ATTACHMENT_LIST = "deletedAttachmmentList";
	public static final String ATTR_DELETED_RESOURCE_ITEM_ATTACHMENT_LIST =  "deletedItemAttachmmentList";;
	public static final String ATT_LEARNING_OBJECT = "cpPackage";
	public static final String ATTR_RESOURCE_REVIEW_URL = "taskListItemReviewUrl";
	public static final String ATTR_RESOURCE = "taskList";
	public static final String ATTR_RUN_AUTO = "runAuto";
	public static final String ATTR_RESOURCE_ITEM_UID = "itemUid";
	public static final String ATTR_NEXT_ACTIVITY_URL = "nextActivityUrl";
	public static final String ATTR_SUMMARY_LIST = "summaryList";
	public static final String ATTR_ITEM_SUMMARY_LIST_LIST = "itemSummaryListList";
	public static final String ATTR_USER_LIST = "userList";
	public static final String ATTR_FINISH_LOCK = "finishedLock";
	public static final String ATTR_SESSION_MAP_ID = "sessionMapID";
	public static final String ATTR_RESOURCE_FORM = "taskListForm";
	public static final String ATTR_TASKLIST_FORM = "taskListItemForm";
	public static final String ATTR_FILE_TYPE_FLAG = "fileTypeFlag";
	public static final String ATTR_TITLE = "title";
	public static final String ATTR_USER_FINISHED = "userFinished";
	public static final String ATTR_USER_VERIFIED_BY_MONITOR = "userVerifiedByMonitor";
	public static final String ATTR_TASK_LIST_ITEM = "taskListItem";
	public static final String ATTR_TASK_LIST_ITEM_TITLE = "taskListItemTitle";
	public static final String ATTR_TASK_LIST_ITEM_DESCRIPTION = "taskListItemDescription";
	public static final String ATTR_TASK_LIST_ITEM_ATTACHMENT_LIST = "taskListItemAttachmentList";
	public static final String ATTR_TASK_LIST_ITEM_COMMENT_LIST = "taskListItemCommentList";
	public static final String ATTR_USER_UID = "userUid";
	public static final String ATTR_TASK_LIST_ITEM_UID = "taskListItemUid";
	public static final String ATTR_USER_LOGIN = "userLogin";
	public static final String ATTR_TASK_LIST_ITEM_DTOS = "itemDTOs";
	
	//error message keys
	public static final String ERROR_MSG_TITLE_BLANK = "error.resource.item.title.blank";
	public static final String ERROR_MSG_NAME_BLANK = "error.condition.name.blank";
	public static final String ERROR_MSG_NAME_CONTAINS_WRONG_SYMBOL = "error.condition.name.contains.wrong.symbol";
	public static final String ERROR_MSG_NO_TASK_LIST_ITEMS = "error.condition.no.tasklistitems.selected";
	public static final String ERROR_MSG_NAME_DUPLICATED = "error.condition.duplicated.name";
	public static final String ERROR_MSG_UPLOAD_FAILED = "error.upload.failed";
	
	public static final String PAGE_EDITABLE = "isPageEditable";
	public static final String MODE_AUTHOR_SESSION = "author_session";
	public static final String ATTR_REFLECTION_ON = "reflectOn";
	public static final String ATTR_REFLECTION_INSTRUCTION = "reflectInstructions";
	public static final String ATTR_REFLECTION_ENTRY = "reflectEntry";
	public static final String ATTR_REFLECT_LIST = "reflectList";

}
