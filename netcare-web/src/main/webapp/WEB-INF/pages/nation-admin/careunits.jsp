<%--

    Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>

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
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				NC_MODULE.CARE_UNIT_ADMIN.init();
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Administrera vårdenheter" plain="true">
		
		<a id="show-careunits-sheet" href="#" class="btn">Lägg till vårdenhet</a>
		
		<mvk:sheet id="careunit-form-sheet" style="display: none;">
			<form id="careunit-form">
				<netcare:field containerId="nameContainer" name="name" label="Namn">
					<input type="text" name="name" id="name" class="required"/>
				</netcare:field>
				<netcare:field containerId="hsaIdContainer" name="hsaId" label="HSA-ID">
					<input type="text" name="hsaId" id="hsaId" class="required" />
				</netcare:field>
				<netcare:field name="countyCouncil" label="Landsting">
					<select name="countyCouncil" id="countyCouncil"></select>
				</netcare:field>
				
				<div class="form-actions">
					<button type="submit" class="btn">Spara</button>
				</div>
			</form>
		</mvk:sheet>
		
		<br />
		
		<div id="careunits">
			<div class="sectionLoader" style="display: none;">
				<img src="<c:url value="/netcare/resources/images/loaders/ajax-loader-medium.gif" />" />
				<span class="loaderMessage"></span>
			</div>
			<div id="careunitsContainer" style="display: none;">
				<mvk:heading title="Vårdenheter">
					Listan visar alla vårdenheter som finns upplagda i Min hälsoplan. Klicka på en för att
					uppdatera dess information. 
				</mvk:heading>
				<mvk:touch-list id="careunitsListContainer"></mvk:touch-list>
				<div id="cuPagination" class="pagination pagination-centered">
					<ul></ul>
				</div>
			</div>
		</div>
		
		
	</hp:viewBody>
</hp:view>