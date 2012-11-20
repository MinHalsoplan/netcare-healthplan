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
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="netcare" uri="http://www.callistasoftware.org/netcare/tags"%>

<!-- healthplan:templates -->
<script id="activityTemplate" type="text/template">
<li id="liActivityItem{{id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item-{{id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{name}}</h4>
			<div class="subRow">{{category.name}}</div>
		</div>
		<div class="mainBody actionBody span6"></div>
	</div>
</div>
<a href="#" class="itemNavigation assistiveText"></a>
</mvk:touch-item>
</li>
</script>
<%--
	Must be included within a touch-item and inserted
	after a.itemNavigation
 --%>
<script id="itemNote" type="text/template">
<div class="itemStateText">
	<div class="wrapper">{{value}}</div>
</div>
</script>

<%-- This template is used to show a healthplan --%>
<script id="patientItem" type="text/template">
<li id="patientItem{{id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item{{id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{name}}</h4>
			<div class="subRow">{{civicRegistrationNumber}}</div>
		</div>
		<div class="mainBody actionBody span6">

		</div>
	</div>
</div>
<a href="#" class="itemNavigation assistiveText"></a>
</mvk:touch-item>
</li>
</script>

<%-- This template is used to show a healthplan --%>
<script id="healthPlanItem" type="text/template">
<li id="healthPlanItem{{id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item{{id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{name}}</h4>
			<div class="subRow"></div>
		</div>
		<div class="mainBody actionBody span6">

		</div>
	</div>
</div>
<a href="#" class="itemNavigation assistiveText"></a>
</mvk:touch-item>
</li>
</script>

<%--Healthplan details --%>
<script id="healthPlanDetails" type="text/template">
<div id="hp-details-{{id}}" style="display: none;">
	<div class="row-fluid">
		<div class="span12">
		
		</div>
	</div>
	<div class="row-fluid">
		<div class="span6" style="text-align: left">
			<a href="<c:url value='/netcare/admin/templates?healthPlan={{id}}' />">Planera ny aktivitet från mall</a>
		</div>
		<div class="span6" style="text-align: right">
			<a id="hp-inactivate-{{id}}" href="#" style="padding-right: 10px">Avaktivera hälsoplan</a>
		</div>
	</div>
</div>
</script>

<%-- Health plan activity def --%>
<script id="healthPlanDefinitions" type="text/template">
<div id="hp-ad-{{id}}" class="row-fluid">
	<div class="span6">{{type.name}}</div>
	<div class="span4"><a id="hp-ad-{{id}}-edit" href="#">Redigera</a> | <a id="hp-ad-{{id}}-remove" href="3">Avsluta</a></div>
</div>
</script>

<%-- Activity planning - Single value --%>
<script id="singleValue" type="text/template">
<h4>{{name}}</h4>
<div class="control-group">
	<label for="field-{{id}}">Målvärde</label>
	<div class="controls">
		<input id="field-{{id}}" type="text" />
		<span class="help-inline">{{unit.value}}</span>
	</div>
</div>
</script>

<%-- Activity planning - Interval value --%>
<script id="intervalValue" type="text/template">
<h4>{{name}}</h4>
<div class="row-fluid">
	<div class="span6">
		<div class="control-group">
			<label for="field-{{id}}-min">Målvärde (min)</label>
			<div class="controls">
				<input id="field-{{id}}-min" type="text" />
				<span class="help-inline">{{unit.value}}</span>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
		<label for="field-{{id}}-max">Målvärde (max)</label>
		<div class="controls">
			<input id="field-{{id}}-max" type="text" />
			<span class="help-inline">{{unit.value}}</span>
		</div>
	</div>
</div>
</script>

<%-- This template is used to show an activity type item --%>
<script id="activityItemTemplate" type="text/template">
<li id="liActivityItem{{id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item{{id}}" class="listItemBase">
	<div class="mainBody">
		<h4 class="titel">{{name}}</h4>
		<div class="subRow">{{details}}</div>
	</div>
	<div id="item{{id}}moveUp" class="listItemMoveUp"></div>
	<div id="item{{id}}moveDown" class="listItemMoveDown"></div>
	<div id="item{{id}}delete" class="listItemDelete"></div>
</div>
<a id="item{{id}}showDetails" href="#" class="itemNavigation assistiveText"></a>
</mvk:touch-item>
</li>
</script>

<%-- This template is used to show an measurement item form --%>

<script id="activityItemMeasurementForm" type="text/template">
<h2>
	<spring:message code="template.activity.measurement.title" />
</h2>
<p>
	<span class="label label-info"><spring:message code="information" /></span>
	<spring:message code="template.activity.measurement.description" />
</p>
<div class="controls">
	<label for="activityItemName"><spring:message code="template.activity.activityItemName.field.label" /></label>
	<input type="text" class="span6" name="activityItemName" id="activityItemName" value="{{name}}"></input>
</div>
<div class="controls controls-row">
	<label for="valueType"><spring:message code="measureValue.type" /></label>
	<select name="valueType" id="valueType" class="span2"></select>
	<label for="measurementUnit"><spring:message code="measureValue.unit" /></label>
	<select name="measurementUnit" id="measurementUnit" class="span3"></select>
</div>
<div id="measureValueIntervalOnly" class="controls" style="display:none;">
	<label for="measurementAlarm"><spring:message code="measureValue.alarm" /></label>
	<input type="checkbox" name="measurementAlarm" id="measurementAlarm"></input>
	&nbsp;<spring:message code="measureValue.alarm.text" />
</div>
<div class="form-actions" style="padding-top:20px;">
	<button id="backButtonForm" class="btn btn-info">&lt;&lt; Tillbaka</button>
</div>
</script>

<%-- This template is used to show an estimation item form --%>

<script id="activityItemEstimationForm" type="text/template">
<h2>
	<spring:message code="template.activity.estimation.title" />
</h2>
<p>
	<span class="label label-info"><spring:message code="information" /></span>
	<spring:message code="template.activity.estimation.description" />
</p>
<div class="control-group">
<div class="controls">
	<label for="activityItemName"><spring:message code="template.activity.activityItemName.field.label" /></label>
	<input type="text" class="span6" name="activityItemName" id="activityItemName" value="{{name}}"></input>
</div>
<div class="controls">
	<label><spring:message code="estimation.scale.label" /></label>
</div>
<div class="controls controls-row">
	<spring:message code="estimation.lowValue.label" />
	<input type="text" name="minScaleValue" id="minScaleValue" class="span1" value="{{minScaleValue}}"></input>
	<span style="padding-left:20px;"><spring:message code="estimation.highValue.label" /></span>
	<input type="text" name="maxScaleValue" id="maxScaleValue" class="span1" value="{{maxScaleValue}}"></input>
</div>
<div class="controls">
	<label for="minScaleText"><spring:message code="estimation.lowText.label" /></label>
	<input type="text" class="span6" name="minScaleText" id="minScaleText" value="{{minScaleText}}"></input>
</div>
<div class="controls">
	<label for="maxScaleText"><spring:message code="estimation.highText.label" /></label>
	<input type="text" class="span6" name="maxScaleText" id="maxScaleText" value="{{maxScaleText}}"></input>
</div>
<div class="span12" style="padding-top:20px;">
	<button id="backButtonForm" class="btn btn-info">&lt;&lt; Tillbaka</button>
</div>
</div>
</script>

<%-- This template is used to show an yesno item form --%>

<script id="activityItemYesNoForm" type="text/template">
<h2>
	<spring:message code="template.activity.yesno.title" />
</h2>
<p>
	<span class="label label-info"><spring:message code="information" /></span>
	<spring:message code="template.activity.yesno.description" />
</p>
<div class="control-group">
<div class="controls">
	<label for="activityItemName"><spring:message code="template.activity.activityItemName.field.label" /></label>
	<input type="text" class="span6" name="activityItemName" id="activityItemName" value="{{name}}"></input>
</div>
<div class="controls">
	<label for="yesNoQuestion"><spring:message code="template.activity.yesno.question.label" /></label>
	<textarea id="yesNoQuestion" rows="2" class="span6">{{question}}</textarea>
</div>
<div class="span12" style="padding-top:20px;">
	<button id="backButtonForm" class="btn btn-info">&lt;&lt; Tillbaka</button>
</div>
</div>
</script>

<%-- This template is used to show an text item form --%>

<script id="activityItemTextForm" type="text/template">
<h2>
	<spring:message code="template.activity.text.title" />
</h2>
<p>
	<span class="label label-info"><spring:message code="information" /></span>
	<spring:message code="template.activity.text.description" />
</p>
<div class="control-group">
<div class="controls">
	<label for="activityItemName"><spring:message code="template.activity.activityItemName.field.label" /></label>
	<input type="text" class="span6" name="activityItemName" id="activityItemName" value="{{name}}"></input>
</div>
<div class="controls">
	<label for="textLabel"><spring:message code="template.activity.text.label.label" /></label>
	<textarea id="textLabel" rows="2" class="span6">{{label}}</textarea>
</div>
<div class="span12" style="padding-top:20px;">
	<button id="backButtonForm" class="btn btn-info">&lt;&lt; Tillbaka</button>
</div>
</div>
</script>

<script id="measureUnitRow" type="text/template">
<tr id="measure-unit-{{id}}">
	<td>{{dn}}</td>
	<td>{{name}}</td>
	<td><img id="measure-unit-{{id}}-edit" src="<c:url value="/img/icons/24/edit.png" />" style="cursor: pointer;" /></td>
</tr>
</script>

<script id="accessLevelsRadioOption" type="text/template">
	<input id="access-level-{{code}}" type="radio" name="accessLevel" value="{{code}}" />
	<label for="access-level-{{code}}" style="display: inline; margin-right: 20px;">{{value}}</label>
</script>

<script id="scheduledItem" type="text/template">
<li id="scheduled-activity-{{id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item-{{id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{type.name}}</h4>
			<div class="subRow"></div>
		</div>
		<div class="mainBody actionBody span6">

		</div>
	</div>
</div>
</mvk:touch-item>
</li>
</script>

<%-- This template is used to show a reported activity --%>
<script id="reportedActivityItem" type="text/template">
<li id="reportedActivityItem{{id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div id="raItem{{id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{patient.surName}}, {{patient.firstName}}</h4>
			<div class="subRow">{{activityDefinition.type.name}} - Rapporterad {{reported}}</div>
		</div>
		<div class="mainBody actionBody span6">

		</div>
	</div>
</div>
</mvk:touch-item>
</li>
</script>

<%--Reported Activity details --%>
<script id="reportedActivityDetails" type="text/template">
<div id="ra-details-{{id}}" class="span10" style="display: none;">
	<div class="row-fluid">
		<div class="span12">
		</div>
	</div>
	<div class="row-fluid">	
		<div class="likeReported span4"></div><div class="starReported span7"></div>
		<div id="actcomment" class="span11">
			<label for="activitycomment"><spring:message code="comments.sendComment" /></label>
			<textarea id="activitycomment" rows="2" class="span11"></textarea>
			<button class="btn"><spring:message code="report.save" /></button>
		</div>
		<div id="actcommented" class="span11" style="visibility:none;padding-bottom: 5px;">
		</div>
	</div>
</div>
</script>

<%--Reported Activity values --%>
<%--Estimation values --%>
<script id="estimationValues" type="text/template">
<div id="ra-row-{{id}}" class="row-fluid">
<div class="span5">{{definition.activityItemType.name}}
{{definition.activityItemType.minScaleValue}} ({{definition.activityItemType.minScaleText}}) - {{definition.activityItemType.maxScaleValue}} ({{definition.activityItemType.maxScaleText}})</div>
<div class="span5">{{perceivedSense}}</div>
</div>
</script>
<%--Measurement values --%>
<script id="measurementValues" type="text/template">
<div id="ra-row-{{id}}" class="row-fluid">
<div class="span5">{{definition.activityItemType.name}}</div>
<div class="span5">{{reportedValue}} {{definition.activityItemType.unit.name}}</div>
</div>
</div>
</script>
<%--Reported Activity values --%>
<script id="yesnoValues" type="text/template">
<div id="ra-row-{{id}}" class="row-fluid">
<div class="span5">{{definition.activityItemType.name}} {{definition.activityItemType.question}}
</div>
<div class="span5">{{answer}}</div>
</div>
</script>
<%--Reported Activity values --%>
<script id="textValues" type="text/template">
<div id="ra-row-{{id}}" class="row-fluid">
<div class="span5">{{definition.activityItemType.name}} {{definition.activityItemType.label}}</div>
<div class="span5">{{textComment}}</div>
</div>
</script>

<%-- This template is used to show a scheduled activity --%>
<script id="scheduledActivityItem" type="text/template">
<li id="scheduledActivityItem{{id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div id="saItem{{id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{activityDefinition.type.name}}</h4>
			<div class="subRow">
			</div>
		</div>
		<div class="mainBody actionBody span6">

		</div>
	</div>
</div>
</mvk:touch-item>
</li>
</script>

<%--Scheduled Activity details --%>
<script id="scheduledActivityDetails" type="text/template">
<div id="sa-details-{{id}}" class="span10" style="display: none;">
	<div class="row-fluid">
		<div class="span12">
		
		</div>
	</div>
	<div class="row-fluid external">
		<div class="span5">
			Ange tidpunkt då aktiviteten utfördes
		</div>
		<div class="span7">
			<input id="{{id}}-report-date" type="text" style="display: inline;" class="span5 dateInput"/>
			<input id="{{id}}-report-time" type="text" style="display: inline;" class="span3"/>
		</div>
	</div>
	<div class="row-fluid external">
		<div class="span5">
			Övriga kommentarer
		</div>
		<div class="span7">
			<textarea id="{{id}}-report-note" class="span11">{{note}}</textarea>
		</div>
	</div>
	<div class="form-actions" style="margin: 0">
		<button id="sa-report-{{id}}" type="button" class="btn btn-primary btn-info">Rapportera</button>
		<button id="sa-noreport-{{id}}" type="button" class="btn btn-danger">Rapportera som ej utförd</button>
	</div>
</div>
</script>

<%--Scheduled Activity values --%>
<%--Estimation values --%>
<script id="scheduled-estimationValues" type="text/template">
<div id="sa-row-{{id}}" class="row-fluid">
<div class="span5">{{definition.activityItemType.name}}</div>
<div class="span7">
	<span style="display: inline;">{{definition.activityItemType.minScaleText}}</span>
	<input type="range" min="{{definition.activityItemType.minScaleValue}}" max="{{definition.activityItemType.maxScaleValue}}" step="1" value="{{perceivedSense}}" />
	<span style="display: inline;">{{definition.activityItemType.maxScaleText}}</span>
</div>
</div>
</script>
<%--Measurement values --%>
<script id="scheduled-measurementValues" type="text/template">
<div id="sa-row-{{id}}" class="row-fluid">
<div class="span5">{{definition.activityItemType.name}}</div>
<div class="span7">
	<input type="text" value="{{reportedValue}}" />
	<span style="display: inline;">{{definition.activityItemType.unit.name}}</span>
</div>
</div>
</div>
</script>
<%--Yes/No values --%>
<script id="scheduled-yesnoValues" type="text/template">
<div id="sa-row-{{id}}" class="row-fluid">
<div class="span5">{{definition.activityItemType.name}} {{definition.activityItemType.question}}
</div>
<div class="span7">
	<input type="radio" name="answer" value="true" /> Ja
	<input type="radio" name="answer" value="false" /> Nej 
</div>
</div>
</script>
<%--Text values --%>
<script id="scheduled-textValues" type="text/template">
<div id="sa-row-{{id}}" class="row-fluid">
<div class="span5">{{definition.activityItemType.name}} {{definition.activityItemType.label}}</div>
<div class="span7"><input type="text" value="{{textComment}}" /></div>
</div>
</script>

<%-- This template is used to show alarms --%>
<script id="alarmPaperSheet" type="text/template">
<li id="alarmsSheet" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div id="alarmsItem" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span11">
			<div class="subRow">
				<netcare:table>
					<thead>
						<tr>
							<th><spring:message code="alarm.patient" /></th>
							<th><spring:message code="alarm.contact" /></th>
							<th><spring:message code="alarm.cause" /></th>
							<th><spring:message code="alarm.created" /></th>
							<!-- work-around (twitter bootstrap problem): hard coded width to avoid compression of icon -->
							<th width="32px">&nbsp;</th>
						</tr>
					</thead>
					<tbody></tbody>
				</netcare:table>
			</div>
		</div>
	</div>
</div>
</mvk:touch-item>
</li>
</script>
<script id="alarmRow" type="text/template">
<tr>
	<td>{{patient.name}}<br/>{{patient.civicRegistrationNumber}}</td>
	<td>{{patient.phoneNumber}}</td>
	<td>{{causeText}}</td>
	<td>{{createdTime}}</td>
	<td><img id="resolve{{id}}" src="{{contextPath}}/img/icons/24/trash.png"></td>
</tr>
</script>

<%-- This template is used to show alarms --%>
<script id="repliesPaperSheet" type="text/template">
<li id="repliesSheet" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div id="repliesItem" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span11">
			<div class="subRow">
				<netcare:table>
					<thead>
						<tr>
							<th><spring:message code="comments.comment" /></th>
							<th><spring:message code="comments.reply" /></th>
							<th><spring:message code="comments.from" />
							<th><spring:message code="comments.activity" /></th>
							<!-- work-around (twitter bootstrap problem): hard coded width to avoid compression of icon -->
							<th width="32px">&nbsp;</th>
						</tr>
					</thead>
					<tbody></tbody>
				</netcare:table>
			</div>
		</div>
	</div>
</div>
</mvk:touch-item>
</li>
</script>
<script id="repliesRow" type="text/template">
<tr>
	<td>{{comment}}</td>
	<td>{{reply}}</td>
	<td>{{repliedBy}}</td>
	<td>{{activityName}}</td>
	<td><img id="deleteComment{{id}}" src="{{contextPath}}/img/icons/24/trash.png"></td>
</tr>
</script>

<%-- Comment --%>
<script id="activity-comment" type="text/template">
<div id="activity-comment-{{id}}" class="alert alert-success activity-comment">
	<button type="button" class="close" data-dismiss="alert">×</button>
	<div class="row-fluid">
		<div class="span12">
			<div style="text-align: center;">
				<h2><i>"{{comment}}"</i></h2>
			</div>
			<div style="text-align: right;">
				<small>- {{commentedBy}}, {{commentedByCareUnit}}, {{commentedAt}}</small> 
			</div>
		</div>
	</div>
</div>
</script>

<script id="activity-comment-awarded" type="text/template">
<div id="activity-comment-{{id}}" class="alert alert-success activity-comment">
	<button type="button" class="close" data-dismiss="alert">×</button>
	<div class="row-fluid">
		<div id="activity-awards-{{id}}" class="span4">
			
		</div>
		<div class="span8">
			<div class="row-fluid">
				<div style="text-align: center;">
					<h2><i>"{{comment}}"</i></h2>
				</div>
				<div style="text-align: right;">
					<small>- {{commentedBy}}, {{commentedByCareUnit}}, {{commentedAt}}</small> 
				</div>
			</div>
			<div id="activity-comment-{{id}}-showReply" class="row-fluid" style="display: none;">
				<div style="text-align: center;">
					<h2><i>"{{reply}}"</i></h2>
				</div>
				<div style="text-align: right;">
					<small>- {{repliedBy}}, {{repliedAt}}</small>
				</div>
			</div>
			<div id="activity-comment-{{id}}-replyButton" style="text-align:center; padding-top: 20px; display: none;">
				<button type="button" class="btn">Svara</button>
			</div>
			<div id="activity-comment-{{id}}-replyForm" style="text-align:center; padding-top: 20px; display: none;">
				<textarea class="span12"></textarea>
				<button type="button" class="btn">Skicka svar</button>
			</div>
		</div>
	</div>
</div>
</script>

<!-- healthplan:templates / -->
