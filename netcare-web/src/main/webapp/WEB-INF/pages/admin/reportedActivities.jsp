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

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags" %>

<hp:view>
	<hp:viewHeader>
		<hp:templates />
		<script type="text/javascript">
			$(function() {
				
				var msgs = {
						like : '<spring:message code="activity.reported.like" />',
						liked : '<spring:message code="activity.reported.liked" />',
						markasread : '<spring:message code="activity.reported.markasread" />',
						markedasread : '<spring:message code="activity.reported.markedasread" />'
				};
				
				var module = NC_MODULE.REPORTED_ACTIVITIES;

				function filter() {
					var personnummer = $('#personnummer').val();
					var dateFrom = $('#dateFrom').val();
					var dateTo = $('#dateTo').val();
					module.doFilter(personnummer, dateFrom, dateTo, msgs);
				}
				
				var now = new Date();
				var start = new Date();
				start.setDate(now.getDate()-3);
				
				$('#dateFrom').datepicker('setDate', start);
				$('#dateTo').datepicker('setDate', now);

				$('.btn').click(function() {
					filter();
				});

				filter();
			});
		</script>
	</hp:viewHeader>
	
	<spring:message code="activity.reported.title" var="reported"/>
	<hp:viewBody title="${reported}" plain="true">
		<mvk:heading title="${reported}">
			<spring:message code="activity.reported.desc2" />
		</mvk:heading>
		<mvk:sheet>
			<div class="controls">
	    		<label class="control-label" for="personnummer">Personnummer</label>
	    		<div class="controls">
	      			<input type="text" id="personnummer">
	    		</div>
	    		<label class="control-label" for="personnummer">Datumperiod</label>
	    		<div class="controls">
	      			<input type="text" id="dateFrom" class="span3 dateInput allow-previous"> -
	      			<input type="text" id="dateTo" class="span3 dateInput allow-previous">
	    		</div>
	    		<div class="form-actions">
	    			<button class="btn btn-info">Sök</button>
	    		</div>
	  		</div>
  		</mvk:sheet>
  		
  		<section id="report">
  			<div class="sectionLoader" style="display: none;">
				<img src="<c:url value="/netcare/resources/images/loaders/ajax-loader-medium.gif" />" />
				<span class="loaderMessage"></span>
			</div>
			<div id="reportContainer" style="display: none;">
  				<mvk:touch-list id="latestActivitiesContainer"></mvk:touch-list>
				<div id="riPagination" class="pagination pagination-centered">
					<ul></ul>
				</div>
			</div>
  		</section>
		
	</hp:viewBody>
</hp:view>
