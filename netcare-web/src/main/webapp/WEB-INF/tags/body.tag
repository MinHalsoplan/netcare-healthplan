<%--

    Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>

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
<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ attribute name="singleColumn" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<body>
	<div class="container">
		<div class="content">
			<div class="page-header">
				<h1>Planerade hälsotjänster <small>Tagline text kan skrivas här</small></h1>
			</div>
			<div class="row">
				<div class="span10">
					<jsp:doBody />
				</div>
				<c:if test="${empty singleColumn}">
					<div class="span4">
						<h3>Vad vill du göra?</h3>
					</div>
				</c:if>
			</div>
		</div>
		<footer>
			<p>Callista Software</p>
		</footer>
	</div>
	
</body>
