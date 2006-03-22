/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
package org.lamsfoundation.lams.learningdesign.dao;

import org.lamsfoundation.lams.dao.IBaseDAO;
import org.lamsfoundation.lams.learningdesign.Grouping;

/**
 * @author Manpreet Minhas
 */
public interface IGroupingDAO extends IBaseDAO {
	
	/**
	 * Get a grouping record from the database. Must return a "real" grouping object, not
	 * a CGLIB proxy object.
	 * 
	 * @param groupingID
	 * @return Grouping populated Grouping object
	 */
	public Grouping getGroupingById(Long groupingID);	
	
	/**
	 * Must return a "real" grouping object, not a CGLIB proxy object.
	 * 
	 * @param groupingUIID
	 * @return Grouping populated Grouping object
	 */
	public Grouping getGroupingByUIID(Integer groupingUIID);

}
