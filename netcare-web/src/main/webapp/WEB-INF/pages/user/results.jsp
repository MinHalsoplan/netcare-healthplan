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

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			google.load('visualization', '1.0', {'packages' : ['corechart']});

			$(function() {
				var drawOverview = function() {
					
					NC.log("Drawing overview...");
					
					var captions = null;
					new NC.Support().loadCaptions('result', ['activityType'
					                                         , 'numberOfActivities'
					                                         , 'date'
					                                         , 'reportedValue'
					                                         , 'targetValue'
					                                         , 'resultLink'
					                                         , 'targetMinValue'
					                                         , 'targetMaxValue'], function(data) {
						NC.log("Load captions returned success...");
						captions = data;
					});
					
					NC.log("Captions are: " + captions);
					
					var hp = new NC.HealthPlan();
					
					var loadStatistics = function(healthPlanId) {
						NC.log("Loading statistics for health plan " + healthPlanId);
						var reports = null;
						hp.loadStatistics(healthPlanId, function(data) {
							reports = new NC.Reports(data, captions);
							reports.getHealthPlanOverview('pieChart', captions.activityType, captions.numberOfActivities);
							$('#pieChart').show();
							
							/*
							 * For each activity type draw diagrams for each
							 */
							var processed = '';
							$.each(data.data.measuredValues, function(i, v) {
								if (v.name != processed) {
									var div = $('<div>').attr('id', 'activity-' + i);
									
									var div2 = $('<div>').css('text-align', 'right');
									
									var link = $('<a>').attr('href', NC.getContextPath() + '/api/patient/result/' + v.definitionId + '/resultat.csv');
									link.html(captions.resultLink);
									div2.append(link);
									
									$('#activities').append(
										$('<h2>').html(v.name)
									).append(div2).append(div);
									
									setupFilter($('#activity-' + i), v.name);
									
									NC.log("Drawing diagrams for: " + v.name);
									reports.getResultsForActivityType(v.name, 'activity-' + i);
									
									processed = v.name;
								}
							});

							$('div[class="shadow-box"]').each(function(i) {
								$(this).after('<br />');
							});
							
							$('#filter').show();
						});
					};
					
					var setupFilter = function(forElement, label) {
						var id = 'filter-for-' + forElement.attr('id');
						var input = new NC.Util().createCheckbox(id, label);
						
						$('#filter-row').append(
							$('<div>').addClass('span1').append(input)
						);
						
						$('#' + id).attr('checked', 'checked');
						input.click(function(e) {
							forElement.toggle();
						});
					}
					
					var healthPlanId = "<c:out value="${param.healthPlan}" />";
					if (healthPlanId == "")  {
						hp.list(<sec:authentication property="principal.id" />, function(data) {
							loadStatistics(data.data[0].id);
						});	
					} else {
						loadStatistics(healthPlanId);
					}
				};
								
				google.setOnLoadCallback(drawOverview);
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<h2><spring:message code="result.title" /></h2>
			<p>
				<span class="label label-info"><spring:message code="information" /></span>
				<spring:message code="result.desc" />
			</p>
			<div id="pieChart" style="display: none;" class="shadow-box"></div><br />
			
			<section id="filter" style="display:none;">
				<h2><spring:message code="result.filter" /></h2>
				<p>
					<span class="label label-info"><spring:message code="information" /></span>
					<spring:message code="result.filter.desc" />
				</p>
				<form>
					<netcare:row id="filter-row"></netcare:row>
				</form>
			</section>
			<div id="activities"></div>
		</netcare:content>
		
	</netcare:body>	
</netcare:page>