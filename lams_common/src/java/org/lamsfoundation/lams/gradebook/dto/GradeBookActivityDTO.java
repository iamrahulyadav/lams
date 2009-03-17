/****************************************************************
 * Copyright (C) 2008 LAMS Foundation (http://lamsfoundation.org)
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
 
/* $Id$ */ 
package org.lamsfoundation.lams.gradebook.dto; 

import java.util.ArrayList;
 
public class GradeBookActivityDTO extends GradeBookGridRow{
    long activityId;
    String activityTitle;
    //String toolString;
    String status;
    String output;
    //String instructions;
    String competences;
    //double timeTaken;
    Double mark;
    
    public GradeBookActivityDTO() {}
    
    @Override
    public ArrayList<String> toStringArray() {
	ArrayList<String> ret = new ArrayList<String>();
	
	ret.add("" + activityId);
	ret.add(activityTitle);
	ret.add(status);
	ret.add(output);
	ret.add(competences);
	
	if (mark != null) {
	    ret.add(mark.toString());
	} else {
	    ret.add("-"); 
	}
	
	
	return ret;
    }
    
    @Override
    public String getRowId() {
	return "" + activityId;
    }
    
    public int getId(){
	return new Long(activityId).intValue();
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public String getCompetences() {
        return competences;
    }

    public void setCompetences(String competences) {
        this.competences = competences;
    }
}
 