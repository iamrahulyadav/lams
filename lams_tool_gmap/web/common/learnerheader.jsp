<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>
<c:set var="lrnForm" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />

<lams:head>  
	<title>
		<fmt:message key="activity.title" />
	</title>
	<link href="${tool}pages/learning/gmap_style.css" rel="stylesheet" type="text/css">
	<lams:css/>
	
	<link href="${lams}css/fckeditor_style.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
	<script type="text/javascript" src="${lams}fckeditor/fckeditor.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/fckcontroller.js"></script>
	<script type="text/javascript" src="${tool}includes/javascript/mapFunctionsLearning.js"></script>

	<%@ include file="/includes/jsp/mapFunctions.jsp"%>

	<script type="text/javascript">
	<!--
		var YELLOW_MARKER_ICON = "${tool}/images/yellow_Marker.png";
		var BLUE_MARKER_ICON = "${tool}/images/blue_Marker.png";
		var LIGHTBLUE_MARKER_ICON = "${tool}/images/paleblue_Marker.png";
		var RED_MARKER_ICON = "${tool}/images/red_Marker.png";
		var TREE_CLOSED_ICON = "${tool}/images/tree_closed.gif";
		var TREE_OPEN_ICON = "${tool}/images/tree_open.gif";
		
		var errorMissingTitle = '<fmt:message key="error.missingMarkerTitle"/>';
		var makerLimitMsg = '<fmt:message key="label.learner.markerLimitReached"/>'
		var confirmDelete = '<fmt:message key="label.authoring.basic.confirmDelete"/>';
		
		var sessionName = '${gmapSessionDTO.sessionName}';
		
		var map;
		var markers;
		var users;
		var geocoder = null;
		var currUser;
		var currUserId;
		var userMarkerCount =0;
		var limitMarkers = ${gmapDTO.limitMarkers};
		var markerLimit = 0;
		var markerLimit = ${gmapDTO.maxMarkers};
		function initLearnerGmap()
		{
			if (GBrowserIsCompatible()) 
			{
				//map = new GMap2(document.getElementById("map_canvas"), { size: new GSize(640,320) } );
				map = new GMap2(document.getElementById("map_canvas"), { size: new GSize(500,320) } );
				markers = new Array();
		    	users = new Array();
		    	geocoder = new GClientGeocoder();

				map.setCenter(new GLatLng('${gmapDTO.mapCenterLatitude}', '${gmapDTO.mapCenterLongitude}' ));
				map.setZoom(${gmapDTO.mapZoom});

				currUser = '${gmapUserDTO.firstName} ${gmapUserDTO.lastName}';
				currUserId = '${gmapUserDTO.uid}'
				
				// Set up map controls
				map.addControl(new GMapTypeControl());
				<c:if test="${gmapDTO.allowZoom == true}">map.addControl(new GLargeMapControl());</c:if>
				<c:if test="${gmapDTO.allowTerrain == true}">map.addMapType(G_PHYSICAL_MAP);</c:if>
				<c:if test="${gmapDTO.allowSatellite == false}">map.removeMapType(G_SATELLITE_MAP);</c:if>
				<c:if test="${gmapDTO.allowHybrid == false}">map.removeMapType(G_HYBRID_MAP);</c:if>
				map.setMapType(${gmapDTO.mapType});
		    	
		    	
		    	
		    	addUserToList('0','<fmt:message key="label.authoring.basic.authored"></fmt:message>' );
		    	<c:forEach var="user" items="${gmapSessionDTO.userDTOs}">
		    		addUserToList('${user.uid}','${user.firstName} ${user.lastName}');
		    	</c:forEach>
		    	
		    	<c:forEach var="marker" items="${gmapSessionDTO.markerDTOs}">
					<c:choose>
					<c:when test="${marker.createdBy.loginName == gmapUserDTO.loginName && marker.isAuthored == false}">
					    addMarker(new GLatLng('${marker.latitude}', '${marker.longitude}' ),'${marker.infoWindowMessage}', '${marker.title}', '${marker.uid}', true, ${gmapDTO.allowEditMarkers}, '${marker.createdBy.firstName} ${marker.createdBy.lastName}', '${marker.createdBy.uid}');	
						userMarkerCount ++;
					</c:when>
					<c:otherwise>
						<c:choose>
						<c:when  test="${marker.isAuthored}">
						addMarker(new GLatLng('${marker.latitude}', '${marker.longitude}' ), '${marker.infoWindowMessage}', '${marker.title}', '${marker.uid}', true, false, '<fmt:message key="label.authoring.basic.authored"></fmt:message>', '0' );
						</c:when>
						<c:when  test="${gmapDTO.allowShowAllMarkers}">
						addMarker(new GLatLng('${marker.latitude}', '${marker.longitude}' ), '${marker.infoWindowMessage}', '${marker.title}', '${marker.uid}', true, false, '${marker.createdBy.firstName} ${marker.createdBy.lastName}', '${marker.createdBy.uid}' );
						</c:when>
						</c:choose>
					</c:otherwise>
					</c:choose>
				</c:forEach>

		    	refreshSideBar(sessionName);
		    }
		}
	//-->
	</script>
</lams:head>
