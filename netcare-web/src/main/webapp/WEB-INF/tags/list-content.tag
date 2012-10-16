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
<%@ attribute name="titleCode" required="true" %>
<%@ attribute name="descriptionCode" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<section id="list-header">
	<h2><spring:message code="${titleCode}" /></h2>
	<p>
		<span class="label label-info"><spring:message code="label.information" /></span>
		<spring:message code="${descriptionCode}" />
	</p>
</section>

<section id="list-content">
	<div id="list-status">
		<netcare:block-message id="list-info" type="info" style="display:none;"></netcare:block-message>
		<netcare:block-message id="list-empty" type="info" style="display:none;"></netcare:block-message>
	</div>
	<jsp:doBody />
</section>


