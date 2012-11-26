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
<!-- mobile:templates -->

<!-- Activity items templates -->
<script id="measurementSingleItemTemplate" type="text/template">
	<div data-role="fieldcontain">
    	<label for="measurement{{id}}">{{definition.activityItemType.name}}</label>
    	<input type="text" name="measurement{{id}}" id="measurement{{id}}" value="{{reportedValue}}" data-mini="true" />
	</div>	
</script>
<script id="measurementIntervalItemTemplate" type="text/template">
	<div data-role="fieldcontain">
    	<label for="measurement{{id}}">{{definition.activityItemType.name}} ({{definition.minTarget}} - {{definition.maxTarget}})</label>
    	<input type="text" name="measurement{{id}}" id="measurement{{id}}" value="{{reportedValue}}" data-mini="true" />
	</div>	
</script>
<script id="estimationItemTemplate" type="text/template">
	<div data-role="fieldcontain">
		<label for="slider{{id}}">{{definition.activityItemType.name}} ({{definition.activityItemType.minScaleText}} - {{definition.activityItemType.maxScaleText}})</label>
		<input type="number" data-type="range" name="slider{{id}}" id="slider{{id}}" data-mini="true"
			value="{{perceivedSense}}" min="{{definition.activityItemType.minScaleValue}}" max="{{definition.activityItemType.maxScaleValue}}" />
	</div>
</script>
<script id="yesnoItemTemplate" type="text/template">
	<div data-role="fieldcontain">
		<fieldset data-role="controlgroup">
			<legend>{{definition.activityItemType.name}}</legend>
			<input type="radio" name="radio{{id}}" id="radioyes{{id}}" data-mini="true" />
			<label for="radioyes{{id}}">Ja</label>
			<input type="radio" name="radio{{id}}" id="radiono{{id}}" data-mini="true" />
			<label for="radiono{{id}}">Nej</label>
		</fieldset>
	</div>
</script>
<script id="textItemTemplate" type="text/template">
	<div data-role="fieldcontain">
		<label for="text{{id}}">{{definition.activityItemType.name}}</label>
		<textarea name="text{{id}}" id="text{{id}}" data-mini="true">{{textComment}}</textarea>
	</div>
</script>
<script id="commonActivityItemTemplate" type="text/template">
	<div data-role="fieldcontain">
		<label for="date"><spring:message code="mobile.report.form.date" /></label>
		<input type="date" id="date" name="date" data-mini="true" value="{{date}}" />
	</div>
	<div data-role="fieldcontain">
		<label for="time"><spring:message code="mobile.report.form.time" /></label>
		<input type="time" id="time" name="time" data-mini="true" value="{{time}}" />
	</div>
	<div data-role="fieldcontain">
		<label for="note" class="ui-input-text"><spring:message code="mobile.report.form.note" /></label>
		<textarea name="note" id="note" data-mini="true">{{note}}</textarea>
	</div>
</script>
<!-- mobile:templates / -->
