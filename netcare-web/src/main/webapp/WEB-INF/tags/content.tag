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
<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>


<div class="span7">
	<div id="pageMessages">
		<c:if test="${not empty requestScope.result and empty requestScope.hideMessages}">
			<%-- Display any messages that we have --%>
			<c:forEach items="${requestScope.result.errorMessages}" var="error">
				<netcare:message type="error" message="${error.message}" />
			</c:forEach>

			<c:forEach items="${requestScope.result.warningMessages}" var="warning">
				<netcare:message type="warning" message="${warning.message}" />
			</c:forEach>

			<c:forEach items="${requestScope.result.infoMessages}" var="info">
				<netcare:message type="success" message="${info.message}" />
			</c:forEach>
		</c:if>
	</div>
	<jsp:doBody />
</div>