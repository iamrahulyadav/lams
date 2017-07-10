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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

package org.lamsfoundation.lams.learning.kumalive.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.lamsfoundation.lams.dao.hibernate.LAMSBaseDAO;
import org.lamsfoundation.lams.learning.kumalive.dao.IKumaliveDAO;
import org.lamsfoundation.lams.learning.kumalive.model.Kumalive;
import org.springframework.stereotype.Repository;

@Repository
public class KumaliveDAO extends LAMSBaseDAO implements IKumaliveDAO {
    private static final String FIND_BY_ORGANISATION = "FROM " + Kumalive.class.getName()
	    + " AS k WHERE k.organisation.organisationId = ? AND k.finished = 0";

    private static final String SAVE_SCORE_SQL = "INSERT INTO lams_kumalive_score VALUES (NULL, ?, ?, ?)";

    @Override
    @SuppressWarnings("unchecked")
    public Kumalive findByOrganisationId(Integer organisationId) {
	List<Kumalive> result = (List<Kumalive>) doFind(FIND_BY_ORGANISATION, organisationId);
	return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public void saveScore(Long kumaliveId, Integer userId, Short score) {
	Query query = getSession().createSQLQuery(SAVE_SCORE_SQL);
	query.setLong(0, kumaliveId);
	query.setInteger(1, userId);
	query.setShort(2, score);
	query.executeUpdate();
    }
}