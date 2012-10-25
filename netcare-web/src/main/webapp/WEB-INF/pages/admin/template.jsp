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

<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<%@ taglib prefix="hp" tagdir="/WEB-INF/tags"%>

<hp:view>
	<hp:viewHeader>
		<sec:authentication property='principal.careUnit.hsaId' var="currentHsaId" scope="page" />
		<hp:templates />
		<script type="text/javascript">
			$(document).ready(function() {
				var currentId = <c:out value="${currentId}" />;
				var currentActivityType;
				var ncActivityTypes = new NC.ActivityTypes();
				NC.log(ncActivityTypes);
				
				var findActivityTypeById = function() {
					ncActivityTypes.get(currentId, function(data) {
						NC.log('Loaded type id:' + currentId);
						currentActivityType = data.data;
						NC.log(currentActivityType);
						$("#activityTypeName").val(currentActivityType.name);
						var template = _.template($("#activityTypeItem").html());
						$.each(currentActivityType.activityItems, function(index, value) {
							$("#activityItems").append(template(value));
						});
					});
				};
				findActivityTypeById();
				$("#nextChooseName").click(function() {
					$("#activityTypeWrapper").show();
					$("#nextChooseName").hide();
				});
				$("#nextchooseActivities").click(function() {
					$("#chooseSaveWrapper").show();
					$("#nextchooseActivities").hide();
				});
				$("#addYesNoButton").click(function() {
					var template = _.template($("#activityTypeItem").html());
					var param = {
						id:	111,
						name : 'Ja/Nej-fråga'
					}
					$("#activityItems").append(template(param));
				});
			});
			function deleteListItem(itemId) {
				$("#" + itemId).remove();
			}
			function showYesNoContainer() {
				$("#activityTypeContainer").hide();
				$("#yesNoFormContainer").show();
			}
			function showActivityTypeContainer() {
				$("#activityTypeContainer").show();
				$("#yesNoFormContainer").hide();
			}
		</script>
	</hp:viewHeader>
	<hp:viewBody title="NyAktivitetsmall">
		<div id="whole">
			<div id="activityTypeContainer">
				<section id="head">
					<h2>
						<spring:message code="template.title" />
					</h2>
					<p>
						<span class="label label-info"><spring:message code="information" /></span>
						<spring:message code="template.description" />
					</p>
				</section>
				<section id="template">
					<div id="chooseName">
						<h4>
							1.
							<spring:message code="template.select.name" />
						</h4>
						<input id="activityTypeName" type="text" size="32">
						<div id="nextChooseNameWrapper">
							<a href="###" id="nextChooseName">
								<button class="btn btn-info" type="button">Nästa</button>
							</a>
						</div>
					</div>
					<div id="chooseActivities">
						<h4>
							2.
							<spring:message code="template.select.activities" />
						</h4>
						<div id="activityTypeWrapper" style="display: none; background-color: white; padding: 3px;">

							<ul id="activityItems" class="itemList facility">
							</ul>
							<div id="addActivityButtons">
								<div id="addListItemWrapper">
									<span style="padding-right: 15px"></span> <a href="#" class="addListItem">Mätning</a> <a href="#"
										class="addListItem">Skattning</a> <a href="#" class="addListItem" id="addYesNoButton">Ja/Nej-fråga</a>
									<a href="#" class="addListItem">Text</a>
								</div>
							</div>
							<div id="nextchooseActivitiesWrapper">
								<a href="###" id="nextchooseActivities">
									<button class="btn btn-info" type="button">Nästa</button>
								</a>
							</div>
						</div>
					</div>
					<div id="chooseSave">
						<h4>
							3.
							<spring:message code="template.select.saveTemplate" />
						</h4>
						<div id="chooseSaveWrapper" style="display: none; background-color: white; padding: 3px;">
							<button class="btn btn-info" type="button">Spara aktivitetsmall</button>
						</div>
					</div>
				</section>
			</div>
			<div id="yesNoFormContainer" style="display: none;">
				<section id="yesnoHead">
					<h2>
						<spring:message code="template.activity.yesno.title" />
					</h2>
					<p>
						<span class="label label-info"><spring:message code="information" /></span>
						<spring:message code="template.activity.yesno.description" />
					</p>
				</section>
				<section id="yesnoSection">
					<h4>
						<spring:message code="template.activity.yesno.field.label" />
						:
					</h4>
					<textarea id="questionId" rows="2" class="span6"></textarea>
				</section>
				<button onclick="showActivityTypeContainer();" class="btn btn-info">&lt;&lt; Spara</button>
			</div>
		</div>
	</hp:viewBody>
</hp:view>

