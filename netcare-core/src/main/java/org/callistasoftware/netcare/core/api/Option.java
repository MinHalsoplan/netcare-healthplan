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
package org.callistasoftware.netcare.core.api;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class represents a simple option that
 * can be used from the UI. The code must exist
 * in a message bundle to lookup the calue
 * @author marcuskrantz
 *
 */
public class Option implements Comparable<Option> {

	private String code;
	private String value;
	
	public Option() {}
	
	public Option(final String code, final Locale l) {
		this.code = code;
		
		final ResourceBundle bundle = ResourceBundle.getBundle("messages");
		this.value = (l != null) ? bundle.getString(this.getCode()) : null;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(final String code) {
		this.code = code;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public int compareTo(Option o) {
		return this.getValue().compareTo(o.getValue());
	}
}
