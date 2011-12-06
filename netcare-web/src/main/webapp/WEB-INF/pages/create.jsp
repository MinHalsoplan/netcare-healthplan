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
				
				$('div[class="box"]').position({
					of : $(window)
				});
				
				/* Hide pincode initially */
				var mobileElem = $('input[name="mobile"]');
				if (mobileElem.attr('checked') === undefined) {
					$('#pinField').hide();
				}
				
				$(mobileElem).click(function(event) {
					if(mobileElem.attr('checked') == 'checked') {
						console.log('Show pin code');
						$('#pinField').fadeIn(500);
					} else {
						console.log('Hide pin code');
						$('#pinField').fadeOut(250);
					}
				});
			});
		</script>
	</netcare:header>
	<netcare:body>
		<div class="box">
			<h1><spring:message code="initialCreate" /></h1>
			<p>
				<spring:message code="initialCreateDesc" />
			</p>
		
			<form:form modelAttribute="user" action="create" method="post">
				<div class="row">
					<form:label path="name"><spring:message code="name" /></form:label>
					<form:input path="name"/>
				</div>
				
				<div class="row">
					<form:label path="email"><spring:message code="email" /></form:label>
					<form:input path="email" />
				</div>
				
				<div class="row">
					<form:label path="mobile"><spring:message code="mobile" /></form:label>
					<form:checkbox path="mobile"/>
				</div>
				
				<div id="pinField" class="row">
					<form:label path="pinCode"><spring:message code="pincode" /></form:label>
					<form:password path="pinCode"/>
				</div>
			
				<div>
					<input type="submit" value="<spring:message code='create' />" />
				</div>
			</form:form>
		</div>
	</netcare:body>
</netcare:page>