/**
 * Copyright (C) 2011,2012 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.callistasoftware.netcare.core.spi.impl;

import org.callistasoftware.netcare.core.spi.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

	private static Logger log = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);
	
	@Value("${support.email}")
	private String supportEmail;
	
	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendSupportEmail(String message) {
		this.doSendEmail(message, "NETCARE ERROR", this.supportEmail);
	}
	
	private void doSendEmail(final String message, final String subject, final String toAddress) {
		log.info("Delivering email message '{}' to {}", subject, toAddress);
		
		final SimpleMailMessage smm = new SimpleMailMessage();
		smm.setTo(toAddress);
		smm.setSubject(subject);
		smm.setText(message);
		
		try {
			this.mailSender.send(smm);
		} catch (final MailException e) {
			log.warn("Could not deliver email message. Reason: {}", e.getMessage());
		}
	}
}
