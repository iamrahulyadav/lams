/****************************************************************
 * Copyright (C) 2014 LAMS Foundation (http://lamsfoundation.org)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */ 

package org.lamsfoundation.lams.pages.tool.forum.util;

public class ForumConstants {
	
	// Constants
	
	// Default values (English AU)
	
	public static final String FORUM_TITLE = "Forum";
	public static final String FORUM_INSTRUCTIONS = "<div>Instructions</div>";
	
	public static final String FORUM_DEFAULT_TOPIC = "Topic Heading";

	// Warning messages
	
	public static final String FORUM_WARNING_REPLY_LIMITS_TXT = "Please add at least 1 topic when"; // When no topic and reply limits are set
	
	// Learner's textarea id for CkEditor
	public static final String FORUM_LEARNER_CKEDITOR_ID = "message.body";
	public static final CharSequence FORUM_FORM_INFO_MIN_CHAR = "The minimum number of characters for your response is";
	public static final String FORUM_LEARNER_MIN_CHAR_WARNING = "characters more to proceed";;
	
}
