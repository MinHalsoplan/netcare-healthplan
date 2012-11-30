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
						star : '<spring:message code="activity.reported.star" />',
						starred : '<spring:message code="activity.reported.starred" />'
				};
				
				var module = NC_MODULE.REPORTED_ACTIVITIES;
				
				function twoDigits(number) {
					if(number<10) {
						return "0" + number;
					} else {
						return number
					}
				}
				function formattedDate(date) {
					return "" + date.getFullYear() + (twoDigits(date.getMonth()+1)) + twoDigits(date.getDate());
				}
				function threeDaysAgo() {
					var now = new Date();
					var then = new Date()
					then.setDate(now.getDate()-3);
					return formattedDate(then);
				}

				function filter() {
					var personnummer = $('#personnummer').val();
					var dateFrom = $('#dateFrom').val();
					var dateTo = $('#dateTo').val();
					module.doFilter(personnummer, dateFrom, dateTo, msgs);
				}

				$("#dateFrom").val(threeDaysAgo());
				$("#dateTo").val(formattedDate(new Date()));
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
