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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="mobile" tagdir="/WEB-INF/tags/mobile" %>

<netcare:page>
	<mobile:header>
		<script type="text/javascript">
		
			var reportedLabel = '<spring:message code="mobile.activity.reported" />';
		
			var mobile = new NC.Mobile();
		
			var buildListView = function(value, buildHeader) {
				if (buildHeader) {
					mobile.createListHeader($('#schema'), value.day.value + '<br/>' + value.date);
				}
				
				mobile.createListRow($('#schema'), '#report', value, loadActivity);
			};
		
			var loadActivity = function(activityId) {
				NC.log("Load activity: " + activityId);
				
				new NC.HealthPlan().loadScheduledActivity(activityId, function(data) {
					$('#report div h3').html(data.data.definition.type.name);
					$('#report div p').html(data.data.day.value + ', ' + data.data.date + ' ' + data.data.time);
					
					$.each(data.data.measurements, function(i, v) {
						
						var id = 'report-' + v.measurementDefinition.measurementType.seqno;
						
						if (v.measurementDefinition.measurementType.valueType.code == "INTERVAL") {
							mobile.createReportField(id
									, $('#reportForm')
									, v.measurementDefinition.measurementType.name
									, (v.measurementDefinition.maxTarget + v.measurementDefinition.minTarget) / 2);
						} else {
							mobile.createReportField(id
									, $('#reportForm')
									, v.measurementDefinition.measurementType.name
									, v.measurementDefinition.target);
						}
					});
					
					$('#date').val(data.data.date);
					$('#time').val(data.data.time);
				});
				
				/*
				 * Report value
				 */
				$('#sendReport').click(function(e) {
					
					$.mobile.showPageLoadingMsg();
					e.preventDefault();
					
					var formData = new Object();
					formData.values = new Array();
					
					$.each($('input[id*="report-"]'), function(i, v) {
						formData.values.push({ seqno : $(v).attr('id').substr(7), value : $(v).val()});
					});
					
					formData.actualDate = $('#date').val();
					formData.actualTime = $('#time').val();
					formData.sense = $('#slider').val();
					formData.rejected = false;
					formData.note = $('#note').val();
					
					new NC.Patient().reportActivity(activityId, formData, function(data) {
						if (data.success) {
							
							loadFromServer(function() {
								
								var msg = $('<div>').addClass('ui-bar').addClass('ui-bar-e').append(
										$('<h3>' + data.successMessages[0].message + '</h3>')
								);
								
								$('#schema').before(msg);

								$('#back').click();
								$('#actual').click();
								
								$.mobile.hidePageLoadingMsg();
								
								setTimeout(function() {
									msg.slideUp('slow');
								}, 5000);
							});
						}
					});
					
					$('#sendReport').unbind('click');
				});
			};
			
			var buildFromArray = function(object) {
				var currentDay = '';
				
				$('#schema').empty();
				
				$.each(object, function(index, value) {
					NC.log("Processing " + value.id + " ...");
					if (currentDay != value.day.value) {
						currentDay = value.day.value;
						buildListView(value, true);
					} else {
						buildListView(value, false);
					}
				});
			};
			
			var due = new Array();
			var actual = new Array();
			var reported = new Array();
			
			var loadFromServer = function(callback) {
				
				due = new Array();
				actual = new Array();
				reported = new Array();
				
				new NC.Patient().listActivities(function(data) {
					$.each(data.data, function(index, value) {
						
						NC.log('Id: ' + value.id + " Reported: " + value.reported + " Due: " + value.due);
						
						if (value.reported != null) {
							NC.log("Pushing " + value.id + " to reported");
							reported.push(value);
						} else if (value.reported == null && value.due) {
							NC.log("Pushing " + value.id + " to due");
							due.push(value);
						} else {
							NC.log("Pushing " + value.id + " to actual");
							actual.push(value);
						}
					});
					
					callback();
				});
			};
		
			$('#start').live('pageinit', function(e) {
				
				loadFromServer(function() {
					NC.log("Done fetching data.");
					
					$('#actual').click(function(e) {
						NC.log("Loading actual activities...");
						buildFromArray(actual);
					});
					
					$('#due').click(function(e) {
						NC.log("Loading due activities...");
						buildFromArray(due);
					});
					
					$('#reported').click(function(e) {
						NC.log("Loading reported activities...");
						buildFromArray(reported);
					});
					
					$('#actual').click();
				});
				
				$('#ical').click(function(e) {
					NC.log("Getting calendar as ical");
					new NC.Patient().getCalendar(function(data) {
						NC.log("Success!");
					});
				});
			});
			
		</script>	
	</mobile:header>
	<body>
		<mobile:page id="start">
			<mobile:page-header title="mobile.activity.title" id="today-header">
<!-- 				<a rel="external" href="/api/patient/schema/min-halso-plan" data-icon="grid" class="ui-btn-right">iCal</a> -->
				<div data-role="navbar" class="ui-navbar" role="navigation">
					<ul class="ui-grid-b">
						<li class="ui-block-a">
							<a id="actual" href="#" data-icon="home" data-theme="a" class="ui-btn ui-btn-up-a"><spring:message code="mobile.activity.active" /></a>
						</li>
						<li class="ui-block-b">
							<a id="due" href="#" data-icon="alert" data-theme="a" class="ui-btn ui-btn-up-a"><spring:message code="mobile.activity.unfinished" /></a>
						</li>
						<li class="ui-block-b">
							<a id="reported" href="#" data-icon="check" data-theme="a" class="ui-btn ui-btn-up-a"><spring:message code="mobile.activity.done" /></a>
						</li>
					</ul>
				</div>
			</mobile:page-header>
			<mobile:page-body id="today-body">
				<mobile:list id="schema">
				</mobile:list>
			</mobile:page-body>
			<mobile:page-footer id="today-footer">
			
			</mobile:page-footer>
		</mobile:page>
		
		<mobile:page id="report">
			<mobile:page-header title="mobile.report.title" id="report-header">
			</mobile:page-header>
			<mobile:page-body id="report-body">
				<div class="ui-bar ui-bar-b">
					<h3></h3>
					<p></p>
				</div>
				<div class="ui-body ui-body-d">
					
					<form id="reportForm" method="post">
						<div data-role="fieldcontain">
							<label for="date"><spring:message code="mobile.report.form.date" /></label>
							<input type="date" id="date" name="date" />
						</div>
						
						<div data-role="fieldcontain">
							<label for="time"><spring:message code="mobile.report.form.time" /></label>
							<input type="time" id="time" name="time" />
						</div>
						
						<div data-role="fieldcontain">
							<label for="slider" id="slider-label" class="ui-slider ui-input-text"><spring:message code="mobile.report.form.sense" /></label>
							<input type="number" data-type="range" name="slider" id="slider" value="3" min="1" max="5" class="ui-slider-input ui-input-text ui-corner-all ui-shadow-inset" />
						</div>
						
						<div data-role="fieldcontain">
							<label for="note" class="ui-input-text"><spring:message code="mobile.report.form.note" /></label>
							<textarea name="note" id="note" class="ui-input-text"></textarea>
						</div>
						
						
						<a id="sendReport" href="#" data-theme="b" data-role="button" data-icon="check"><spring:message code="mobile.report.title" /></a>
						<a id="back" data-rel="back" data-theme="c" data-icon="arrow-l" data-role="button"><spring:message code="mobile.back" /></a>
					</form>
				</div>
			</mobile:page-body>
			<mobile:page-footer id="report-footer">
			
			</mobile:page-footer>
			
		</mobile:page>
	</body>
</netcare:page>