
<%@ page language="java"%>

<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-bean" prefix="bean" %>
<%@ taglib uri="tags-html" prefix="html" %>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-tiles" prefix="tiles" %>
<%@ taglib uri="tags-c" prefix="c" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
  <head>    
    <title>Files Submitted</title>
    <link href="includes/css/aqua.css" rel="stylesheet" type="text/css">
  </head>  
  <body>
		  <c:set var="details" value="${sessionScope.fileDetails}"/>
		  <c:set var="user" value="${sessionScope.user}" />
		  <c:set var="toolSessionID" value="${sessionScope.toolSessionID}" />		  
		  <b>Please assign a mark and a comment for the report by 
		  		 <c:out value="${user.login}" /> , <c:out value="${user.firstName}" />  <c:out value="${user.lastName}" /> 	
		  </b></p>
		 <table width="100%"  border="0" cellspacing="0" cellpadding="0">
			  <span><tr>	
					<td>File Path:</td><td><c:out value="${details.filePath}"  escapeXml="false"/></td>
				    </tr>
				    <tr>
					<td>File Description:</td><td> <c:out value="${details.fileDescription}"  escapeXml="false"/></td>
					</tr>
				    <tr>						
					<td>Date of Submission: </td><td><c:out value="${details.dateOfSubmission}" /></td>
					</tr>
		</table>
		<table class="forms">
					<tr><td colspan="2"><html:errors/></td></tr>
					<form name="commentForm" action="monitoring.do?method=updateMarks"  method="post">	
							 <input type="hidden" name="toolSessionID" value=<c:out value="${toolSessionID}"/> />						
							 <input type="hidden" name="reportID" value=<c:out value="${details.reportID}"/> />						
 							 <input type="hidden" name="userID" value=<c:out value="${user.userID}"/> />
 					<tr>						 
 				            <td class="formlabel">Marks:</td>
 				            <td class="formcontrol">
 				        		<input type="text" name="marks" value=<c:out value="${details.marks}"  escapeXml="false"/>>
 				        	</td>
 				    </tr>
 				    <tr>
 				    		<td class="formlabel">Comments:</td>
					        <td class="formcontrol">
					            <FCK:editor id="comments" 
					    			basePath="/lams/fckEditor/"
					    			height="150"    
					    			width="85%">
	    									<c:out value="${details.comments}" escapeXml="false"/>
								</FCK:editor>
  				            </td>
			        </tr>
					<tr>
						  <td class="formcontrol" colspan="2">
							<input type="submit" value="Update Marks" />
						  </td>
					</tr>
					</form>
				</span>
		</table>
  </body>
</html:html>

