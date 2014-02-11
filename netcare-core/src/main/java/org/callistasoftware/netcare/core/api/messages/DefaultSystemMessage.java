/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
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
import java.util.ResourceBundle;

import org.springframework.context.i18n.LocaleContextHolder;


/**
 * Default implementation of a system message
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public abstract class DefaultSystemMessage implements SystemMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	
	public DefaultSystemMessage(final String type, final boolean lowerCase, final Object...args) {
		this.code = this.getResourceBundle().getString(type);
		
		final String[] messages = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Number) {
				messages[i] = args[i].toString();
			} else {
				messages[i] = lowerCase ? this.getResourceBundle().getString(args[i].toString()).toLowerCase() : this.getResourceBundle().getString(args[i].toString());;
			}
		}
		
		/*
		 * Format message
		 */
		final MessageFormat frm = new MessageFormat(this.getResourceBundle().getString(type), this.getResourceBundle().getLocale());
		this.message = new StringBuilder().append(frm.format(messages)).toString();
	}
	
	public DefaultSystemMessage(final String code) {
		final String msg = this.getResourceBundle().getString(code);
		
		this.code = msg;
		this.message = msg;
	}
	
	protected ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle("messages", LocaleContextHolder.getLocale());
	}
	
	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
	
	@Override
	public abstract MessageType getType();

}
