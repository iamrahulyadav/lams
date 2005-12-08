<%@ taglib uri="/WEB-INF/struts-html-el.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic-el.tld" prefix="logic-el" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

		<html:form  action="/learning?method=displayMc&validate=false" method="POST" target="_self">
				<table align=center bgcolor="#FFFFFF">
					  <tr>
					  	<td align=left class="input" valign=top bgColor="#333366" colspan=2> 
						  	<font size=2 color="#FFFFFF"> <b>  <bean:message key="label.assessment"/> </b> </font>
					  	</td>
					  </tr>
				
			 		<c:if test="${sessionScope.isRetries == 'true'}"> 		
						  <tr>
						  	<td align=center class="input" valign=top colspan=2> 
							  	<font size=3> <b>  <bean:message key="label.individual.results.withRetries"/> </b> </font>
						  	</td>
						  </tr>
  					</c:if> 			

					<c:if test="${sessionScope.isRetries != 'true'}"> 							  
						  <tr>
						  	<td align=center class="input" valign=top colspan=2> 
							  	<font size=3> <b>  <bean:message key="label.individual.results.withoutRetries"/> </b> </font>
						  	</td>
						  </tr>
					</c:if> 			


					  <tr>
					  	<td align=right class="input" valign=top colspan=2> 
						  	<font size=2 color="#000000"> <b>  <bean:message key="label.mark"/>  
						  	<c:out value="${sessionScope.learnerMark}"/>  &nbsp
							<bean:message key="label.outof"/> &nbsp
						  	<c:out value="${sessionScope.totalQuestionCount}"/>
						  	</b> </font>
					  	</td>
					  </tr>	

					<tr>
						<td align=right class="input" valign=top colspan=2> 
							<hr>
						</td> 
					</tr>
					
					<c:if test="${sessionScope.userPassed != 'true'}">
						 <tr>
						  	<td align=left class="input" valign=top colspan=2> 
							  	<font size=2 color="#FF0000"> <b>  <bean:message key="label.mustGet"/> &nbsp
							  	<c:out value="${sessionScope.learnerMarkAtLeast}"/>  &nbsp
								<bean:message key="label.outof"/> &nbsp
							  	<c:out value="${sessionScope.totalQuestionCount}"/>
								<bean:message key="label.toFinish"/>						  	
							  	</b> </font>
						  	</td>
						  </tr>	
  					</c:if> 			
					
					
  		  	 		<c:set var="mainQueIndex" scope="session" value="0"/>
					<c:forEach var="questionEntry" items="${sessionScope.mapQuestionContentLearner}">
					<c:set var="mainQueIndex" scope="session" value="${mainQueIndex +1}"/>
						  <tr>
						  	<td align=left class="input" valign=top bgColor="#999966" colspan=2> 
							  	<font color="#FFFFFF"> 
							  		<c:out value="${questionEntry.value}"/> 
							  	</font> 
						  	</td>
						  </tr>

								  								  
						  <tr>						 
							<td align=left>
							<table align=left>
			  		  	 		<c:set var="queIndex" scope="session" value="0"/>
								<c:forEach var="mainEntry" items="${sessionScope.mapGeneralOptionsContent}">
									<c:set var="queIndex" scope="session" value="${queIndex +1}"/>
										<c:if test="${sessionScope.mainQueIndex == sessionScope.queIndex}"> 		
									  		<c:forEach var="subEntry" items="${mainEntry.value}">
				  								<tr> 
													<td align=left class="input" valign=top> 
					   								    <img src="images/dot.jpg" align=left> &nbsp
														<font size=2 color="#669966">	<c:out value="${subEntry.value}"/> </font>					   								    
													</td> 
												</tr>	
											</c:forEach>

												<tr>												
												<td colspan=2 align=left class="input" valign=top> 
				   								    <bean:message key="label.you.answered"/>
												</td> 
												</tr>
												
												<tr>
													<td  align=left class="input" valign=top> 											
														<table align=left>
													  		<c:forEach var="subEntry" items="${mainEntry.value}">
						  											<c:forEach var="selectedMainEntry" items="${sessionScope.mapGeneralCheckedOptionsContent}">
																				<c:if test="${selectedMainEntry.key == sessionScope.queIndex}"> 		
																			  		<c:forEach var="selectedSubEntry" items="${selectedMainEntry.value}">
																						<c:if test="${subEntry.key == selectedSubEntry.key}"> 		
																								<tr> 
																									<td align=left class="input" valign=top> 
																										<b> <c:out value="${subEntry.value}"/> </b>
																									</td> 
																								</tr>
										  												</c:if> 			
																					</c:forEach>																						
							  													</c:if> 			
																	</c:forEach>		
																</tr>																			
												  			</c:forEach>											
														</table>
													</td>

													<td  align=right class="input" valign=top> 											
														<c:forEach var="mainEntry" items="${sessionScope.mapLearnerAssessmentResults}">
																<c:if test="${mainEntry.key == sessionScope.queIndex}"> 		
																	<c:if test="${mainEntry.value == 'true'}"> 		
																		<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackCorrect}">
																			<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																				    <img src="images/tick.gif" align=right width=20 height=20>
																			</c:if> 																																				
																		</c:forEach>											
																	</c:if> 														
																	
																	<c:if test="${mainEntry.value == 'false'}"> 		
																		<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackIncorrect}">
																			<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																				    <img src="images/cross.gif" align=right width=20 height=20>
																			</c:if> 																																				
																		</c:forEach>											
																	</c:if> 														
																</c:if> 																		
														</c:forEach>		
													</td>
												</tr>

												<tr>
												<td bgcolor="#CCCC99" colspan=2 align=left class="input" valign=top> 											
													<c:forEach var="mainEntry" items="${sessionScope.mapLearnerAssessmentResults}">
															<c:if test="${mainEntry.key == sessionScope.queIndex}"> 		
																<c:if test="${mainEntry.value == 'true'}"> 		
																	<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackCorrect}">
																		<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																				<c:out value="${feedbackEntry.value}"/>
																		</c:if> 																																				
																	</c:forEach>											
																</c:if> 														
																
																<c:if test="${mainEntry.value == 'false'}"> 		
																	<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackIncorrect}">
																		<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																				<c:out value="${feedbackEntry.value}"/>
																		</c:if> 																																				
																	</c:forEach>											
																</c:if> 														
															</c:if> 																		
													</c:forEach>											
												</td>
												</tr>
											
										</c:if> 			
								</c:forEach>
							</table>
							</td>
						</tr>
					</c:forEach>

			  	   	<tr> 
				 		<td colspan=2 class="input" valign=top> 
				 		&nbsp
				 		</td>
			  	   </tr>

			  	   

			 		<c:if test="${sessionScope.isRetries == 'true'}"> 					  	   
		  	   		  <tr>
					  	<td colspan=2 align=center class="input" valign=top> 
				  			<html:submit property="redoQuestions" styleClass="a.button">
								<bean:message key="label.redo.questions"/>
							</html:submit>	 		
		       
							<c:if test="${sessionScope.userPassed == 'true'}">
						  	   <html:submit property="learnerFinished" styleClass="a.button">
									<bean:message key="label.finished"/>
							   </html:submit>
					  	   </c:if>

	   						<html:submit property="viewSummary" styleClass="a.button">
								<bean:message key="label.view.summary"/>
							</html:submit>	 				 		  					
					  	 </td>
					  </tr>
					</c:if> 																		

					<c:if test="${sessionScope.isRetries != 'true'}"> 							  
		  	   		  <tr>
		  	   		    <td colspan=2 align=right class="input" valign=top>
			  	   		  	<c:if test="${sessionScope.userPassed == 'true'}">
						  	   <html:submit property="learnerFinished" styleClass="a.button">
											<bean:message key="label.finished"/>
							   </html:submit>
				  	   		</c:if>

	   						<html:submit property="viewSummary" styleClass="a.button">
								<bean:message key="label.view.summary"/>
							</html:submit>	 				 		  					
					  	 </td>
					  </tr>
					</c:if> 																		
					
				</table>
	</html:form>

