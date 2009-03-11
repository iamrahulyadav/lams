<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
		"http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/common/taglibs.jsp"%>
<lams:html>
<lams:head>
	<lams:css style="core" />
	<style type="text/css">
		a {
			margin: 10px 2px 0px 0px;
			float: right;
		}
		
		body {
			width: 750px;
		}
		
		table#nominationTable td {
			maring: 0px;
			padding: 0px;
		}
		
		table#nominationTable td.FCKcell {
			padding: 10px 0px 0px 5px;
		}
		
		img.clearEntry {
			maring: 0px;
			padding: 0px;
			cursor: pointer;
		}
	</style>
	
	<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery-latest.pack.js"></script>
  	<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.form.js"></script>
  	<script type="text/javascript">
  		function createQuestion(){
  			prepareFormData();
  			$('#pedagogicalPlannerForm').ajaxSubmit({
  				url: "<c:url value='/pedagogicalPlanner.do?dispatch=createPedagogicalPlannerQuestion' />",
  				success: function(responseText){
  					$('#body').html(responseText.substring(responseText.search(/<body/i)));
  				}
  			});
  		}
  		
  		function prepareFormData(){
			//FCKeditor content is not submitted when sending by jQuery; we need to do this
			var content = FCKeditorAPI.GetInstance('instructions').GetXHTML();
			document.getElementById("instructions").value=content;
			
			var nominationIndex = 0;
			do{
				var nomination = document.getElementById("nomination["+nominationIndex+"]");
				if (nomination!=null){
					var content = FCKeditorAPI.GetInstance("nomination["+nominationIndex+"]").GetXHTML();
					nomination.value=content;
					nominationIndex++;
				}
			} while (nomination!=null);
		}
		
		function clearEntry(nominationIndex){
			FCKeditorAPI.GetInstance("nomination["+nominationIndex+"]").SetHTML("");
		}
  	</script>
</lams:head>
<body id="body">
	<%@ include file="/common/messages.jsp"%>
	<html:form action="/pedagogicalPlanner.do?dispatch=saveOrUpdatePedagogicalPlannerForm" styleId="pedagogicalPlannerForm" method="post">
		<html:hidden property="toolContentID" />
		<html:hidden property="valid" styleId="valid" />
		<html:hidden property="callID" styleId="callID" />
		<html:hidden property="activityOrderNumber" styleId="activityOrderNumber" />
		
		<c:set var="formBean" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />
		<h4 class="space-left"><fmt:message key="label.authoring.instructions"/></h4>
		<lams:FCKEditor id="instructions"
			value="${formBean.instructions}"
			contentFolderID="${formBean.contentFolderID}"
               toolbarSet="Custom-Pedplanner" height="150px"
               width="750px" displayExpanded="false">
		</lams:FCKEditor>
		
		<c:if test="${formBean.nominationCount ne 0}">
			<h4 class="space-left space-top"><fmt:message key="label.vote.nominations"/></h4>
			<table id="nominationTable" cellpadding="0" cellspacing="0">
				<c:forEach var="nominationIndex"  begin="1" end="${formBean.nominationCount}" >
					<tr>
						<td class="FCKcell">
							<lams:FCKEditor id="nomination[${nominationIndex-1}]"
								value="${formBean.nominationList[nominationIndex-1]}"
								contentFolderID="${formBean.contentFolderID}"
				                toolbarSet="Custom-Pedplanner" height="150px"
				                width="705px" displayExpanded="false">
							</lams:FCKEditor>
						</td>
						<td>
							<img class="clearEntry" src="<lams:LAMSURL/>images/icons/cross.png"
								title="<fmt:message key="msg.planner.clear.entry" />"
								onclick="javascript:clearEntry(${nominationIndex-1})" />
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</html:form>
	<a class="button" href="javascript:createQuestion();"><fmt:message key="label.add.new.nomination" /></a>
</body>
</lams:html>