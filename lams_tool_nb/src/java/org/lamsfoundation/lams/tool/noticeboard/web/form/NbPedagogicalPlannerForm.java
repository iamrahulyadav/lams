/****************************************************************
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */


package org.lamsfoundation.lams.tool.noticeboard.web.form;

import org.lamsfoundation.lams.tool.noticeboard.NoticeboardContent;
import org.lamsfoundation.lams.planner.PedagogicalPlannerActivityForm;

/**
 *
 *
 */
public class NbPedagogicalPlannerForm extends PedagogicalPlannerActivityForm {
    private String basicContent;
    private String contentFolderID;

    public String getContentFolderID() {
	return contentFolderID;
    }

    public void setContentFolderID(String contentFolderID) {
	this.contentFolderID = contentFolderID;
    }

    public String getBasicContent() {
	return basicContent;
    }

    public void setBasicContent(String instructions) {
	basicContent = instructions;
    }

    public void fillForm(NoticeboardContent noticeboard) {
	if (noticeboard != null) {
	    String content = noticeboard.getContent();
	    setBasicContent(content);
	    setToolContentID(noticeboard.getNbContentId());
	}
    }
}