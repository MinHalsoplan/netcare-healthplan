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
				
				var _support = new NC.Support();
				
				var _ra
				var msgs;
				_support.loadMessages('report.reject,healthplan.icons.result,healthplan.icons.edit,activity.reported.none,comments.sendComment', function(messages) {
					msgs = messages;
					_ra = new NC.ReportedActivities(msgs);
				});

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
					module.doFilter(personnummer, dateFrom, dateTo);
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
	<hp:viewBody title="Genomförda aktiviteter">
		<h2><spring:message code="activity.reported.title" /></h2>
		<p>
			<span class="label label-info"><spring:message code="label.information" /></span>
			<spring:message code="activity.reported.desc2" />
		</p>
		<div class="controls">
    		<label class="control-label" for="personnummer">Personnummer</label>
    		<div class="controls">
      			<input type="text" id="personnummer">
    		</div>
    		<label class="control-label" for="personnummer">Datumperiod</label>
    		<div class="controls">
      			<input type="text" id="dateFrom" class="span2"> -
      			<input type="text" id="dateTo" class="span2">
    		</div>
    		<div class="form-actions">
    			<button class="btn btn-info">Sök</button>
    		</div>
  		</div>
		<mvk:touch-list id="latestActivitiesContainer">

		</mvk:touch-list>
	</hp:viewBody>
</hp:view>
