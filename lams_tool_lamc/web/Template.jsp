<!--
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
USA

http://www.gnu.org/licenses/gpl.txt
-->
<%@ taglib uri="tags-tiles" prefix="tiles" %>
<%@ taglib uri="tags-html" prefix="html" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-c" prefix="c" %>
<%@ taglib uri="tags-fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale = "true">
    
    <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
      <table width="900" border="0" cellspacing="0" cellpadding="0" align="center"> 
	        <!-- header -->
	        <c:set var="pageheader" scope="session"><tiles:getAsString name="pageHeader"/></c:set>
			<tiles:insert attribute="header" />
	        <!-- end of header -->
	        <!-- main content -->
	        <tiles:insert attribute="content" />
	        <!--end of main content-->
	        <!--footer-->
	        <tiles:insert attribute="footer" />
	        <!-- end of footer -->
      </table>
    </body>
</html:html>
