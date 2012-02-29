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
package org.callistasoftware.netcare.core.api.impl;

import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityReport;
import org.callistasoftware.netcare.core.api.Value;

/**
 * Keeps report information.
 * 
 * @author Peter
 */
public class ActivityReportImpl implements ActivityReport {
	private static final long serialVersionUID = 1L;
	private List<Value> values;	
	private String actualDate;
	private String actualTime;
	private int sense;
	private String note;
	private boolean rejected;	
	
	
	//
	public ActivityReportImpl() {}
	
	
	public void setValues(List<Value> values) {
		this.values = values;
	}

	public void setActualDate(String actualDate) {
		this.actualDate = actualDate;
	}

	public void setActualTime(String actualTime) {
		this.actualTime = actualTime;
	}

	public void setSense(int sense) {
		this.sense = sense;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	@Override
	public List<Value> getValues() {
		return values;
	}

	@Override
	public String getActualDate() {
		return actualDate;
	}

	@Override
	public String getActualTime() {
		return actualTime;
	}

	@Override
	public int getSense() {
		return sense;
	}

	@Override
	public String getNote() {
		return note;
	}

	@Override
	public boolean isRejected() {
		return rejected;
	}

}
