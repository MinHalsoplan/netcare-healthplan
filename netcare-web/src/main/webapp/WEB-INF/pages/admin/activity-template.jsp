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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<sec:authentication property='principal.careUnit.hsaId' var="currentHsaId" scope="page" />
		<hp:templates />
		<script type="text/javascript">
			$(document).ready(function() {
				var params = {
					hsaId : '<c:out value="${currentHsaId}" />'
				};
				
				NC_MODULE.GLOBAL.init();
				NC_MODULE.TEMPLATE_SEARCH.init(params);
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Aktivitetsmallar">
		
		<netcare:row>
			<netcare:col span="6">
				<form class="form-search">
					<div class="input-append">
						<input type="text" class="span12 search-query" placeholder="Sök mall"/>
						<button type="submit" class="btn">Sök</button>
					</div>
				</form>
			</netcare:col>
			<netcare:col span="6" style="text-align: right;">
				<a href="<c:url value="/netcare/admin/template/1" />" class="btn btn-info"><i class="icon-plus-sign icon-white"></i> Skapa ny mall</a>
			</netcare:col>
		</netcare:row>
		
		<netcare:row>
			<netcare:col span="4">
				<netcare:field name="category" label="Välj en kategori">
					<select name="category" class="span10"></select>
				</netcare:field>
			</netcare:col>
			<netcare:col span="4">
				<netcare:field name="level" label="Välj en nivå">
					<select name="level" class="span10"></select>
				</netcare:field>
			</netcare:col>
		</netcare:row>
		
		<mvk:touch-list id="templateList">
		
		</mvk:touch-list>
	</hp:viewBody>
</hp:view>

