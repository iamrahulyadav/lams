<%@ include file="/common/taglibs.jsp"%>
<c:set var="formBean" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />
<c:set var="sessionMapID" value="${formBean.sessionMapID}" />
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
<c:set var="confidenceLevelsActivities" value="${sessionMap.confidenceLevelsActivities}" />

<script lang="javascript">
	$(document).ready(function(){
		$("#display-confidence-levels-activities").click(function() {
			$("#confidence-levels-activity").prop("disabled", !$(this).is(':checked'));
		});
		<c:if test="${formBean.scratchie.confidenceLevelsActivityUiid == null}">$("#confidence-levels-activity").prop('disabled','disabled'); </c:if>
		
		
		$("#overwrite-preset-marks").click(function() {
			$("#preset-marks").prop("disabled", !$(this).is(':checked'));
		});
		<c:choose>
			<c:when test="${sessionMap.isPresetMarksOverwritten}">
				$("#collapseAdvanced").collapse("show");
			</c:when>
			<c:otherwise>
				$("#preset-marks").val("${sessionMap.defaultPresetMarks}"); 
				$("#preset-marks").prop('disabled','disabled');
			</c:otherwise>
		</c:choose>
		
	});
</script>

<lams:SimplePanel titleKey="label.scratchie.options">
	<c:if test="${sessionMap.isEnabledExtraPointOption}">
		<div class="checkbox">
			<label for="extraPoint">
				<html:checkbox property="scratchie.extraPoint" styleId="extraPoint"/>
				<fmt:message key="label.authoring.advanced.give.extra.point" />
			</label>
		</div>
	</c:if>
	
	<div class="checkbox">
		<label for="burning-questions-enabled">
			<html:checkbox property="scratchie.burningQuestionsEnabled" styleId="burning-questions-enabled"/>
			<fmt:message key="label.authoring.advanced.burning.questions" />
		</label>
	</div>
	
	<div class="checkbox">
		<label for="show-scratchies-in-results">
			<html:checkbox property="scratchie.showScrachiesInResults" styleId="show-scratchies-in-results"/>
			<fmt:message key="label.authoring.advanced.show.scratchies.in.results" />
		</label>
	</div>
	
	<div class="checkbox">
		<label for="shuffle-items">
			<html:checkbox property="scratchie.shuffleItems" styleId="shuffle-items"/>
			<fmt:message key="label.authoring.advanced.shuffle.items" />
		</label>
	</div>
	
	<div class="form-inline">
		<label for="time-limit">
			<fmt:message key="label.time.limit" />&nbsp;
			<html:text property="scratchie.timeLimit" size="3" styleId="time-limit" styleClass="form-control input-sm"/>
		</label>
	</div>
	
	<div class="checkbox">
		<c:if test="${confidenceLevelsActivities == null}">
			<div class="alert-warning" style="margin-bottom: 5px;">
				<fmt:message key="label.save.learning.design" />
			</div>
		</c:if>
		
		<c:choose>
			<c:when test="${fn:length(confidenceLevelsActivities) == 0}">
				<fmt:message key="label.no.confidence.levels.activities" />
			</c:when>
				
			<c:otherwise>
				<label for="display-confidence-levels-activities">
					<input type="checkbox" id="display-confidence-levels-activities"
							<c:if test="${formBean.scratchie.confidenceLevelsActivityUiid != null}">checked="true"</c:if>
					/>
				
					<fmt:message key="label.show.confidence.level" />&nbsp;
					<html:select property="scratchie.confidenceLevelsActivityUiid" styleClass="form-control input-sm" styleId="confidence-levels-activity">
						<c:forEach var="confidenceProvidingActivity" items="${confidenceLevelsActivities}">
							<html:option value="${confidenceProvidingActivity.activityUIID}">${confidenceProvidingActivity.title}</html:option>
						</c:forEach>
					</html:select>
				</label>
			</c:otherwise>
		</c:choose>
	</div>
</lams:SimplePanel>

<c:set var="adTitle"><fmt:message key="label.change.marking.allocation" /></c:set>
<lams:AdvancedAccordian title="${adTitle}">
	<div class="panel-body ">
		<div class="alert alert-warning">
			<fmt:message key="label.change.marking.allocation.warning" />
		</div>
	
		<div class="checkbox">
			<label for="overwrite-preset-marks">
				<input type="checkbox" id="overwrite-preset-marks"
					<c:if test="${sessionMap.isPresetMarksOverwritten}">checked="true"</c:if>
				/>
				<fmt:message key="label.modify.default.marking" />
			</label>
		</div>
		
		<div class="form-inline">
			<label for="preset-marks">
				<fmt:message key="admin.preset.marks" />&nbsp;
				<html:text styleId="preset-marks" property="scratchie.presetMarks" size="50" maxlength="255" styleClass="form-control form-control-inline"	/>
			</label>
		</div>
	</div>
</lams:AdvancedAccordian>

<lams:OutcomeAuthor toolContentId="${formBean.scratchie.contentId}" />

<lams:SimplePanel titleKey="label.activity.completion">
	<div class="checkbox">
		<label for="reflect-on">
			<html:checkbox property="scratchie.reflectOnActivity" styleId="reflect-on"/>
			<fmt:message key="label.authoring.advanced.reflectOnActivity" />
		</label>
	</div>
	
	<div class="form-group">
		<html:textarea property="scratchie.reflectInstructions"  styleClass="form-control" styleId="reflectInstructions" rows="3" />
	</div>
</lams:SimplePanel>

<script type="text/javascript">
	//automatically turn on refect option if there are text input in refect instruction area
	var ra = document.getElementById("reflectInstructions");
	var rao = document.getElementById("reflectOn");
	function turnOnRefect(){
		if(isEmpty(ra.value)){
		//turn off	
			rao.checked = false;
		}else{
		//turn on
			rao.checked = true;		
		}
	}
	ra.onkeyup=turnOnRefect;
	
	//disable #shuffle-items when burning questions option is ON
	$("#burning-questions-enabled").click(function() {
		var isBurningQuestionsOn = $("#burning-questions-enabled").is(":checked");
	
		$("#shuffle-items").prop("disabled", isBurningQuestionsOn);
		if (isBurningQuestionsOn) {
			$("#shuffle-items").prop("checked", false);
		}
	});
	
	<c:if test="${formBean.scratchie.burningQuestionsEnabled}">$("#shuffle-items").prop("disabled", true);</c:if>
	
</script>
