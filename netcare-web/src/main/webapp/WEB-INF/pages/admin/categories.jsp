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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<mvk:page>
	<mvk:header title="Netcare 2.0" resourcePath="/netcare/resources" contextPath="${pageContext.request.contextPath}">
		<netcare:css resourcePath="/netcare/resources" />
		<netcare:js resourcePath="/netcare/resources"/>
		<hp:healthplan-js />
		<script type="text/javascript">
			$(function() {
				var categories = new NC.ActivityCategories();
				var loadCallback = function(data) {
					
					$('#categoryTable tbody').empty();
					
					$.each(data.data, function(index, value) {
						NC.log("Processing " + value.name + "...");
						
						var tr = $('<tr>');
						var name = $('<td>' + value.name + '</td>');
						
						tr.append(name);
						
						$('#categoryTable tbody').append(tr);
					});
				}
				
				categories.load(loadCallback);
				
				$(':submit').click(function(event) {
					NC.log("Form submitted");
					event.preventDefault();
					
					var formData = new Object();
					formData.name = $('input[name="name"]').val();
					
					categories.create(formData, function(data) {
						categories.load(loadCallback);
					});
					
					$('input[name="name"]').val('');
				});
				
			});
		</script>
	</mvk:header>
	<mvk:body>
		<mvk:pageHeader title="Min hälsoplan - Profil" loggedInUser="Testar Test" loggedInAsText="Inloggad som : "
			logoutUrl="/netcare/security/logout" logoutText="Logga ut" />

		<mvk:pageContent>
			<mvk:leftMenu>
				<hp:menu />
			</mvk:leftMenu>
			<mvk:content title="Aktivitetskategorier">
			
				<h2><spring:message code="category.title" /></h2>
				<p>
					<span class="label label-info"><spring:message code="information" /></span>
					<spring:message code="category.desc" />
				</p>
				<p>
					<span class="label label-important"><spring:message code="important" /></span>
					<spring:message code="category.important" />
				</p>
				
				<form id="activityCategoryForm">
					<fieldset>
						<legend><spring:message code="category.new" /></legend>
					</fieldset>
					
					<spring:message code="category.name" var="categoryName" scope="page" />
					<netcare:field name="name" label="${categoryName}">
						<input type="text" name="name" />
					</netcare:field>
					
					<div class="form-actions">
						<button type="submit" class="btn btn-info"><spring:message code="category.new" /></button>
						<button type="reset" class="btn"><spring:message code="clear" /></button>
					</div>
					
				</form>
				
				<section id="categoryList">
					<h3><spring:message code="category.list.title" /></h3>
					<p>
						<span class="label label-info"><spring:message code="information" /></span>
						<spring:message code="category.list.desc" />
					</p>
					<netcare:table id="categoryTable">
						<thead>
							<tr>
								<th><spring:message code="name" /></th>
							</tr>
						</thead>
						<tbody></tbody>
					</netcare:table>
				</section>
			</mvk:content>
		</mvk:pageContent>
	</mvk:body>	
</mvk:page>