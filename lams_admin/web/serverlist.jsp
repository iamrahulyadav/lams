<%@ include file="/taglibs.jsp"%>

<p><a href="<lams:LAMSURL/>/admin/sysadminstart.do" class="btn btn-default"><fmt:message key="sysadmin.maintain" /></a></p>

<table class="table table-striped">
	<tr>
		<th><fmt:message key="sysadmin.serverid" /></th>
		<th><fmt:message key="sysadmin.serverkey" /></th>
		<th><fmt:message key="sysadmin.servername" /></th>
		<th><fmt:message key="sysadmin.serverdesc" /></th>
		<th><fmt:message key="sysadmin.prefix" /></th>
		<th><fmt:message key="sysadmin.disabled" /></th>
		<th><fmt:message key="sysadmin.organisation" /></th>
		<th><fmt:message key="admin.actions"/></th>
	</tr>
	<c:forEach items="${servers}" var="serverOrgMap">
	<tr>
		<td><c:out value="${serverOrgMap.serverid}" /></td>
		<td><c:out value="${serverOrgMap.serverkey}" /></td>
		<td><c:out value="${serverOrgMap.servername}" /></td>
		<td><c:out value="${serverOrgMap.serverdesc}" /></td>
		<td><c:out value="${serverOrgMap.prefix}" /></td>
		<td>
			<c:choose>
			<c:when test="${serverOrgMap.disabled}" >
				<fmt:message key="label.yes" />
			</c:when>
			<c:otherwise>
				<fmt:message key="label.no" />
			</c:otherwise>
			</c:choose>
		</td>
		<td><c:out value="${serverOrgMap.organisation.name}" /></td>
		<td>
			<a id="edit_<c:out value='${serverOrgMap.serverid}'/>" href="servermaintain.do?method=edit&sid=<c:out value='${serverOrgMap.sid}' />"><fmt:message key="admin.edit" /></a>
			&nbsp;
			<c:choose>
				<c:when test="${serverOrgMap.disabled}">
					<a id="enable_<c:out value='${serverOrgMap.serverid}'/>" href="servermaintain.do?method=enable&sid=<c:out value='${serverOrgMap.sid}' />"><fmt:message key="admin.enable" /></a>
				</c:when>
				<c:otherwise>
					<a id="disable_<c:out value='${serverOrgMap.serverid}'/>" href="servermaintain.do?method=disable&sid=<c:out value='${serverOrgMap.sid}' />"><fmt:message key="admin.disable" /></a>
				</c:otherwise>
			</c:choose>
			&nbsp;
			<a id="delete_<c:out value='${serverOrgMap.serverid}'/>" href="servermaintain.do?method=delete&sid=<c:out value='${serverOrgMap.sid}' />"><fmt:message key="admin.delete" /></a>
		</td>
	</tr>
	</c:forEach>
</table>
<p>${fn:length(servers)}&nbsp;<fmt:message key="sysadmin.integrated.servers" /></p>

<input class="btn btn-default pull-right" name="addnewserver" type="button" value="<fmt:message key='sysadmin.server.add' />" onClick="javascript:document.location='servermaintain.do?method=edit'" />

