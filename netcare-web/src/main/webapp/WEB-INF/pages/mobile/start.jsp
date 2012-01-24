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
			var buildListView = function(value, buildHeader) {
				
				if (buildHeader) {
					 var header = $('<li>').attr('data-role', 'list-divider')
					.attr('role', 'heading')
					.addClass('ui-li')
					.addClass('ui-li-divider')
					.addClass('ui-btn')
					.addClass('ui-bar-b')
					.addClass('ui-li-has-count')
					.addClass('ui-btn-up-undefined') 
					.html(value.day.value + ' <br />' + value.date);
					
					$('#schema').append(header);
				}
				
				var activityContainer = $('<li>').attr('data-theme', 'c')
				.addClass('ui-btn')
				.addClass('ui-btn-icon-right')
				.addClass('ui-li-has-arrow')
				.addClass('ui-li')
				.addClass('ui-btn-up-c');
			
				var activityContentDiv = $('<div>').attr('area-hidden', 'true')
					.addClass('ui-btn-inner')
					.addClass('ui-li');
				
				activityContentDiv.append(
					$('<span>').addClass('ui-icon').addClass('ui-icon-arrow-r').addClass('ui-icon-shadow')
				);
				
				activityContainer.append(activityContentDiv);
				
				var link = $('<a>').attr('href', '#report').addClass('ui-link-inherit');
				activityContentDiv.append(link);
				
				link.append(
					$('<p><strong>' + value.time + '</strong></p>').addClass('ui-li-aside').addClass('ui-li-desc')
				);
				
				var activityText = $('<div>').addClass('ui-btn-text');
				activityText.append(
					$('<h3>' + value.definition.type.name + '</h3>').addClass('ui-li-heading')
				);
				
				activityText.append(
					$('<p>' + value.definition.goal +  ' ' + value.definition.type.unit.value + '</p>').addClass('ui-li-desc')
				);
				
				if (value.reported != null) {
					activityText.append(
						$('<p>Rapporterat värde: ' + value.actualValue +  ' ' + value.definition.type.unit.value + '</p>').addClass('ui-li-desc')
					);
					
					activityText.append(
						$('<p>' + value.reported + '</p>').addClass('ui-li-desc')
					);
				}
				
				link.append(activityText);
				
				$('#schema').append(activityContainer);
				
				link.click(function(e) {
					loadActivity(value.id);
				});
			};
		
			var loadActivity = function(activityId) {
				NC.log("Load activity: " + activityId);
				
				new NC.HealthPlan().loadScheduledActivity(activityId, function(data) {
					$('#valueUnit').html(data.data.definition.type.unit.value);
					
					$('#report div h3').html(data.data.definition.type.name + ' ' + data.data.definition.goal + ' ' + data.data.definition.type.unit.value);
					$('#report div p').html(data.data.day.value + ', ' + data.data.date + ' ' + data.data.time);
					
					$('#value').val(data.data.definition.goal);
					$('#date').val(data.data.date);
					$('#time').val(data.data.time);
				});
				
				/*
				 * Report value
				 */
				$('#sendReport').click(function(e) {
					
					$.mobile.showPageLoadingMsg();
					
					NC.log("Submitting form...");
					e.preventDefault();
					
					var formData = new Object();
					formData.actualValue = $('#value').val();
					formData.actualDate = $('#date').val();
					formData.actualTime = $('#time').val();
					formData.sense = $('#slider').val();
					formData.rejected = false;
					formData.note = $('#note').val();
					
					new NC.Patient().reportActivity(activityId, JSON.stringify(formData), function(data) {
						if (data.success) {
							loadFromServer(function() {
								
								$.mobile.hidePageLoadingMsg();
								
								$('#back').click();
								$('#actual').click();
								
								var msg = $('<div>').addClass('ui-bar').addClass('ui-bar-e').append(
										$('<h3>' + data.successMessages[0].message + '</h3>')
								);
								
								$('#schema').before(
									msg
								);
								
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
			});
			
		</script>	
	</mobile:header>
	<body>
		<mobile:page id="start">
			<mobile:page-header title="Aktiviteter" id="today-header">
				<div data-role="navbar" class="ui-navbar" role="navigation">
					<ul class="ui-grid-b">
						<li class="ui-block-a">
							<a id="actual" href="#" data-icon="home" data-theme="a" class="ui-btn ui-btn-up-a">Aktuella</a>
						</li>
						<li class="ui-block-b">
							<a id="due" href="#" data-icon="alert" data-theme="a" class="ui-btn ui-btn-up-a">Ej klara</a>
						</li>
						<li class="ui-block-b">
							<a id="reported" href="#" data-icon="check" data-theme="a" class="ui-btn ui-btn-up-a">Klara</a>
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
			<mobile:page-header title="Rapportera" id="report-header">
			</mobile:page-header>
			<mobile:page-body id="report-body">
				<div class="ui-bar ui-bar-b">
					<h3></h3>
					<p></p>
				</div>
				<div class="ui-body ui-body-d">
					
					<form method="post">
						<div data-role="fieldcontain">
							<label for="value" class="ui-input-text">Rapportera</label>
							<input type="number" id="value" name="value" /> <span id="valueUnit"></span>
						</div>
						
						<div data-role="fieldcontain">
							<label for="date">Datum</label>
							<input type="date" id="date" name="date" />
						</div>
						
						<div data-role="fieldcontain">
							<label for="time">Tid</label>
							<input type="time" id="time" name="time" />
						</div>
						
						<div data-role="fieldcontain">
							<label for="slider" id="slider-label" class="ui-slider ui-input-text">Känsla</label>
							<input type="number" data-type="range" name="slider" id="slider" value="3" min="1" max="5" class="ui-slider-input ui-input-text ui-corner-all ui-shadow-inset" />
						</div>
						
						<div data-role="fieldcontain">
							<label for="note" class="ui-input-text">Anteckning</label>
							<textarea name="note" id="note" class="ui-input-text"></textarea>
						</div>
						
						
						<a id="sendReport" href="#" data-theme="b" data-role="button" data-icon="check">Rapportera</a>
						<a id="back" data-rel="back" data-theme="c" data-icon="arrow-l" data-role="button">Tillbaka</a>
					</form>
				</div>
			</mobile:page-body>
			<mobile:page-footer id="report-footer">
			
			</mobile:page-footer>
			
		</mobile:page>
	</body>
</netcare:page>