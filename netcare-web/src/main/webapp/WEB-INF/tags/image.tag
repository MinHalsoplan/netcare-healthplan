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
<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="size" required="false" %>
<%@ attribute name="icon" required="false" %>
<%@ attribute name="cursor" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:choose>
	<c:when test="${not empty size}">
		<c:set var="iconSize" value="${size}" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="iconSize" value="16" scope="page" />
	</c:otherwise>
</c:choose>

<c:url value="/img/icons/${iconSize}/${name}.png" var="url" scope="page"/>
<c:choose>
	<c:when test="${not empty icon}">
		<img src="${url}" style="background: url('${url}'); vertical-align: middle; cursor: ${cursor};"/>
	</c:when>
	<c:otherwise>
		<img src="${url}" />
	</c:otherwise>
</c:choose>

