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

import java.io.Serializable;

/**
 * Keeps a reported value.
 * 
 * @author Peter
 */
public class Value implements Serializable {
	private static final long serialVersionUID = 1L;
	private int seqno;
	private float value;
	
	public Value() {}
	
	//
	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}

	//
	public void setValue(float value) {
		this.value = value;
	}


	/**
	 * Returns the sequence number. {@link MeasurementEntity}
	 */
	public int getSeqno() {
		return seqno;
	}

	/**
	 * Returns the actual value.
	 */
	public float getValue() {
		return value;
	}
}
