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
<%@ taglib prefix="mvk" uri="http://www.callistasoftware.org/mvk/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!-- healthplan:templates -->
<script id="activityTemplate" type="text/template">
<li id="at{{id}}" class="item withNavigation" style="cursor: pointer;">
<mvk:touch-item>
<div id="item-{{id}}" class="listItemBase">
	<div class="mainBody">
		<h4 class="titel">{{name}}</h4>
		<div class="subRow">{{category.name}}</div>
	</div>
</div>
<a href="#" class="itemNavigation assistiveText"></a>
</mvk:touch-item>
</li>
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
<section id="head">
<h2>
	<spring:message code="template.activity.yesno.title" />
</h2>
<p>
	<span class="label label-info"><spring:message code="information" /></span>
	<spring:message code="template.activity.yesno.description" />
</p>
</section>
<section id="formSection">
<h4>
	<spring:message code="template.activity.yesno.field.label" />
	:
</h4>
<textarea id="questionId" rows="2" class="span6"></textarea>
</section>
<button onclick="showActivityTypeContainer();" class="btn btn-info">&lt;&lt; Tillbaka</button>
</script>

<%-- This template is used to show an text item form --%>

<script id="activityItemTextForm" type="text/template">
<section id="head">
<h2>
	<spring:message code="template.activity.text.title" />
</h2>
<p>
	<span class="label label-info"><spring:message code="information" /></span>
	<spring:message code="template.activity.text.description" />
</p>
</section>
<section id="formSection">
<h4>
	<spring:message code="template.activity.yesno.field.label" />
	:
</h4>
<textarea id="questionId" rows="2" class="span6"></textarea>
</section>
<button onclick="showActivityTypeContainer();" class="btn btn-info">&lt;&lt; Tillbaka</button>
</script>

<!-- healthplan:templates / -->
