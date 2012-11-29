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

<hp:view>
	<hp:viewHeader>
		<script type="text/javascript">
			$(function() {
				NC_MODULE.CATEGORIES.init();
			});
		</script>
	</hp:viewHeader>
	<spring:message code="admin.menu.categories" var="cat" />
	<hp:viewBody title="${cat}">
		<form id="activityCategoryForm">
			<spring:message code="category.name" var="categoryName" scope="page" />
			<netcare:field name="name" label="${categoryName}">
				<input type="text" name="name" />
			</netcare:field>
			
			<input type="hidden" name="id" value="-1" />
			
			<div class="form-actions">
				<button type="submit" class="btn btn-info">Lägg till</button>
			</div>
			
		</form>
		
		<section id="categoryList">
			<h3 class="title"><spring:message code="category.list.title" /></h3>
			<netcare:table id="categoryTable">
				<thead>
					<tr>
						<th><spring:message code="name" /></th>
					</tr>
				</thead>
				<tbody></tbody>
			</netcare:table>
		</section>
	</hp:viewBody>
</hp:view>