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
<%@ taglib prefix="c_rt" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>

<mvk:page>
	<mvk:header title="Netcare 2.0" resourcePath="/netcare/resources" contextPath="${pageContext.request.contextPath}">
		<link rel="stylesheet" href="<c:url value="/css/netcare.css" />" />
	
		<script type="text/javascript" src="https://www.google.com/jsapi"></script>
		<script type="text/javascript">
			google.load('visualization', '1', {
				packages : [ 'gauge' ]
			});
		</script>
		
		<netcare:js />
	
		<script type="text/javascript">
			 var home;
			 
			 function createGauge(pd) {
			    // Create and populate the data table.
		        pd.data = new google.visualization.DataTable();
		        pd.data.addColumn('string', 'Label');
		        pd.data.addColumn('number', 'Value');
		        pd.data.addRows(1);
		        pd.data.setValue(0, 0, '%');
				var pctDone = Math.ceil((pd.numDone / pd.numTarget)*100);						
		        pd.data.setValue(0, 1, pctDone);
		        pd.options = new Object();
		        pd.options.width = 100;
		        pd.options.height = 100;
		        pd.options.max = Math.max(120, pctDone);
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
				

				$('#sendComment').click(function(event) {
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

						var icon = util.createIcon('send-reply', 24, function() {
							$('#sendReplyId input[name="commentId"]').attr('value', value.id);
							$('#sendReply').modal('show');
							$('#sendReplyId input[name="reply"]').focus();
						}, 'comments.sendComment');
												
						var deleteIcon = util.createIcon('trash', 24, function() {
							hps.deleteComment(value.id, function(data) {
								hps.loadLatestComments(patientId, loadComments);
							});
						}, 'comments.delete').css('padding-left', '10px');
				
						tr.append($('<td>').css('font-style', 'italic').html('"' + value.comment + '"'));
						tr.append($('<td>').html(activity));
						tr.append($('<td>').html(value.commentedBy));
						
						var actionCol = $('<td>').css('text-align', 'right');
						actionCol.append(icon);
						actionCol.append(deleteIcon);
						
						tr.append(actionCol);
						
						$('#commentTableId tbody').append(tr);
						
					});
				};
				
				var updateGauges = function(total) {
					var arr = home.perfData();
					for (var i = 0; i < arr.length; i++) {
						var pd = arr[i];
						var pctDone = Math.ceil((pd.numDone / (total ? pd.numTotal : pd.numTarget))*100);						
						pd.data.setValue(0, 1, pctDone);
					    pd.options.max = Math.max(120, pctDone);
				        pd.options.greenFrom = total ? (pd.numTarget / pd.numTotal) * 100 : 90;
				        pd.options.greenTo = pd.options.max;
				        pd.gauge.draw(pd.data, pd.options);
					}
				};
				
				$('#totalBoxId').click(function() {
					updateGauges(($(this).is(':checked')));
				});
				
				// reporting stuff				
				var report = new NC.PatientReport('schemaTable', true);
				report.init();
				report.reportCallback(function(id, done, last) {
					if (last) {
						$('#eventBody').hide();
					}
					var gid = 'gauge-' + id;
					var arr = home.perfData();
					for (var i = 0; i < arr.length; i++) {
						if (arr[i].id == gid) {
							var pd = arr[i];
							pd.numDone += done;
							var pctDone = Math.ceil((pd.numDone / pd.numTarget)*100);						
					        pd.options.max = Math.max(120, pctDone);
							pd.data.setValue(0, 1, pctDone);
				        	pd.gauge.draw(pd.data, pd.options);
				        	break;
						}
					}
				});

				hps.loadLatestComments(patientId, loadComments);
				
				$('#sendReply').bind('shown', function() {
					$('#sendReply input[name="reply"]').focus();
				});

			});

		</script>
	</mvk:header>
	<mvk:body>
		
		<mvk:pageHeader title="Min hälsoplan"
			loggedInUser="Testar Test"
			loggedInAsText="Inloggad som : "
			logoutUrl="/netcare/security/logout"
			logoutText="Logga ut" />
		
		<mvk:pageContent>
			<mvk:leftMenu>
				<netcare:menu />
			</mvk:leftMenu>
			<mvk:content title="Startsida">
				<div id="sendReply" class="modal hide fade" style="display: none;">
					<div class="modal-header">
						<a href="#" class="close" data-dismiss="modal">x</a>
						<h3>
							<spring:message code="comments.sendReply" />
						</h3>
					</div>
					<div class="modal-body">
						<form id="sendReplyId" action="#" method="post">
							<input type="hidden" name="commentId" />
							<spring:message code="comments.reply" var="reply" scope="page" />
							<netcare:field name="reply" label="${reply}:">
								<input type="text" class="xlarge" name="reply" />
							</netcare:field>
					</div>
					<div class="modal-footer">
						<input id="sendComment" type="submit" class="btn info" value="<spring:message code="comments.reply" />" />
						</form>
					</div>
				</div>
				
				<section id="report">
					<netcare:block-message type="info" id="eventBody">
					
					</netcare:block-message>
					
					<netcare:report />
					<br />
				</section>
				
				<section id="comments">
					<h2><spring:message code="comments.title" /></h2>
					<p>
						<span class="label label-info"><spring:message code="information" /></span>
						<spring:message code="comments.desc" />
					</p>
					<netcare:block-message type="info" id="noCommentId">
						<spring:message code="comments.none" />
					</netcare:block-message>
					<netcare:table id="commentTableId" style="display: none;">
						<thead>
							<tr>
								<th><spring:message code="comments.comment" /></th>
								<th><spring:message code="comments.activity" /></th>
								<th><spring:message code="comments.from" /></th>
								<!-- work-around (twitter bootstrap problem): hard coded width to avoid compression of icon -->
								<th width="64px">&nbsp;</th>
							</tr>
						</thead>
						<tbody></tbody>
					</netcare:table>
				</section>
				
				<br />
	
				<section id="healthPlan">
					<h2><spring:message code="phome.header" /></h2>
					<p>
						<span class="label label-info"><spring:message code="information" /></span>
						<spring:message code="phome.headerDesc" />
					</p>
					<div id="planDescription" style="margin: 10px"></div>
					<netcare:table id="planTable">
						<thead>
							<tr>
								<!-- work-around (twitter bootstrap problem): hard coded width to avoid compression of icon -->
								<th width="40px">&nbsp;</th>
								<th><spring:message code="phome.plan" /></th>
								<th><spring:message code="phome.activity" /></th>
								<th><spring:message code="phome.until" /></th>
								<th><spring:message code="phome.frequency" /></th>
								<th><spring:message code="phome.done" /><br /> <input id="totalBoxId" type="checkbox" /> <spring:message code="phome.showTot" /></th>
							</tr>
						</thead>
						<tbody></tbody>
					</netcare:table>
					<br />
					<div style="text-align: right">
						<a href="<c:out value="${GLOB_CTX_PATH}" />/api/patient/schema/min-halso-plan"><spring:message code="phome.icalLink" /></a>
					</div>
				</section>
				
			</mvk:content>
		</mvk:pageContent>
		
		<mvk:pageFooter>
		
		</mvk:pageFooter>
	</mvk:body>
</mvk:page>

<%-- 
		<netcare:content>
			<div id="sendReply" class="modal hide fade" style="display: none;">
				<div class="modal-header">
					<a href="#" class="close" data-dismiss="modal">x</a>
					<h3>
						<spring:message code="comments.sendReply" />
					</h3>
				</div>
				<div class="modal-body">
					<form id="sendReplyId" action="#" method="post">
						<input type="hidden" name="commentId" />
						<spring:message code="comments.reply" var="reply" scope="page" />
						<netcare:field name="reply" label="${reply}:">
							<input type="text" class="xlarge" name="reply" />
						</netcare:field>
				</div>
				<div class="modal-footer">
					<input id="sendComment" type="submit" class="btn btn-info" value="<spring:message code="comments.reply" />" />
					</form>
				</div>
			</div>
		</netcare:content>
--%>