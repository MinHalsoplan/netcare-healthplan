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
					$('#userprofile input[name="firstName"]').val(data.data.firstName);
					$('#userprofile input[name="surName"]').val(data.data.surName);
					$('#userprofile input[name="cnr"]').val(new NC.Util().formatCnr(data.data.civicRegistrationNumber));
					$('#userprofile input[name="email"]').val(data.data.email);
					$('#userprofile input[name="phoneNumber"]').val(data.data.phoneNumber);
					
					var mobile = data.data.mobile;
					if (mobile) {
						$('#userprofile input[name="mobile"]').attr('checked', 'checked');
						$('#userprofile input[name="pinCode"]').val(data.data.password);
						$('#userprofile input[name="pinCode2"]').val(data.data.password);
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
				$('#userprofile input:submit').click(function(event) {
					event.preventDefault();
					
					var formData = new Object();
					formData.firstName = $('#userprofile input[name="firstName"]').val();
					formData.surName = $('#userprofile input[name="surName"]').val();
					formData.email = $('#userprofile input[name="email"]').val();
					formData.phoneNumber = $('#userprofile input[name="phoneNumber"]').val();
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
				<h2><sec:authentication property="principal.name" /></h2>
				<p>
					<span class="label info"><spring:message code="information" /></span>
					På den här sidan anger du...
				</p>
				
				
				<div id="userprofile">
					<form class="form-stacked">
					
						<netcare:row>
							<netcare:col span="3">
								<netcare:field name="firstName" label="Förnamn">
									<input type="text" name="firstName" class="medium"/>
								</netcare:field>
							</netcare:col>
							<netcare:col span="3">
								<netcare:field name="surName" label="Efternamn">
									<input type="text" name="surName" class="medium"/>
								</netcare:field>
							</netcare:col>
							<netcare:col span="3">
								<netcare:field name="cnr" label="Cnr">
									<input type="text" name="cnr" disabled class="medium"/>
								</netcare:field>
							</netcare:col>
						</netcare:row>
						
						<netcare:row>
							<netcare:col span="5">
								<netcare:field name="email" label="E-post">
									<input type="email" name="email" />
								</netcare:field>
							</netcare:col>							
							<netcare:col span="5">
								<netcare:field name="phoneNumber" label="Telefonnummer">
									<input type="tel" name="phoneNumber" />
								</netcare:field>
							</netcare:col>
						</netcare:row>
						
						<br />
						
						<fieldset>
							<legend>Mobil konfiguration</legend>
							
							<netcare:row>
								<netcare:col span="12">
									<p>
									<input type="checkbox" name="mobile" value="true"> <span>Jag vill använda min mobiltelefon för att rapportera utförda aktiviteter.</span>
									</p>
								</netcare:col>
							</netcare:row>
							
							<netcare:row>
								<netcare:col span="5">
									<netcare:field name="password" label="Pin kod">
										<input type="password" name="password" />
									</netcare:field>
								</netcare:col>
								
								<netcare:col span="5">
									<netcare:field name="password2" label="Pin kod (igen)">
										<input type="password" name="password2" />
									</netcare:field>
								</netcare:col>
							</netcare:row>
						</fieldset>
						
						<div class="actions">
							<input type="submit" class="btn primary" value="Spara"/>
						</div>
						
					</form>
				</div>
				
			</section>
		</netcare:content>
	</netcare:body>
</netcare:page>