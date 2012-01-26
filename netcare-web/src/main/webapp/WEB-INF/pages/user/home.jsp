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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			google.load('visualization', '1', {
				packages : [ 'gauge' ]
			});
		</script>
		
		<script type="text/javascript">
			 var home;
			 
			 function createGauge(pd) {
			    // Create and populate the data table.
		        pd.data = new google.visualization.DataTable();
		        pd.data.addColumn('string', 'Label');
		        pd.data.addColumn('number', 'Value');
		        pd.data.addRows(1);
		        pd.data.setValue(0, 0, '%');
		        pd.data.setValue(0, 1, pd.pctSum);
		        pd.options = new Object();
		        pd.options.width = 100;
		        pd.options.height = 100;
		        pd.options.max = Math.max(120, pd.pctSum);
		        pd.options.min = 0;
		        pd.options.greenFrom = 90;
		        pd.options.greenTo = pd.options.max;
		        // Create and draw the visualization.
		        pd.gauge = new google.visualization.Gauge($('#' + pd.id).get(0));
		        pd.gauge.draw(pd.data, pd.options);
			}
			
			google.setOnLoadCallback(function() {
				$(function() {
					home = NC.PatientHome('planDescription', 'planTable',
							'eventBody');
					home.status();
					home.list(function() {
						$.each(home.perfData(), function(index, pd) {
							createGauge(pd);
						});
					});
				});
			});
			
			$(function() {
				NC.log("Loading latest comments...");
				var patientId = '<sec:authentication property="principal.id" />';
				
				var util = new NC.Util();
				var hps = new NC.HealthPlan();
				

				$('#sendReplyId :submit').click(function(event) {
					event.preventDefault();
					
					NC.log("Submitting reply...");
					
					var commentId = $('#sendReplyId input[name="commentId"]').val();
					hps.sendCommentReply(commentId, $('#sendReplyId input[name="reply"]').val(), function(data) {
						
						$('#sendReplyId input[name="reply"]').val('');
						$('#sendReply').modal('hide');
						
						$('#sendReply').unbind('click');
						
						hps.loadLatestComments(patientId, loadComments);
						
						$('#sendReplyId').modal('hide');
					});
				});

				var loadComments = function(data) {
					NC.log("Found " + data.data.length + " comments. Processing...");
					
					if (data.data.length > 0) {
						$('#commentTableId').show();
						$('#noCommentId').hide();
					} else {
						$('#commentTableId').hide();
						$('#noCommentId').show();
					}
					
					$('#commentTableId tbody > tr').empty();
					
					$.each(data.data, function(index, value) {
						
						var tr = $('<tr>');
						
						var activity = value.activityName + ' (' + value.activityReportedAt + ')';
						

						var icon = util.createIcon('comment', 24, function() {
							$('#sendReplyId input[name="commentId"]').attr('value', value.id);
							$('#sendReply').modal('show');
							$('#sendReplyId input[name="reply"]').focus();
						});
												
						var deleteIcon = util.createIcon('trash', 24, function() {
							hps.deleteComment(value.id, function(data) {
								hps.loadLatestComments(patientId, loadComments);
							});
						});
						
						tr.append($('<td>').css('font-style', 'italic').html('"' + value.comment + '"'));
						tr.append($('<td>').html(activity));
						tr.append($('<td>').html(value.commentedBy));
						
						var actionCol = $('<td>');
						actionCol.append(icon);
						actionCol.append(deleteIcon);
						
						tr.append(actionCol);
						
						$('#commentTableId tbody').append(tr);
						
					});
				};
				
				// reporting stuff				
				var report = new NC.PatientReport('schemaTable', true);
				report.init();
				report.reportCallback(function(id, actual, last) {
					if (last) {
						$('#eventBody').hide();
					}
					var gid = 'gauge-' + id;
					var arr = home.perfData();
					for (var i = 0; i < arr.length; i++) {
						if (arr[i].id == gid) {
							var pd = arr[i];
							pd.sumDone +=  actual;
							pd.pctSum = Math.ceil((pd.sumDone / pd.sumTarget)*100);						
					        pd.options.max = Math.max(120, pd.pctSum);
							pd.data.setValue(0, 1, pd.pctSum);
				        	pd.gauge.draw(pd.data, pd.options);
				        	break;
						}
					}
				});

				hps.loadLatestComments(patientId, loadComments);
				
				$('#sendReply').modal();
				$('#sendReply').bind('shown', function() {
					$('#sendReply input[name="reply"]').focus();
				});

			});

		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<div id="sendReply" class="modal hide fade" style="display: none;">
				<form id="sendReplyId" action="#" method="post">
					<div class="modal-header">
						<a href="#" class="close">x</a>
						<h3>
							<spring:message code="comments.sendReply" />
						</h3>
					</div>
					<div class="modal-body">
						<input type="hidden" name="commentId" />
						<spring:message code="comments.reply" var="reply" scope="page" />
						<netcare:field name="reply" label="${reply}:">
							<input type="text" class="xlarge" name="reply" />
						</netcare:field>
					</div>
					<div class="modal-footer">
						<input type="submit" class="btn primary"
							value="<spring:message code="comments.reply" />" />
					</div>
				</form>
			</div>

			<section id="report">
				<div id="eventBody" style="border-radius: 10px" class="alert-message info"></div>
				<netcare:report />
				<br />
			</section>
			
			<section id="comments">
				<h2><spring:message code="comments.comments" /></h2>
				<div id="noCommentId" class="alert-message info">
					<p><spring:message code="comments.noComments" /></p>
				</div>
				<table id="commentTableId" class="bordered-table zebra-striped shadow-box" style="display: none;">
					<thead>
						<th><spring:message code="comments.comment" /></th>
						<th><spring:message code="comments.activity" /></th>
						<th><spring:message code="comments.from" /></th>
						<th>&nbsp;</th>
					</thead>
					<tbody></tbody>
				</table>
			</section>
			
			<br />

			<section id="healthPlan">
				<h2><spring:message code="phome.header" /></h2>
				<div id="planDescription" style="margin: 10px"></div>
				<table id="planTable"
					class="bordered-table zebra-striped shadow-box">
					<thead>
						<th>&nbsp;</th>
						<th><spring:message code="phome.plan" /></th>
						<th><spring:message code="phome.activity" /></th>
						<th><spring:message code="phome.until" /></th>
						<th><spring:message code="phome.frequency" /></th>
						<th><spring:message code="phome.done" /></th>
					</thead>
					<tbody>
					</tbody>
				</table>
				<br />
				<div style="text-align: right">
					<a href="/netcare-web/api/patient/result/mina-resultat.csv"><spring:message code="phome.resultLink" /></a>&nbsp;|&nbsp; 
					<a href="/netcare-web/api/patient/schema/min-halso-plan"><spring:message code="phome.icalLink" /></a>
				</div>
			</section>
		</netcare:content>
	</netcare:body>
</netcare:page>