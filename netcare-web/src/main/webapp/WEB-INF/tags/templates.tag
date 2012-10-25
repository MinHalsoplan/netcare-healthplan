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
<!-- healthplan:templates -->
<script id="johannesid" type="text/template" class="johannes">
							<li id="item2" class="item withNavigation" style="cursor: pointer;">
								<div class="containerBoxShadow paperSlip">
									<div class="top">
										<div class="wrap"></div>
										<div class="left"></div>
										<div class="right"></div>
									</div>
									<div class="wrap">
										<div class="boxShadowBody">
											<div class="listItemBody">
												<div class="listItemBase">
													<div class="mainBody">
														<h4 class="titel">{{name}}</h4>
														<div class="subRow">Vikt | Intervall | kg</div>
													</div>
												</div>
												<div class="listItemMoveUp"></div>
												<div class="listItemMoveDown"></div>
												<div class="listItemDelete"></div>
												<a href="javascript:showYesNoContainer();" class="itemNavigation assistiveText">Uppdatera
													aktivitet</a>
											</div>
										</div>
									</div>
									<div class="bottom">
										<div class="wrap"></div>
										<div class="left"></div>
										<div class="right"></div>
									</div>
								</div>
							</li>
</script>
<script id="activityTemplate" type="text/template">
<mvk:touch-item>
<div id="item-{{id}}" class="listItemBase">
	<div class="mainBody">
		<h4 class="titel">{{name}}</h4>
		<div class="subRow">{{category.name}}</div>
	</div>
</div>
<a href="#" class="itemNavigation assistiveText"></a>
</mvk:touch-item>
</script>
<!-- healthplan:templates / -->
