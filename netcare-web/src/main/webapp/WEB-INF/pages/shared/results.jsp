<%--

    Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>

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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="hp" tagdir="/WEB-INF/tags" %>

<hp:view>
	<hp:viewHeader>
		<script src="<c:url value="/js/highstock-1.2.5/highstock.js" />" type="text/javascript"></script>

    <!-- Include printing css -->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/print-results.css" />" media="print" />
		
		<hp:templates />
		<script type="text/javascript">
			$(function() {

        <sec:authorize access="hasRole('CARE_ACTOR')">
            var name = '<c:out value="${sessionScope.currentPatient.name}" />';
            var crn = '<c:out value="${sessionScope.currentPatient.civicRegistrationNumber}" />';
        </sec:authorize>

        <sec:authorize access="hasRole('PATIENT')">
          var name = '<sec:authentication property="principal.name" />';
          var crn = '<sec:authentication property="principal.civicRegistrationNumber" />';
        </sec:authorize>

				var params = {
					activityId : <c:out value="${param.activity}" />,
                    crn : crn,
                    name : name
				};
				
				NC_MODULE.RESULTS.init(params);
				
			});
		</script>
	</hp:viewHeader>
	<c:url value="/netcare/shared/select-results" var="backToUrl" />
	<hp:viewBody backTitle="Tillbaka" backUrl="${backToUrl}" backToWhat="till Resultat" printable="true">
		<div id="activities">
		</div>
	</hp:viewBody>
</hp:view>