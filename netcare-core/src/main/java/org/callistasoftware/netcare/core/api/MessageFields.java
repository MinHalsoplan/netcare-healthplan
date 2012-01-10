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
package org.callistasoftware.netcare.core.api;

import java.io.Serializable;

/**
 * Defines message keys to get localized messages (captions) from server.
 * 
 * @author Peter
 *
 */
public class MessageFields implements Serializable {
	private static final long serialVersionUID = 1L;
	private String record;
	private String[] fields;
	/**
	 * Returns the message record, i.e. prefix to key as in [record].[field].
	 * @return the message record name or null if none.
	 */
	public String getRecord() {
		return record;
	}
	
	/**
	 * Returns message field names.
	 * 
	 * @return the field names, must be defined in message.properties.
	 */
	public String[] getFields() {
		return fields;
	}
}
