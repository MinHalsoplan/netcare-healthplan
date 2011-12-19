/**
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
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
package org.callistasoftware.netcare.core.api.messages;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.i18n.LocaleContextHolder;


/**
 * Default implementation of a system message
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class DefaultSystemMessage implements SystemMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	
	public DefaultSystemMessage(final String entityCode, final String type, final Object...args) {
		final Locale l = LocaleContextHolder.getLocale();
		final ResourceBundle bundle = ResourceBundle.getBundle("messages", l);
		
		this.code = bundle.getString(entityCode);
		
		/*
		 * Format message
		 */
		final MessageFormat frm = new MessageFormat(bundle.getString(type), l);
		this.message = new StringBuilder().append(frm.format(args)).toString();
	}
	
	public DefaultSystemMessage(final String message) {
		this.message = message;
	}
	
	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
