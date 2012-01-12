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

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			$(function() {
				var categories = new NC.ActivityCategories();
				var loadCallback = function(data) {
					
					$('#categoryTable tbody').empty();
					
					$.each(data.data, function(index, value) {
						console.log("Processing " + value.name + "...");
						
						var tr = $('<tr>');
						var id = $('<td>' + value.id + '</td>');
						var name = $('<td>' + value.name + '</td>');
						
						tr.append(id).append(name);
						
						$('#categoryTable tbody').append(tr);
					});
				}
				
				categories.load(loadCallback);
				
				$(':submit').click(function(event) {
					console.log("Form submitted");
					event.preventDefault();
					
					var formData = new Object();
					formData.name = $('input[name="name"]').val();
					
					var jsonObj = JSON.stringify(formData);
					categories.create(jsonObj, function(data) {
						categories.load(loadCallback);
					});
					
					$('input[name="name"]').val('');
				});
				
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2><spring:message code="activityCategories" /></h2>
			<p>På den här sidan lägger du till nya aktivitetskategorier. Varje aktivitetskategori kan sedan innehålla flera aktivitetstyper. Etc...</p>
			
			<form id="activityCategoryForm" class="form-stacked">
				<fieldset>
					<legend><spring:message code="create" /></legend>
				</fieldset>
				
				<netcare:field name="name">
					<input type="text" name="name" />
					
					<spring:message code="create" var="create" scope="page" />
					<input type="submit" class="btn primary" value="${create}" />
				</netcare:field>
				
			</form>
			
			<table id="categoryTable" class="bordered-table zebra-striped shadow-box">
				<thead>
					<th>Id</th>
					<th>Namn</th>
				</thead>
				<tbody>
				
				</tbody>
			</table>
		</netcare:content>
		
	</netcare:body>	
</netcare:page>