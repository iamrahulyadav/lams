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

package org.lamsfoundation.lams.admin.web.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.admin.service.AdminServiceProxy;
import org.lamsfoundation.lams.admin.web.form.UserRolesForm;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author jliew
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
@Controller
public class UserRolesSaveController {

    private static Logger log = Logger.getLogger(UserRolesSaveController.class);
    private static IUserManagementService service;
    private static MessageService messageService;
    private static List<Role> rolelist;

    @Autowired
    private WebApplicationContext applicationContext;

    @RequestMapping(path = "/userrolessave", method = RequestMethod.POST)
    public String execute(@ModelAttribute UserRolesForm userRolesForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	service = AdminServiceProxy.getService(applicationContext.getServletContext());
	messageService = AdminServiceProxy.getMessageService(applicationContext.getServletContext());
	if (rolelist == null) {
	    rolelist = service.findAll(Role.class);
	    Collections.sort(rolelist);
	}

	Errors errors = null;
	Integer orgId = userRolesForm.getOrgId();
	Integer userId = userRolesForm.getUserId();
	String[] roles = userRolesForm.getRoles();

	request.setAttribute("org", orgId);

	if (request.getAttribute("CANCEL") != null) {
	    return "forward:/usermanage.do";
	}

	log.debug("userId: " + userId + ", orgId: " + orgId + " will have " + roles.length + " roles");
	Organisation org = (Organisation) service.findById(Organisation.class, orgId);
	User user = (User) service.findById(User.class, userId);

	// user must have at least 1 role
	if (roles.length < 1) {
	    errors.reject("roles", messageService.getMessage("error.roles.empty"));
	    request.setAttribute("rolelist",
		    service.filterRoles(rolelist, request.isUserInRole(Role.SYSADMIN), org.getOrganisationType()));
	    request.setAttribute("login", user.getLogin());
	    request.setAttribute("fullName", user.getFullName());
	    return "forward:/userroles.do";
	}

	service.setRolesForUserOrganisation(user, orgId, Arrays.asList(roles));

	return "forward:/usermanage.do";
    }

}
