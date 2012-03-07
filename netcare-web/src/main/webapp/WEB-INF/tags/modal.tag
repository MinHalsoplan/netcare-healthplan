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
<%@ attribute name="id" required="true" %>
<%@ attribute name="titleCode" required="true" %>
<%@ attribute name="confirmCode" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="${id}" class="modal fade">
	<div class="modal-header">
		<a class="close" data-dismiss="modal">×</a>
		<h3><spring:message code="${titleCode}" /></h3>
	</div>
	<div class="modal-body">
		<jsp:doBody />
	</div>
	<div class="modal-footer">
		<a href="#" class="btn btn-primary"><spring:message code="${confirmCode}" /></a>
		<a data-dismiss="modal" class="btn"><spring:message code="label.close" /></a>
	</div>
</div>
