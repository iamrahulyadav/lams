<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="tags-lams" prefix="lams" %>
<%@ taglib uri="tags-bean" prefix="bean" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-logic" prefix="logic" %>

	<script language="JavaScript" type="text/javascript" src="includes/javascript/prototype.js"></script>
	<script type="text/javascript" language="javascript">
		var i = Math.round(1000*Math.random());
		function getContent(){
			i++;
			var url = "index.do?unique="+i+"&state=archived";
			var params = "";
			var myAjax = new Ajax.Updater(
				"courselist",
				url,
				{
					method: 'get',
					parameters: params
				});
		}

		function findNextDiv(node)
		 {
		 	 var nd = node.nextSibling;
		 	  while(nd.nodeName.toUpperCase()!="DIV"){
		 	   	nd=nd.nextSibling;
		 	  }  
		 	   return nd
		 }
		 
		function toggle(node)
		{
			// Unfold the branch if it isn't visible
			var nextDiv = findNextDiv(node);
			if (nextDiv.style.display == 'none')
			{
				// Change the image (if there is an image)
				if (node.childNodes.length > 0)
				{
					if (node.childNodes.item(0).nodeName == "IMG")
					{
						node.childNodes.item(0).src = "images/tree_open.gif";
					}
				}
		
				nextDiv.style.display = 'block';
			}
			// Collapse the branch if it IS visible
			else
			{
				// Change the image (if there is an image)
				if (node.childNodes.length > 0)
				{
					if (node.childNodes.item(0).nodeName == "IMG")
					{
						node.childNodes.item(0).src = "images/tree_closed.gif";
					}
				}
		
				nextDiv.style.display = 'none';
			}
		
		}
		
		getContent();
	</script>

<h2><fmt:message key="index.myprofile" /></h2>

<table border="0">
	<col align="left" /><col align="left" />
	<tr>
		<td><fmt:message key="label.name" />: 
		</td>
		<td><bean:write name="fullName" />
		</td>
	</tr>
	<tr>
		<td><fmt:message key="label.email" />: 
		</td>
		<td><bean:write name="email" />
		</td>
	</tr>
	<tr>
		<td><fmt:message key="label.portrait.current" />:
		</td>
		<td><logic:notEqual name="portraitUuid" value="0">
				<img src="/lams/download/?uuid=<bean:write name="portraitUuid" />&preferDownload=false" />
			</logic:notEqual>
			<logic:equal name="portraitUuid" value="0">
				<i><fmt:message key="msg.portrait.none" /></i>
			</logic:equal>
		</td>
	</tr>
</table>

<p><a href="profile.do?method=edit"><fmt:message key="title.profile.edit.screen" /></a><br />
<a href="password.do"><fmt:message key="title.password.change.screen" /></a><br />
<a href="portrait.do"><fmt:message key="title.portrait.change.screen" /></a><br />
</p>

<h2><fmt:message key="title.archived.groups" /></h2>

<div id="courselist" align="center">
	<img src="images/loading.gif" /> <font color="gray" size="4"><fmt:message key="msg.loading"/></font>
</div>

<script type="text/javascript" language="javascript">
	getContent();
</script>
