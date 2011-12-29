<%--

    Copyright (C) 2011,2012 Callista Enterprise AB <info@callistaenterprise.se>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>NetCare</title>
	
	<!-- Include Twitter bootstrap -->
	<c:url value="/css/bootstrap.min.css" var="bootstrapCss" scope="page" />
	<link rel="stylesheet" href="${bootstrapCss}" />
	
	<!-- Include JQuery UI Css -->
	<c:url value="/css/ui-lightness/jquery-ui-1.8.16.custom.css" var="jqueryUiCss" scope="page" />
	<link href="${jqueryUiCss}" type="text/css" rel="stylesheet" />
	
	<!-- Include our CSS -->
	<c:url value="/css/netcare.css" var="netcareCss" scope="page" />
	<link href="${netcareCss}" type="text/css" rel="stylesheet" />
	
	<style type="text/css">
		/* Override some defaults */
      html, body {
        background-color: #eee;
      }
      body {
        padding-top: 40px; /* 40px to make the container go all the way to the bottom of the topbar */
      }
      .container > footer p {
        text-align: center; /* center align it with the container */
      }
      .container {
        width: 940px; /* downsize our container to make the content feel a bit tighter and more cohesive. NOTE: this removes two full columns from the grid, meaning you only go to 14 columns and not 16. */
      }

      /* The white background content wrapper */
      .content {
        background-color: #fff;
        padding: 20px;
        margin: 0 -20px; /* negative indent the amount of the padding to maintain the grid system */
        -webkit-border-radius: 6px 6px 6px 6px;
           -moz-border-radius: 6px 6px 6px 6px;
                border-radius: 6px 6px 6px 6px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.15);
           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.15);
                box-shadow: 0 1px 2px rgba(0,0,0,.15);
      }

      /* Page header tweaks */
      .page-header {
        background-color: #f5f5f5;
        padding: 20px 20px 10px;
        margin: -20px -20px 20px;
      }

      /* Styles you shouldn't keep as they are for displaying this base example only */
      .content .span10,
      .content .span4 {
        min-height: 500px;
      }
      /* Give a quick and non-cross-browser friendly divider */
      .content .span4 {
        margin-left: 0;
        padding-left: 19px;
        border-left: 1px solid #eee;
      }
	</style>
	
	<!-- Json -->
	<c:url value="/js/json2.js" var="json" scope="page"/>
	<script type="text/javascript" src="${json}"></script>
	
	<!-- Include JQuery -->
	<c:url value="/js/jquery-1.6.2.min.js" var="jqueryJs" scope="page" />
	<script type="text/javascript" src="${jqueryJs}"></script>
	
	<!-- Include JQuery UI Widgets -->
	<c:url value="/js/jquery-ui-1.8.16.custom.min.js" var="jqueryUiJs" scope="page" />
	<script type="text/javascript" src="${jqueryUiJs}"></script>
	
	<!-- Include Twitter bootstrap js -->
	<c:url value="/js/bootstrap-alerts.js" var="bootstrapAlerts" scope="page" />
	<c:url value="/js/bootstrap-buttons.js" var="bootstrapButtons" scope="page" />
	<c:url value="/js/bootstrap-dropdown.js" var="bootstrapDropdown" scope="page" />
	<c:url value="/js/bootstrap-modal.js" var="bootstrapModal" scope="page" />
	<c:url value="/js/bootstrap-popover.js" var="bootstrapPopover" scope="page" />
	<c:url value="/js/bootstrap-scrollspy.js" var="bootstrapScrollspy" scope="page" />
	<c:url value="/js/bootstrap-tabs.js" var="bootstrapTabs" scope="page" />
	<c:url value="/js/bootstrap-twipsy.js" var="bootstrapTwipsy" scope="page" />
	
	<script type="text/javascript" src="${bootstrapAlerts}"></script>
	<script type="text/javascript" src="${bootstrapButtons}"></script>
	<script type="text/javascript" src="${bootstrapDropdown}"></script>
	<script type="text/javascript" src="${bootstrapModal}"></script>
	<script type="text/javascript" src="${bootstrapTwipsy}"></script>
	<script type="text/javascript" src="${bootstrapPopover}"></script>
	<script type="text/javascript" src="${bootstrapScrollspy}"></script>
	<script type="text/javascript" src="${bootstrapTabs}"></script>
	
	<!-- Include NETCARE javascripts  -->
	<c:url value="/js/netcare.js" var="netcareJs" scope="page" />
	<script type="text/javascript" src="<c:url value='/js/netcare.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/Util.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/Support.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/Patient.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/HealthPlan.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/ActivityTypes.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/netcare/PatientSchema.js' />"></script>
	
	<jsp:doBody />
</head>
