<%@ taglib uri="tags-html-el" prefix="html" %>
<%@ taglib uri="tags-bean" prefix="bean" %>
<%@ taglib uri="tags-logic-el" prefix="logic-el" %>
<%@ taglib uri="tags-c" prefix="c" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

				<table class="forms" align=center>
					<tr>
	 				 	<td class="formlabel" align=right valign=top> 
		 				 	<font size=2>
								<b> <bean:message key="label.question"/>: </b>
							</font>
						</td>
						
						<td colspan=3 valign=top>
							<font size=2>
				  				<input type="text" name="selectedQuestion" value="<c:out value="${sessionScope.selectedQuestion}"/>"   
						  			size="50" maxlength="255"> 
					  		</font>
					  	</td>
					</tr>
					
					<tr>
	 				 	<td colspan=4 align=center valign=top>				
	 				 	&nbsp				
					  	</td>
					</tr>

					<tr> <td colspan=4 align=center valign=top width="100%">
					
					<table align="center" border="1">
					     <tr>
							  	<td bgcolor="#A8C7DE" colspan=4 class="input" valign=top align=left>
							  	 	<font size=2> <b> <bean:message key="label.mc.options"/> </b> </font>
							  	</td>
						 </tr>					
					
						<c:set var="optionIndex" scope="session" value="1"/>
			  	 		<c:set var="selectedOptionFound" scope="request" value="0"/>
						<c:forEach var="optionEntry" items="${sessionScope.mapOptionsContent}">
							  <c:if test="${optionEntry.key == 1}"> 			
								  <tr>
									  	<td bgcolor="#EEEEEE" class="input" valign=top>  <font size=2> <b> <c:out value="Candidate Answer ${optionIndex}"/> :  </b>  </font>

										<font size=2>
								  			<input type="text" name="optionContent<c:out value="${optionIndex}"/>" value="<c:out value="${optionEntry.value}"/>"   
									  		size="50" maxlength="255">
									  	</font>
							
			  				  	 		<c:set var="selectedOptionFound" scope="request" value="0"/>
			  							<c:forEach var="selectedOptionEntry" items="${sessionScope.mapSelectedOptions}">
				  							<font size=2>								  	
												  <c:if test="${selectedOptionEntry.value == optionEntry.value}"> 			
							  					
														  	<select name="checkBoxSelected<c:out value="${optionIndex}"/>">
																<option value="Incorrect" > <font size=2> Incorrect </font> </option>
																<option value="Correct" SELECTED>  <font size=2> Correct </font></option>
															</select>
												
							  				  	 		<c:set var="selectedOptionFound" scope="request" value="1"/>
							 					</c:if> 			
							 				</font>
										</c:forEach>
										
									  <c:if test="${selectedOptionFound == 0}"> 			
										  <font size=2>								  	
													  	<select name="checkBoxSelected<c:out value="${optionIndex}"/>">
															<option value="Incorrect" SELECTED> <font size=2> Incorrect </font> </option>
															<option value="Correct">   <font size=2> Correct  </font> </option>
														</select>
											</font>													
					 					</c:if> 			
										</td>
	
										<td bgcolor="#EEEEEE" class="input" valign=top>
											<font size=2>								  	
		       								    <img src="images/delete.gif" align=left onclick="javascript:deleteOption(1,'removeOption');">
		       								 </font>
									  	</td>
							 </tr>
							</c:if> 			
					  		<c:if test="${optionEntry.key > 1}"> 			
								<c:set var="optionIndex" scope="session" value="${optionIndex +1}"/>
								  <tr>
								  	<td bgcolor="#EEEEEE" class="input" valign=top> <font size=2> <b> <c:out value="Candidate Answer ${optionIndex}"/> : </b></font>

									<font size=2>								  	
							  			<input type="text" name="optionContent<c:out value="${optionIndex}"/>" value="<c:out value="${optionEntry.value}"/>"
								  		size="50" maxlength="255">
							  		</font>

			  				  	 		<c:set var="selectedOptionFound" scope="request" value="0"/>
			  							<c:forEach var="selectedOptionEntry" items="${sessionScope.mapSelectedOptions}">
				  							<font size=2>								  	
											  <c:if test="${selectedOptionEntry.value == optionEntry.value}"> 			
													  	<select name="checkBoxSelected<c:out value="${optionIndex}"/>">
															<option value="Incorrect" > <font size=2> Incorrect </font> </option>
															<option value="Correct" SELECTED> <font size=2>	  Correct  </font> </option>
														</select>
						  				  	 		<c:set var="selectedOptionFound" scope="request" value="1"/>
							 					</c:if> 			
							 				</font>
										</c:forEach>
										
									  <c:if test="${selectedOptionFound == 0}"> 			

												  	<select name="checkBoxSelected<c:out value="${optionIndex}"/>">
														<option value="Incorrect" SELECTED> <font size=2> Incorrect  </font> </option>
														<option value="Correct">   <font size=2> Correct </font>   </option>
													</select>
					 					</c:if> 			
										</td>
	
										<td bgcolor="#EEEEEE" class="input" valign=top>
											<font size=2>
		       								    <img src="images/delete.gif" align=left onclick="javascript:deleteOption(<c:out value="${optionIndex}"/>,'removeOption');">
	       								    </font>
    								  	</td>
								  </tr>
							</c:if> 			
						  		<input type=hidden name="checkBoxSelected<c:out value="${optionIndex}"/>">
						</c:forEach>
						
						 <tr>
							  	<td bgcolor="#EEEEEE" colspan=4 class="input" valign=top align=right>
				  					<font size=2>
										<html:submit onclick="javascript:submitMethod('addOption');" styleClass="button">
												<bean:message key="label.add.option"/>
										</html:submit>	 				 		  										  		
									</font>									
							  	</td>
						 </tr>							
					</table> </td> </tr>	
					
					<html:hidden property="deletableOptionIndex"/>							
					
					<tr>
	 				 	<td colspan=4 align=center valign=top>								
							&nbsp
					  	</td>
					</tr>
					<tr>
	 				 	<td colspan=4 align=center valign=top>								
							&nbsp
					  	</td>
					</tr>

			
	 				 <tr>
	 				 <td valign=top> </td>
	 				 <td class="input" align=left colspan=3 valign=top>
						<font size=2>
							<html:submit onclick="javascript:submitMethod('doneOptions');" styleClass="button">
									<bean:message key="button.done"/>
							</html:submit>	 				 		  										  		
						</font>							
	 			  	</td>
	 				</tr>
				</table> 	 
