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
			$('#start').live('pageinit', function(e) {
				
				NC.log("Loading unreported activities");
				new NC.Patient().listActivities(function(data) {
					
					
					var currentDay = '';
					$.each(data.data, function(index, value) {
						NC.log("Processing " + value.id + " ...");
						
						if (currentDay != value.day.value) {
							currentDay = value.day.value;
							
							 var header = $('<li>').attr('data-role', 'list-divider')
							.attr('role', 'heading')
							.addClass('ui-li')
							.addClass('ui-li-divider')
							.addClass('ui-btn')
							.addClass('ui-bar-b')
							.addClass('ui-li-has-count')
							.addClass('ui-btn-up-undefined') 
							.html(currentDay + ' <br />' + value.date);
							
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
						
						link.append(activityText);
						
						$('#schema').append(activityContainer);
						
						
						link.click(function(e) {
							NC.log("Load activity: " + value.id);
							
							new NC.HealthPlan().loadScheduledActivity(value.id, function(data) {
								$('#valueUnit').html(data.data.definition.type.unit.value);
								
								$('#report h2').html(data.data.definition.type.name + ' ' + data.data.definition.goal + ' ' + data.data.definition.type.unit.value);
								$('#report p').html(data.data.day.value + ', ' + data.data.date + ' ' + data.data.time);
								
								$('#value').val(data.data.definition.goal);
								$('#date').val(data.data.date);
								$('#time').val(data.data.time);
							});
							
						});
					});
				});
				
			});
		</script>	
	</mobile:header>
	<body>
		<mobile:page id="start" title="Mina aktiviteter">
			<mobile:list id="schema">
			</mobile:list>
		</mobile:page>
		
		<mobile:page title="Rapportera aktivitet" id="report">
			<div class="ui-body ui-body-d">
				<h2>Löpning 1200 Meter</h2>
				<p>Måndag, 2012-01-16 18:15</p>
				
				
				<form>
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
						<input type="number" data-type="range" name="slider" id="slider" value="3" min="1" max="5" class="ui-slider-input ui-input-text ui-body-c ui-corner-all ui-shadow-inset" />
					</div>
					
					<div data-role="fieldcontain">
						<label for="note" class="ui-input-text">Anteckning</label>
						<textarea name="note" id="note" class="ui-input-text"></textarea>
					</div>
					
					<fieldset class="ui-grid-a">
						<div class="ui-block-b">
							<a href="#start" data-role="button">Tillbaka</a>
						</div>
						<div class="ui-block-b">
							<button type="submit" data-theme="b" class="ui-btn-hidden" aria-disabled="false">Rapportera</button>
						</div>
					</fieldset>
				</form>
			</div>
		</mobile:page>
	</body>
</netcare:page>