<%--

    Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>

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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			$(function() {
				$('#modal-from-dom').modal('show');
			});
		</script>
	</netcare:header>
	<body>
		<div class="modal-backdrop fade in"></div>
		<div id="modal-from-dom" class="modal hide fade in" style="display: block;">
			<form method="post" action="<spring:url value="/j_spring_security_check" />" class="form-stacked">
				<div class="modal-header">
					<h3><spring:message code="login" /></h3>
				</div>
				<div class="modal-body">
					<div class="clearfix">
						<label for="j_username"><spring:message code="username" /></label>
						<div class="input">
							<input name="j_username" type="text" class="xlarge" />
						</div>
					</div>
					<div class="clearfix">
						<label for="j_password"><spring:message code="password" /></label>
						<div class="input">
							<input name="j_password" type="secret" class="xlarge" />
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<input type="submit" value="<spring:message code="login" />" />
				</div>
			
			</form>	
		</div>
	</body>
</netcare:page>