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

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mobile" tagdir="/WEB-INF/tags/mobile"%>

<!DOCTYPE html>
<html>
<mobile:header>
	<script type="text/javascript">
		var reportedLabel = '<spring:message code="mobile.activity.reported" />';
		var senseLabel = '<spring:message code="mobile.report.form.sense" />';

		var mobile = new NC.Mobile();
		var util = new NC.Util();
		var buildListView = function(value, buildHeader) {
			if (buildHeader) {
				mobile.createListHeader($('#schema'), value.day.value + ' '
						+ value.date);
			}

			mobile.createListRow($('#schema'), '#report', value, loadActivity,
					reportedLabel);
		};

		var due = new Array();
		var actual = new Array();
		var reported = new Array();

		var loadScheduledActivity = function(activityId, callback) {
			var activity;
			$.each(actual, function(index, act){
				if(act.id==activityId)
					activity = act;
				break;
			});
			if(activity==null) {
				$.each(due, function(index, act){
					if(act.id==activityId)
						activity = act;
					break;
				});
			}
			if(activity==null) {
				$.each(reported, function(index, act){
					if(act.id==activityId)
						activity = act;
					break;
				});
			}
			callback(activity);
		}
		
		var loadActivity = function(activityId) {
			NC.log("Load activity: " + activityId);

			loadScheduledActivity(45, function(activity) {
				NC.log(activity.id + '-' + activity.activityDefinition.healthPlanName);
			});
			
			new NC.HealthPlan()
					.loadScheduledActivity(
							activityId,
							function(data) {
								$('#report div h3').html(
										data.data.definition.type.name);
								$('#report div p').html(
										data.data.day.value + ', '
												+ data.data.date + ' '
												+ data.data.time);
								$('#slider-label')
										.html(
												senseLabel
														+ '&nbsp;('
														+ data.data.definition.type.minScaleText
														+ '-'
														+ data.data.definition.type.maxScaleText
														+ ')');

								if (data.data.definition.type.measuringSense) {
									$('#slider-div').show();
								} else {
									$('#slider-div').hide();
								}

								var reported = (data.data.reported != null);
								$
										.each(
												data.data.measurements,
												function(i, v) {

													var id = 'report-'
															+ v.measurementDefinition.measurementType.seqno;
													// remove existing
													$('#' + id).remove();
													if (v.measurementDefinition.measurementType.valueType.code == "INTERVAL") {
														mobile
																.createReportField(
																		id,
																		$('#reportForm'),
																		v.measurementDefinition.measurementType.name,
																		reported ? v.reportedValue
																				: Math
																						.round((v.measurementDefinition.maxTarget + v.measurementDefinition.minTarget) / 2));
													} else {
														mobile
																.createReportField(
																		id,
																		$('#reportForm'),
																		v.measurementDefinition.measurementType.name,
																		reported ? v.reportedValue
																				: v.measurementDefinition.target);
													}
												});

								$('#date').val(data.data.date);
								$('#time').val(data.data.time);
								$('#note').val(data.data.note);
							});

			/*
			 * Report value
			 */
			$('#sendReport')
					.click(
							function(e) {

								$.mobile.showPageLoadingMsg();
								e.preventDefault();

								var formData = new Object();
								formData.values = new Array();

								$.each($('input[id*="report-"]'),
										function(i, v) {
											formData.values.push({
												seqno : $(v).attr('id').substr(
														7),
												value : $(v).val()
											});
										});

								formData.actualDate = $('#date').val();
								formData.actualTime = $('#time').val();
								formData.sense = $('#slider').val();
								formData.rejected = false;
								formData.note = $('#note').val();

								new NC.Patient()
										.reportActivity(
												activityId,
												formData,
												function(data) {
													if (data.success) {

														loadFromServer(function() {

															var msg = $('<div>')
																	.addClass(
																			'ui-bar')
																	.addClass(
																			'ui-bar-e')
																	.append(
																			$('<h3>'
																					+ data.successMessages[0].message
																					+ '</h3>'));

															$('#schema')
																	.before(msg);

															$('#back').click();
															$('#actual')
																	.click();

															$.mobile
																	.hidePageLoadingMsg();

															setTimeout(
																	function() {
																		msg
																				.slideUp('slow');
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

		var loadFromServer = function(callback) {

			due = new Array();
			actual = new Array();
			reported = new Array();

			new NC.Ajax().get('/scheduledActivities', function(data) {
				$.each(data.data, function(index, value) {

					NC.log('Id: ' + value.id + " Reported: " + value.reported
							+ " Due: " + value.due);

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
	<div data-role="page" id="start" data-external-page="true">
		<div data-role="header" id="today-header" data-theme="c" data-position="fixed">
			<h1>
				<spring:message code="mobile.activity.title" />
			</h1>
			<!-- doesn't work, other integration method has to be used
 				<a rel="external" href="/api/patient/schema/min-halso-plan" data-icon="grid" class="ui-btn-right">iCal</a>
 				 -->
		</div>
		<div id="today-body" data-role="content-primary">
			<mobile:list id="schema">
			</mobile:list>
		</div>
		<div id="nc-footer" data-role="footer" data-theme="c" data-position="fixed">
			<div data-role="navbar" class="ui-navbar">
				<ul>
					<li><a id="actual" href="#" data-icon="home" class="ui-btn-active"><spring:message
								code="mobile.activity.active" /></a></li>
					<li><a id="due" href="#" data-icon="alert"><spring:message code="mobile.activity.unfinished" /></a></li>
					<li><a id="reported" href="#" data-icon="check"><spring:message code="mobile.activity.done" /></a></li>
				</ul>
			</div>
		</div>
	</div>
	<div data-role="page" id="report" data-external-page="true" data-add-back-btn="true">
		<div id="report-header" data-role="header" data-theme="c" data-position="fixed">
			<a href="#" data-rel="back" data-icon="arrow-l">Tillbaka</a>
			<h1></h1>
			<a href="#" data-rel="back" id="sendReport"><spring:message code="mobile.report.title" /></a>
		</div>
		<div id="report-body" data-role="content-primary">
			<div class="ui-bar">
				<h3></h3>
				<p></p>
			</div>
			<div class="ui-body ui-body-d">
				<form id="reportForm" method="post">
				</form>
			</div>
		</div>
	</div>
</body>
</html>
