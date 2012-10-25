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
<%@ tag language="java" pageEncoding="UTF-8" body-content="empty"%>
<%@ attribute name="name" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<spring:message code="time.pattern" var="timePattern" scope="page" />
<netcare:row id="${name}Container">
	<netcare:col span="1">
		<spring:message code="${name}" var="label" scope="page" />
		<netcare:field name="day" label="${label}">
			<input type="checkbox" name="day" value="${name}"/>
		</netcare:field>
	</netcare:col>
	<netcare:col span="3">
		<spring:message code="activity.form.time" var="addTime" scope="page" />
		<netcare:field name="${name}TimeField" label="${addTime}">
			<netcare:timeInput name="${name}TimeField" timePattern="${timePattern}" classes="input-mini"/>
			<netcare:image name="add" icon="true"/>
		</netcare:field>
	</netcare:col>
	<netcare:col id="${name}AddedTimes" span="6" style="display: none;">
		<p><strong><spring:message code="activity.form.times" /></strong></p>
	</netcare:col>
</netcare:row>
