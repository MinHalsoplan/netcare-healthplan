<%--

    Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>

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
<li id="liActivityItem{{=id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item-{{=id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{=name}}</h4>
			<div class="subRow">{{=category.name}}</div>
		</div>
		<div class="mainBody actionBody span6"></div>
	</div>
</div>
{{ if (planningMode) { }}
    <a href="<c:url value="/netcare/admin/healthplans/{{=planForHealthplanId}}/plan/new?template={{=id}}" />" class="itemNavigation assistiveText"></a>
{{ } else { }}
    <a href="<c:url value="/netcare/admin/template/{{=id}}" />" class="itemNavigation assistiveText"></a>
{{ } }}
</mvk:touch-item>
</li>
</script>
<%--
	Must be included within a touch-item and inserted
	after a.itemNavigation
 --%>
<script id="itemNote" type="text/template">
<div class="itemStateText">
	<div class="wrapper">{{=value}}</div>
</div>
</script>

<%-- This template is used to show a healthplan --%>
<script id="patientItem" type="text/template">
<li id="patientItem{{=id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item{{=id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{=name}}</h4>
			<div class="subRow">{{=civicRegistrationNumber}}</div>
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
<li id="healthPlanItem{{=id}}" class="item withNavigation">
<mvk:touch-item style="padding-right:0px;">
<div id="item{{=id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{=name}}</h4>
			<div class="subRow"></div>
		</div>
		<div class="mainBody actionBody span6">

		</div>
	</div>
</div>
</mvk:touch-item>
</li>
</script>

<%--Healthplan details --%>
<script id="healthPlanDetails" type="text/template">
<div id="hp-details-{{=id}}" style="display: none;">
	<div class="row-fluid">
		<div class="span12 healthplan-activity-definitions">
		
		</div>
	</div>
  {{ if (active) { }}
  <div class="healthplan-actions">
    <div class="row-fluid">
      <div class="span6">
        <a href="<c:url value='/netcare/admin/templates?healthPlan={{=id}}' />">Lägg till aktivitet från aktivitetsmall</a>
      </div>
    </div>
  </div>
  {{ } }}
  <div class="row-fluid extend">
    <div class="span6" style="margin-top: 15px;">
      {{ if (autoRenewal && active) { }}
        <input id="hp-extend-plan-{{=id}}" type="checkbox" checked="checked"/>
      {{ } else if (autoRenewal && !active) { }}
        <input id="hp-extend-plan-{{=id}}" type="checkbox" checked="checked" disabled="disabled"/>
      {{ } else if (!autoRenewal && !active) { }}
        <input id="hp-extend-plan-{{=id}}" type="checkbox" disabled="disabled"/>
      {{ } else { }}
        <input id="hp-extend-plan-{{=id}}" type="checkbox" />
      {{ } }}
      <small>Förläng automatiskt vid periodens slut</small>
    </div>
    {{ if (active && !autoRenewal) { }}
    <div class="span6 inactivate" style="text-align: right; padding-right: 20px;">
      <button class="btn" id="hp-inactivate-{{=id}}" href="#">Inaktivera hälsoplan</button>
      <div id="hp-remove-confirmation-{{=id}}" class="modal fade" style="display: none; ">
        <div class="modal-body">Är du säker att hälsoplan {{=name}} skall inaktiveras? All planering för kommande tidpunkter kommer tas bort och det går inte att rapportera tills dess att hälsoplanen aktiveras.</div>
        <div class="modal-footer">
          <a href="#" data-dismiss="modal">Avbryt</a>
          <button id="inactivate-hp-btn-{{=id}}"class="btn" data-dismiss="modal">Inaktivera</button>
        </div>
      </div>
    </div>
    {{ } else if (!active) { }}
    <div class="span6 activate" style="text-align: right; padding-right: 20px;">
      <button class="btn" id="hp-activate-{{=id}}" href="#">Aktivera hälsoplan</button>
      <div id="hp-activate-confirmation-{{=id}}" class="modal fade" style="display: none; ">
        <div class="modal-body">Är du säker att hälsoplan {{=name}} skall aktiveras? Ursprungsplaneringen för alla aktiviteter kommer aktiveras och de går att rapportera på.</div>
        <div class="modal-footer">
          <a href="#" data-dismiss="modal">Avbryt</a>
          <button id="activate-hp-btn-{{=id}}" class="btn" data-dismiss="modal">Aktivera</button>
        </div>
      </div>
    </div>
    {{ } else if (active && autoRenewal) { }}
    <div class="span6 inactivate" style="text-align: right; padding-right: 20px;">
      <button class="btn" id="hp-inactivate-{{=id}}" href="#" style="display:none;">Inaktivera hälsoplan</button>
      <div id="hp-remove-confirmation-{{=id}}" class="modal fade" style="display: none; ">
        <div class="modal-body">Är du säker att hälsoplan {{=name}} skall inaktiveras? All planering för kommande tidpunkter kommer tas bort och det går inte att rapportera tills dess att hälsoplanen aktiveras.</div>
        <div class="modal-footer">
          <a href="#" data-dismiss="modal">Avbryt</a>
          <button id="inactivate-hp-btn-{{=id}}"class="btn" data-dismiss="modal">Inaktivera</button>
        </div>
      </div>
    </div>
    {{ } }}
  </div>
</div>
</script>

<%-- Health plan activity def --%>
<script id="healthPlanDefinitions" type="text/template">
<div id="hp-ad-{{=id}}" class="row-fluid">
	<div class="span6">{{=type.name}}</div>

    {{ if (healthPlanActive) { }}
        {{ if (active) { }}
        <div class="span4"><a id="hp-ad-{{=id}}-edit" href="#">Redigera</a> | <a id="hp-ad-{{=id}}-remove" href="#">Inaktivera</a></div>
        {{ } else { }}
        <div class="span4"><a id="hp-ad-{{=id}}-activate" href="#">Aktivera</a></div>
        {{ } }}
    {{ } else { }}
        <div class="span4"><a id="hp-ad-{{=id}}-view" href="#">Se planering</a></div>
    {{ } }}

    <div id="hp-ad-remove-confirmation-{{=id}}" class="modal fade" style="display: none; ">
        <div class="modal-body">Är du säker på att aktivitet {{=type.name}} skall inaktiveras? All planering för kommande tidpunkter kommer tas bort och det går inte att rapportera tills dess att aktiviteten aktiveras.</div>
        <div class="modal-footer remove">
            <a href="#" data-dismiss="modal">Avbryt</a>
            <button id="inactivatebtn-{{=id}}" class="btn" data-dismiss="modal">Inaktivera</button>
        </div>
    </div>
    <div id="hp-ad-activate-confirmation-{{=id}}" class="modal fade" style="display: none; ">
        <div class="modal-body">Är du säker på att aktivitet {{=type.name}} skall aktiveras? Ursprungsplaneringen för aktiviteten kommer aktiveras och den går att rapportera på.</div>
        <div class="modal-footer activate">
            <a href="#" data-dismiss="modal">Avbryt</a>
            <button id="activatebtn-{{=id}}" class="btn" data-dismiss="modal">Aktivera</button>
        </div>
    </div>
</div>
</script>

<%-- Activity planning - Single value --%>
<script id="singleValue" type="text/template">
<div id="item-value-{{=id}}-container">
<div class="row-fluid">
	<div class="span10">
		<h4>{{=name}}</h4>
	</div>
	<div class="span2" style="padding-top: 10px; text-align: right;">
		<a href="#" id="field-{{=id}}-exclude">Exkludera</a>
		<a href="#" id="field-{{=id}}-include">Inkludera</a>
	</div>
</div>
<div class="row-fluid">
<div class="span12">
<div class="control-group">
	<label for="field-{{=id}}">Målvärde</label>
	<div class="controls">
		<input id="field-{{=id}}" type="text" />
		<span class="help-inline"><small>{{=unit.name}}</small></span>
	</div>
</div>
</div>
</div>
</div>
</script>

<%-- Activity planning - Interval value --%>
<script id="intervalValue" type="text/template">
<div id="item-value-{{=id}}-container">
<div class="row-fluid">
	<div class="span10">
		<h4>{{=name}}</h4>
	</div>
	<div class="span2" style="padding-top: 10px; text-align: right;">
		<a href="#" id="field-{{=id}}-exclude">Exkludera</a>
		<a href="#" id="field-{{=id}}-include">Inkludera</a>
	</div>
</div>
<div class="row-fluid">
	<div class="span6">
		<div class="control-group">
			<label for="field-{{=id}}-min">Målvärde (min)</label>
			<div class="controls">
				<input id="field-{{=id}}-min" type="text" />
				<span class="help-inline"><small>{{=unit.name}}</small></span>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
		<label for="field-{{=id}}-max">Målvärde (max)</label>
		<div class="controls">
			<input id="field-{{=id}}-max" type="text" />
			<span class="help-inline"><small>{{=unit.name}}</small></span>
		</div>
	</div>
</div>
</div>
</script>

<script id="estimationItem" type="text/template">
<div id="item-value-{{=id}}-container">
	<div class="row-fluid">
		<div class="span10">
			<h4>{{=name}} ({{=minScaleValue}} = {{=minScaleText}}, {{=maxScaleValue}} = {{=maxScaleText}})</h4>
		</div>
		<div class="span2" style="padding-top: 10px; text-align: right;">
			<a href="#" id="field-{{=id}}-exclude">Exkludera</a>
			<a href="#" id="field-{{=id}}-include">Inkludera</a>
		</div>
	</div>
</div>
</script>

<script id="yesNoItem" type="text/template">
<div id="item-value-{{=id}}-container">
	<div class="row-fluid">
		<div class="span10">
			<h4>{{=question}}</h4>
		</div>
		<div class="span2" style="padding-top: 10px; text-align: right;">
			<a href="#" id="field-{{=id}}-exclude">Exkludera</a>
			<a href="#" id="field-{{=id}}-include">Inkludera</a>
		</div>
	</div>
</div>
</script>

<script id="textItem" type="text/template">
<div id="item-value-{{=id}}-container">
	<div class="row-fluid">
		<div class="span10">
			<h4>{{=label}}</h4>
		</div>
		<div class="span2" style="padding-top: 10px; text-align: right;">
			<a href="#" id="field-{{=id}}-exclude">Exkludera</a>
			<a href="#" id="field-{{=id}}-include">Inkludera</a>
		</div>
	</div>
</div>
</script>

<%-- This template is used to show an activity type item --%>
<script id="activityItemTemplate" type="text/template">
<li id="liActivityItem{{=id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item{{=id}}" class="listItemBase">
	<div class="mainBody">
		<h4 class="titel">{{=name}}</h4>
		<div class="subRow">{{=details}}</div>
	</div>
	<div id="item{{=id}}moveUp" class="listItemMoveUp"></div>
	<div id="item{{=id}}moveDown" class="listItemMoveDown"></div>
	<div id="item{{=id}}delete" class="listItemDelete"></div>
</div>
<a id="item{{=id}}showDetails" href="#" class="itemNavigation assistiveText"></a>
</mvk:touch-item>
</li>
</script>

<%-- This template is used to show a measurement item form --%>

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
	<input type="text" class="span6" name="activityItemName" id="activityItemName" value="{{=name}}"></input>
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
    <button id="backButtonForm" class="btn btn-info"><spring:message code="template.save" /></button>
    <button id="cancelButtonForm" class="btn btn-info"><spring:message code="template.cancel" /></button>
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
	<input type="text" class="span6" name="activityItemName" id="activityItemName" value="{{=name}}"></input>
</div>
<div class="controls">
	<label><spring:message code="estimation.scale.label" /></label>
</div>
<div class="controls controls-row">
	<spring:message code="estimation.lowValue.label" />
	<input type="text" name="minScaleValue" id="minScaleValue" class="span1 estvalue" value="{{=minScaleValue}}"></input>
	<span style="padding-left:20px;"><spring:message code="estimation.highValue.label" /></span>
	<input type="text" name="maxScaleValue" id="maxScaleValue" class="span1 estvalue" value="{{=maxScaleValue}}"></input>
</div>
<div class="controls">
	<label for="minScaleText"><spring:message code="estimation.lowText.label" /></label>
	<input type="text" class="span6" name="minScaleText" id="minScaleText" value="{{=minScaleText}}"></input>
</div>
<div class="controls">
	<label for="maxScaleText"><spring:message code="estimation.highText.label" /></label>
	<input type="text" class="span6" name="maxScaleText" id="maxScaleText" value="{{=maxScaleText}}"></input>
</div>
<div class="span12" style="padding-top:20px;">
	<button id="backButtonForm" class="btn btn-info"><spring:message code="template.save" /></button>
    <button id="cancelButtonForm" class="btn btn-info"><spring:message code="template.cancel" /></button>
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
	<input type="text" class="span6" name="activityItemName" id="activityItemName" value="{{=name}}"></input>
</div>
<div class="controls">
	<label for="yesNoQuestion"><spring:message code="template.activity.yesno.question.label" /></label>
	<textarea id="yesNoQuestion" rows="2" class="span6">{{=question}}</textarea>
</div>
<div class="span12" style="padding-top:20px;">
	<button id="backButtonForm" class="btn btn-info"><spring:message code="template.save" /></button>
    <button id="cancelButtonForm" class="btn btn-info"><spring:message code="template.cancel" /></button>
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
	<input type="text" class="span6" name="activityItemName" id="activityItemName" value="{{=name}}"></input>
</div>
<div class="controls">
	<label for="textLabel"><spring:message code="template.activity.text.label.label" /></label>
	<textarea id="textLabel" rows="2" class="span6">{{=label}}</textarea>
</div>
<div class="span12" style="padding-top:20px;">
	<button id="backButtonForm" class="btn btn-info"><spring:message code="template.save" /></button>
    <button id="cancelButtonForm" class="btn btn-info"><spring:message code="template.cancel" /></button>
</div>
</div>
</script>

<script id="measureUnitRow" type="text/template">
<tr id="measure-unit-{{=id}}">
	<td>{{=dn}}</td>
	<td>{{=name}}</td>
	<td><img id="measure-unit-{{=id}}-edit" src="<c:url value="/img/icons/24/edit.png" />" style="cursor: pointer;" /></td>
</tr>
</script>

<script id="accessLevelsRadioOption" type="text/template">
	<input id="access-level-{{=code}}" type="radio" name="accessLevel" value="{{=code}}" />
	<label for="access-level-{{=code}}" style="display: inline; margin-right: 20px;">{{=value}}</label>
</script>

<script id="scheduledItem" type="text/template">
<li id="scheduled-activity-{{=id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item-{{=id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{=type.name}}</h4>
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
<li id="reportedActivityItem{{=id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div id="raItem{{=id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span8">
			<h4 class="titel">{{=patient.surName}}, {{=patient.firstName}}</h4>
			<div class="subRow">{{=activityDefinition.type.name}} - Rapporterad {{=reported}}</div>
		</div>
		<div class="mainBody actionBody span4">

		</div>
	</div>
</div>
</mvk:touch-item>
</li>
</script>

<%--Reported Activity details --%>
<script id="reportedActivityDetails" type="text/template">
<div id="ra-details-{{=id}}" class="item-with-form" style="display: none; margin-right: 15px;">
	<div class="row-fluid">
		<div class="span12">
		</div>
	</div>
	<div class="row-fluid commentReported">	
		<div class="span5">Kommentar</div>
		<div class="span7" style="margin-left:0px;">{{=note}}</div>
	</div>
	<div class="row-fluid">	
		<div class="likeReported span4"></div><div class="markReported span7"></div>
	</div>
	<div class="row-fluid">	
		<div id="actcomment" class="span11">
			<label for="activitycomment"><spring:message code="comments.sendComment" /></label>
			<textarea id="activitycomment" rows="2" class="span11"></textarea>
			<button class="btn"><spring:message code="report.save" /></button>
		</div>
		<div id="actcommented" class="span11" style="display:none;padding-bottom: 5px;">
		</div>
		<div id="actreplied" class="span11" style="display:none;padding-bottom: 5px;">
		</div>
	</div>
</div>
</script>

<%--Reported Activity values --%>
<%--Estimation values --%>
<script id="estimationValues" type="text/template">
  {{ if (definition.active) { }}
    <div id="ra-row-{{=id}}" class="row-fluid">
      <div class="span5"><span>{{=definition.activityItemType.name}}
      {{=definition.activityItemType.minScaleValue}} ({{=definition.activityItemType.minScaleText}}) - {{=definition.activityItemType.maxScaleValue}} ({{=definition.activityItemType.maxScaleText}})</span></div>
      <div class="span5">{{=perceivedSense}}</div>
    </div>
  {{ } }}
</script>
<%--Measurement values --%>
<script id="measurementSingleValues" type="text/template">
  {{ if (definition.active) { }}
    <div id="ra-row-{{=id}}" class="row-fluid">
      <div class="span5"><span>{{=definition.activityItemType.name}} ({{=definition.target}} {{=definition.activityItemType.unit.name}})</span></div>
      <div class="span5">{{=reportedValue}} {{=definition.activityItemType.unit.name}}</div>
    </div>
  {{ } }}
</script>
<script id="measurementIntervalValues" type="text/template">
  {{ if (definition.active) { }}
    <div id="ra-row-{{=id}}" class="row-fluid">
      <div class="span5"><span>{{=definition.activityItemType.name}} ({{=definition.minTarget}} - {{=definition.maxTarget}} {{=definition.activityItemType.unit.name}})</span></div>
      <div class="span5">{{=reportedValue}} {{=definition.activityItemType.unit.name}}</div>
    </div>
  {{ } }}
</script>
<%--Reported Activity values --%>
<script id="yesnoValues" type="text/template">
  {{ if (definition.active) { }}
    <div id="ra-row-{{=id}}" class="row-fluid">
      <div class="span5"><span>{{=definition.activityItemType.name}} {{=definition.activityItemType.question}}
      </span></div>
      <div class="span5">{{=answer?'Ja':'Nej'}}</div>
    </div>
  {{ } }}
</script>
<%--Reported Activity values --%>
<script id="textValues" type="text/template">
  {{ if (definition.active) { }}
    <div id="ra-row-{{=id}}" class="row-fluid">
      <div class="span5"><span>{{=definition.activityItemType.label}}</span></div>
      <div class="span5">{{=textComment}}</div>
    </div>
  {{ } }}
</script>

<%-- This template is used to show a scheduled activity --%>
<script id="scheduledActivityItem" type="text/template">
<li id="scheduledActivityItem{{=id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div id="saItem{{=id}}" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{=activityDefinition.type.name}}</h4>
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
<div id="sa-details-{{=id}}" class="item-with-form" style="display: none; margin-right: 15px;">
	{{ if (reportedDate && !reportingPossible) { }}
	<div class="alert alert-info">
		<i>Den här aktiviteten är stängd för rapportering eftersom du redan har rapporterat den.</i>
	</div>
	{{ } if (!reportedDate && !reportingPossible) { }}
	<div class="alert alert-info">
		<i>Den här aktiviteten är inte öppen för rapportering ännu.</i>
	</div>
	{{ } }}

	<div class="row-fluid">
		<div class="span12 sa-details">
		</div>
	</div>
	<div class="row-fluid external">
		<div class="span5">
			<span>Ange tidpunkt då aktiviteten utfördes</span>
		</div>
        <div class="span7">
            <input id="{{=id}}-report-date" type="text" style="display: inline;" class="dateInput previous-7 upcoming-0 input-small" readonly="true"/>
            <input id="{{=id}}-report-time" type="text" style="display: inline;" class="input-mini timeInput" placeholder="TT:MM"/>
        </div>
	</div>
	<div class="row-fluid external">
		<div class="span5">
			<span>Övriga kommentarer</span>
		</div>
		<div class="span7">
			<textarea id="{{=id}}-report-note" class="span11" maxlength="500">{{=note}}</textarea>
		</div>
	</div>
	{{ if (reportingPossible) { }}
	<div class="form-actions" style="margin: 0">
		<button id="sa-report-{{=id}}" type="button" class="btn btn-primary btn-info">Rapportera</button>
		<button id="sa-noreport-{{=id}}" type="button" class="btn btn-danger">Rapportera som ej utförd</button>
	</div>
	{{ } }}
</div>
</script>

<%--Scheduled Activity values --%>
<%--Estimation values --%>
<script id="scheduled-estimationValues" type="text/template">
<div id="sa-row-{{=id}}" class="row-fluid">
<div class="span5"><span>{{=definition.activityItemType.name}}</span></div>
<div class="span5">
	<netcare:row>
		<netcare:col span="2" style="text-align: right;">
			<span style="display: inline;">{{=definition.activityItemType.minScaleText}} </span>
		</netcare:col>
		<netcare:col span="8">
			<input type="hidden" value="{{=perceivedSense}}" />
			<div id="sa-row-slider-{{=id}}"></div>
		</netcare:col>
		<netcare:col span="2" style="text-align: left;">
			<span style="display: inline;"> {{=definition.activityItemType.maxScaleText}}</span>
		</netcare:col>
	</netcare:row>
</div>
</div>
</script>
<%--Measurement values --%>
<script id="scheduled-measurementSingleValues" type="text/template">
<div id="sa-row-{{=id}}" class="row-fluid">
<div class="span5"><span>{{=definition.activityItemType.name}} ({{=definition.target}} {{=definition.activityItemType.unit.name}})</span></div>
<div class="span7">
	<input type="text" value="{{=reportedValue}}" class="decimalNumber"/>
	<span style="display: inline;">{{=definition.activityItemType.unit.name}}</span>
</div>
</div>
</script>
<script id="scheduled-measurementIntervalValues" type="text/template">
<div id="sa-row-{{=id}}" class="row-fluid">
<div class="span5"><span>{{=definition.activityItemType.name}} ({{=definition.minTarget}} - {{=definition.maxTarget}} {{=definition.activityItemType.unit.name}})</span></div>
<div class="span7">
	<input type="text" value="{{=reportedValue}}" class="decimalNumber" />
	<span style="display: inline;">{{=definition.activityItemType.unit.name}}</span>
</div>
</div>
</script>
<%--Yes/No values --%>
<script id="scheduled-yesnoValues" type="text/template">
<div id="sa-row-{{=id}}" class="row-fluid">
<div class="span5"><span>{{=definition.activityItemType.name}} {{=definition.activityItemType.question}}</span></div>
<div class="span7">
	<input type="radio" name="answer{{=id}}" value="true" checked="{{=answer}}" /> Ja
	<input type="radio" name="answer{{=id}}" value="false" checked="{{=answer}}" /> Nej 
</div>
</div>
</script>
<%--Text values --%>
<script id="scheduled-textValues" type="text/template">
<div id="sa-row-{{=id}}" class="row-fluid">
<div class="span5"><span>{{=definition.activityItemType.label}}</span></div>
<div class="span7"><textarea class="span11">{{=textComment}}</textarea></div>
</div>
</script>

<%-- This template is used to show alarms --%>
<script id="alarmPaperSheet" type="text/template">
<li id="alarmsSheet" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div id="alarmsItem" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span12">
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
	<td>{{=patient.name}}<br/>{{=patient.civicRegistrationNumber}}</td>
	<td>{{=patient.phoneNumber}}</td>
	<td>{{=causeText}}</td>
	<td>{{=createdTime}}</td>
	<td><img id="resolve{{=id}}" src="{{=contextPath}}/img/icons/24/trash.png"></td>
</tr>
</script>

<%-- This template is used to show alarms --%>
<script id="repliesPaperSheet" type="text/template">
<li id="repliesSheet" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div id="repliesItem" class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span12">
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
	<td>{{=comment}}</td>
	<td>{{=reply}}</td>
	<td>{{=repliedBy}}</td>
	<td>{{=activityName}}</td>
	<td><img id="hideComment{{=id}}" src="{{=contextPath}}/img/icons/24/trash.png"></td>
</tr>
</script>

<%-- Comment --%>
<script id="activity-comment" type="text/template">
<div id="activity-comment-{{=id}}" class="alert alert-success activity-comment">
	<button type="button" class="close" data-dismiss="alert">×</button>
	<div class="row-fluid">
		<div id="activity-awards-{{=id}}" class="span2"></div>
		<div class="span10">
			<div class="row-fluid">
				<div style="text-align: center;">
					<h2><i>{{=(comment!==null && comment!=='')?'"' + comment + '"':''}}</i></h2>
				</div>
				<div style="text-align: right;">
					<small>- {{=commentedBy}}, {{=commentedByCareUnit}}, {{=commentedAt}}</small> 
				</div>
			</div>
			<div id="activity-comment-{{=id}}-showReply" class="row-fluid" style="display: none;">
				<div style="text-align: center;">
					<h2><i>"{{=reply}}"</i></h2>
				</div>
				<div style="text-align: right;">
					<small>- {{=repliedBy}}, {{=repliedAt}}</small>
				</div>
			</div>
			<div id="activity-comment-{{=id}}-replyButton" style="text-align:center; padding-top: 20px; display: none;">
				<button type="button" class="btn">Svara</button>
			</div>
			<div id="activity-comment-{{=id}}-replyForm" style="text-align:center; padding-top: 20px; display: none;">
				<textarea class="span12"></textarea>
				<button type="button" class="btn">Skicka svar</button>
			</div>
		</div>
	</div>
</div>
</script>

<script id="patient-schema" type="text/template">
<li id="activityItem{{=id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item style="padding-right:0px;">
<div class="listItemBase">
	<div class="row-fluid">
		<div class="mainBody span6">
			<h4 class="titel">{{=type.name}}</h4>
			<div class="subRow">
				{{=healthPlanName}}, <i>{{=healthPlanCareUnit.name}}</i>
			</div>
		</div>
		<div class="span6" style="text-align:right; margin-top: 15px; padding-right: 20px;">
      {{ if (healthPlanActive) { }}
			<small>Pågår till och med: <strong>{{=period}}</strong></small>
      {{ } else { }}
      <small>Inaktiv</small>
      {{ } }}
		</div>
	</div>
	<div class="row-fluid" style="border-top: 1px dotted #eee; padding-top: 15px;">
		<div class="mainBody span12">
			<h4>Inställningar</h4>
			<div class="subRow">
        {{ if (healthPlanActive) { }}
				<input id="activityItem{{=id}}-reminder" type="checkbox" />
        {{ } else { }}
        <input id="activityItem{{=id}}-reminder" type="checkbox" disabled="disabled"/>
        {{ } }}
				<label for="activityItem{{=id}}-reminder" style="display: inline;"><small>Skicka en påminnelse till min mobiltelefon när denna aktivitet skall utföras.</small></label>
			</div>
		</div>
	</div>
	<div class="row-fluid" style="border-top: 1px dotted #eee; padding-top: 15px;">
		<div class="span12">
			<h4>Schema</h4>
			<span id="schedule-{{=id}}-repeat"><i><small></small></i></span>
			<div style="margin-right: 15px;">
				<table id="planned-times-{{=id}}" class="table table-striped table-condensed">
					<thead><tr>
						<th>Må</th>
						<th>Ti</th>
						<th>On</th>
						<th>To</th>
						<th>Fr</th>
						<th>Lö</th>
						<th>Sö</th>
					</tr></thead>
					<tbody></tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="row-fluid" style="border-top: 1px dotted #eee; padding-top: 15px;">
		<div class="span12">
			<h4>Andel genomförda aktiviteter</h4>
			{{ if(numExtra > 0) { }}
				<small>Du har genomfört <strong id="numDone1-{{=id}}">{{=numDone}}</strong> (+ <strong id="numExtra-{{=id}}">{{=numExtra}}</strong> extra) av <strong id="numTarget-{{=id}}">{{=numTarget}}</strong> <i><strong>fram till idag</strong></i>.</small>
			{{ } else { }}
				<small>Du har genomfört <strong id="numDone1-{{=id}}">{{=numDone}}</strong> av <strong id="numTarget-{{=id}}">{{=numTarget}}</strong> <i><strong>fram till idag</strong></i>.</small>
			{{ } }}
			<div class="progress progress-info" style="margin-right: 15px;">
				<div id="targetDone-{{=id}}" class="bar" style="width: {{=targetDone}}%">{{=targetDone}}%</div>
			</div>

			<small>Du har genomfört <strong id="numDone2-{{=id}}">{{=numDone}}</strong> av <strong><i>hela hälsoplanens</i></strong> <strong id="numTotal-{{=id}}">{{=numTotal}}</strong> aktiviteter.</small>
			<div class="progress progress-info" style="margin-right: 15px;">
				<div id="totalDone-{{=id}}" class="bar" style="width: {{=totalDone}}%">{{=totalDone}}%</div>
			</div>
		</div>
	</div>
</div>
</mvk:touch-item>
</li>
</script>

<script id="paginationItem" type="text/template">
	<li id="{{=prefix}}-{{=page}}"><a href="#">{{=text}}</a></li> 
</script>

<script id="activityResultItem" type="text/template">
	<li id="activityResultItem-{{=id}}" class="item withNavigation" style="cursor: pointer;">
		<mvk:touch-item>
			<div id="result-item-{{=id}}" class="listItemBase">
				<div class="mainBody">
					{{ if (numExtra > 0) { }}
						<h4 class="titel">{{=type.name}} | {{=numDone}} (+ {{=numExtra}} extra) rapporteringar</h4>
					{{ } else { }}
						<h4 class="titel">{{=type.name}} | {{=numDone}} rapporteringar</h4>
					{{ } }}
					<div class="subRow">{{=cs}}</div>
				</div>
			</div>
			<a id="result-item-{{=id}}-showResults" href="#" class="itemNavigation assistiveText"></a>
		</mvk:touch-item>
	</li>
</script>

<script id="reportHead" type="text/template">
	<h3>{{=label}}</h3>
</script>

<script id="yesNoReportRow" type="text/template">
	<h4>{{=question}}</h4>
    <div class="progress">
        <div class="bar bar-info" style="width: {{=percentYes}}%;">Ja {{=percentYes}}%</div>
        <div class="bar bar-warning" style="width: {{=percentNo}}%;">Nej {{=percentNo}}%</div>
    </div>
</script>

<script id="textReportRow" type="text/template">
	<tr id='{{=divId}}'>
		<td>{{=date}}</td>
		<td>{{=text}}</td>
	</tr>
</script>

<script id="myModal" type="text/template">
<div id="myModalItems" style="display: none;" class="modal" tabindex="-1" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="myModalLabel">Modal header</h3>
  </div>
  <div class="modal-body">
    <p>One fine body…</p>
  </div>
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Stäng</button>
  </div>
</div>
</script>

<script id="careUnitItem" type="text/template">
	<li id="careUnitItem-{{=id}}" class="item withNavigation" style="cursor: pointer;">
		<mvk:touch-item>
			<div id="careunit-item-{{=id}}" class="listItemBase">
				<div class="mainBody">
					<h4 class="titel">{{=name}}</h4>
					<div class="subRow">{{=countyCouncil.name}}</div>
				</div>
			</div>
			<a id="result-item-{{=id}}-showResults" href="#" class="itemNavigation assistiveText"></a>
		</mvk:touch-item>
	</li>
</script>

<!-- printing header -->
<script id="printHeader" type="text/template">
  <div class="onlyInPrintMode">
    <h2>{{=title}}</h2>
    <netcare:row>
      <netcare:col span="12">
        <netcare:table>
          <tbody>
          <tr>
            <td>Patient:</td>
            <td>{{=name}}</td>
          </tr>
          <tr>
            <td>Personnummer:</td>
            <td>{{=crn}}</td>
          </tr>
          <tr>
            <td>Hälsoplan:</td>
            <td>{{=healthPlanName}}</td>
          </tr>
          <tr>
            <td>Aktivitet:</td>
            <td>{{=activityName}}</td>
          </tr>
          <tr>
            <td>Vårdenhet:</td>
            <td>{{=careUnitName}}</td>
          </tr>
          </tbody>
        </netcare:table>
      </netcare:col>
    </netcare:row>
  </div>
</script>

<!-- healthplan:templates / -->
