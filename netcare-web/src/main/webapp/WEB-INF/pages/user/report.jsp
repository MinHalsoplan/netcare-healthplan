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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				var params = {
					patientId : '<sec:authentication property="principal.id" />',
					showAll : true,
					due : true,
					reported : true,
					start : new Date().getTime(),
					end : new Date().getTime()
				};
				
				NC_MODULE.PATIENT_SCHEDULE.init(params);
			});
		</script>
	</hp:viewHeader>
	<hp:viewBody title="Rapportera resultat" plain="true">
	
		<a href="<spring:url value="/netcare/user/extra-report" />" class="btn">Extrarapportering</a>
	
		<mvk:sheet>
			<div id="filter">
				<form id="filterForm">
					<fieldset>
						<legend>Välj vilka aktiviteter som ska visas</legend>
						<netcare:row>
							<netcare:col span="4">
								<netcare:field name="start" label="Från">
									<input id="start" type="text" name="start" class="dateInput allow-previous input-small">
								</netcare:field>	
							</netcare:col>
							<netcare:col span="4">
								<netcare:field name="end" label="Till">
									<input id="end" type="text" name="end" class="dateInput allow-previous input-small"/>
								</netcare:field>	
							</netcare:col>
						</netcare:row>
						<netcare:row>
							<netcare:col span="3">
								<netcare:field name="reported">
									<input id="reported" type="checkbox" name="reported" /><label style="display: inline;" for="reported"> Visa rapporterade aktiviteter</label>
								</netcare:field>	
							</netcare:col>
							<netcare:col span="3">
								<netcare:field name="due">
									<input id="due" type="checkbox" name="due" /><label style="display: inline;" for="due"> Visa aktiviteter som inte är utförda</label>
								</netcare:field>
							</netcare:col>
						</netcare:row>
						
						<div class="form-actions">
							<button id="filter-submit" type="submit" class="btn btn-primary btn-info">Visa</button>
						</div>
					
					</fieldset>
				</form>
			</div>
		</mvk:sheet>
	
		<div id="report">
			<div class="sectionLoader" style="display: none;">
				<img src="<c:url value="/netcare/resources/images/loaders/ajax-loader-medium.gif" />" />
				<span class="loaderMessage"></span>
			</div>
			<div id="reportContainer" style="display: none;">
				<h3 class="title">Aktiviteter</h3>
				<mvk:touch-list id="reportList"></mvk:touch-list>
				
				
				<div id="siPagination" class="pagination pagination-centered">
					<ul>
					</ul>
				</div>
				
			</div>
		</div>
		
	</hp:viewBody>
</hp:view>