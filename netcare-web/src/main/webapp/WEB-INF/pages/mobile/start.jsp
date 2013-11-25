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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mobile" tagdir="/WEB-INF/tags/mobile"%>

<!DOCTYPE html>
<html>
<mobile:header>
	<mobile:templates />
	<script type="text/javascript">

		$(document).bind('mobileinit', function() {
			$.mobile.defaultPageTransition = 'none';
      jQuery.extend(jQuery.mobile.datebox.prototype.options, {
        overrideDateFormat: 'yyyy-mm-dd'
      });
		});
		
		$(document).bind('ready', function() {
			NC_MOBILE.ACTIVITIES.init();
		});

	</script>
</mobile:header>
<body>
	<div data-role="page" id="start" data-external-page="true">
		<div data-role="header" id="today-header" data-theme="c" data-position="fixed">
      <a href="#" id="logout">Logga ut</a>
			<h1>
				<spring:message code="mobile.activity.title" />
			</h1>
			<a href="#" id="refresh" data-icon="refresh" data-iconpos="notext"></a>
		</div>
		<div id="today-body" data-role="content-primary">
			<ul id="schema" data-role="listview">
			</ul>
		</div>
		<div id="nc-footer" data-role="footer" data-theme="c" data-position="fixed">
			<div data-role="navbar" class="ui-navbar">
				<ul>
					<li><a id="actual" href="#" data-icon="home" class="ui-btn-active"><spring:message
								code="mobile.activity.active" /></a></li>
					<li><a id="due" href="#" data-icon="alert"><spring:message code="mobile.activity.unfinished" /></a></li>
					<li><a id="reported" href="#" data-icon="check"><spring:message code="mobile.activity.done" /></a></li>
				</ul>
			</div>
		</div>
	</div>
	<div data-role="page" id="report" data-external-page="true" data-add-back-btn="true">
		<div id="report-header" data-role="header" data-theme="c" data-position="fixed">
			<a href="#" data-rel="back" id="goBack" data-icon="arrow-l">Tillbaka</a>
			<h1></h1>
			<a href="#" data-rel="back" id="sendReport"><spring:message code="mobile.report.title" /></a>
		</div>
		<div id="report-body" data-role="content-primary">
			<div class="ui-bar">
				<h3></h3>
				<p></p>
			</div>
			<div class="ui-body ui-body-d">
				<form id="reportForm" method="post">
				</form>
			</div>
		</div>
	</div>
  <div data-role="page" id="blank">
    <div align="center" style="margin-top: 40px;">Utloggad</div>
  </div>
</body>
</html>
