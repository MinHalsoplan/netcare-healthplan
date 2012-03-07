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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="netcare" tagdir="/WEB-INF/tags" %>

<netcare:page>
	<netcare:header>
		<script type="text/javascript">
			$(function() {
				
				/*
				 * Load patient
				 */
				var patientId = <sec:authentication property="principal.id" />;
				var serviceClient = new NC.Patient();
				
				var updateForm = function(data) {
					$('#userprofile input[name="firstname"]').val(data.data.firstName);
					$('#userprofile input[name="surname"]').val(data.data.surName);
					$('#userprofile input[name="cnr"]').val(new NC.Util().formatCnr(data.data.civicRegistrationNumber));
					$('#userprofile input[name="email"]').val(data.data.email);
					$('#userprofile input[name="phone"]').val(data.data.phoneNumber);
					
					var mobile = data.data.mobile;
					if (mobile) {
						$('#userprofile input[name="mobile"]').attr('checked', 'checked');
						$('#userprofile input[name="password"]').val(data.data.password);
						$('#userprofile input[name="password2"]').val(data.data.password);
					} else {
						$('#userprofile input[type="password"]').attr('disabled', 'disabled');
					}
				}
				
				serviceClient.loadSingle(patientId, function(data) {
					NC.log("Patient loaded...");
					updateForm(data);
				});
				
				$('#userprofile input[name="mobile"]').click(function() {
					
					if ($(this).attr('checked') == 'checked') {
						$('#userprofile input[type="password"]').removeAttr('disabled');
					} else {
						$('#userprofile input[type="password"]').attr('disabled', 'disabled');
					}
					
				});
				
				/*
				 * Form submission
				 */
				$('#userprofile :submit').click(function(event) {
					event.preventDefault();
					
					var formData = new Object();
					formData.firstname = $('#userprofile input[name="firstname"]').val();
					formData.surname = $('#userprofile input[name="surname"]').val();
					formData.email = $('#userprofile input[name="email"]').val();
					formData.phone = $('#userprofile input[name="phone"]').val();
					formData.mobile = $('#userprofile input[name="mobile"]:checked').val();
					formData.password = $('#userprofile input[name="password"]').val();
					formData.password2 = $('#userprofile input[name="password2"]').val();
					
					if (formData.mobile == "true") {
						
						if (formData.password !== formData.password2) {
							NC.log("Password mismatch. Add error handling...");
						}
						
					}
					
					serviceClient.update(patientId, formData, function(data) {
						updateForm(data);
					});
					
					
				});
			});
		</script>
	</netcare:header>
	<netcare:body>
		<netcare:content>
			<section id="profile">
				<sec:authentication property="principal.name" var="currentPrincipal" scope="page"/>
				<spring:message code="profile.update" var="update" scope="page" />
				
				<h2><spring:message code="profile.title" arguments="${currentPrincipal}"/></h2>
				<p>
					<span class="label label-info"><spring:message code="information" /></span>
					<spring:message code="profile.desc" arguments="${update}"/>
				</p>
				
				
				<div id="userprofile">
					<form>
					
						<netcare:row>
							<netcare:col span="2">
								<spring:message code="profile.crn" var="crn" scope="page" />
								<netcare:field name="cnr" label="${crn}">
									<input type="text" name="cnr" disabled class="span2"/>
								</netcare:field>
							</netcare:col>
						</netcare:row>
					
						<netcare:row>
							<netcare:col span="3">
								<spring:message code="profile.firstName" var="firstName" scope="page" />
								<netcare:field name="firstName" label="${firstName}">
									<input type="text" name="firstname" class="medium"/>
								</netcare:field>
							</netcare:col>
							<netcare:col span="3">
								<spring:message code="profile.surName" var="surName" scope="page" />
								<netcare:field name="surName" label="${surName}">
									<input type="text" name="surname" class="medium"/>
								</netcare:field>
							</netcare:col>
						</netcare:row>
						
						<netcare:row>
							<netcare:col span="3">
								<spring:message code="profile.email" var="email" scope="page" />
								<netcare:field name="email" label="${email}">
									<input type="email" name="email" />
								</netcare:field>
							</netcare:col>							
							<netcare:col span="2">
								<spring:message code="profile.phone" var="phone" scope="page" />
								<netcare:field name="phone" label="${phone}">
									<input type="tel" name="phone" class="span2"/>
								</netcare:field>
							</netcare:col>
						</netcare:row>
						
						<br />
						
						<fieldset>
							<legend><spring:message code="profile.mobile.title" /></legend>
							
							<netcare:row>
								<netcare:col span="5">
									<p>
									<input type="checkbox" name="mobile" value="true"> <span><spring:message code="profile.mobile.enable" /></span>
									</p>
								</netcare:col>
							</netcare:row>
							
							<netcare:row>
								<netcare:col span="1">
									<spring:message code="profile.mobile.pin" var="pin" scope="page" />
									<netcare:field name="password" label="${pin}">
										<input type="password" name="password" class="span1" />
									</netcare:field>
								</netcare:col>
								
								<netcare:col span="2">
									<spring:message code="profile.mobile.pinRepeat" var="pin2" scope="page" />
									<netcare:field name="password2" label="${pin2}">
										<input type="password" name="password2" class="span1" />
									</netcare:field>
								</netcare:col>
							</netcare:row>
						</fieldset>
						
						<div class="form-actions">
							<button type="submit" class="btn btn-primary"><spring:message code="profile.update" /></button>
							<button type="reset" class="btn"><spring:message code="clear" /></button>
						</div>
						
					</form>
				</div>
				
			</section>
		</netcare:content>
	</netcare:body>
</netcare:page>