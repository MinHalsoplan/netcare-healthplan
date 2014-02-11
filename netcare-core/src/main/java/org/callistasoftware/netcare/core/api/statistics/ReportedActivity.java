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
package org.callistasoftware.netcare.core.api.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportedActivity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	private String label;
	
	private String reportedAt;
	private String note;
	
	private List<MeasuredValue> measures;

	public ReportedActivity() {
		this.setMeasures(new ArrayList<MeasuredValue>());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<MeasuredValue> getMeasures() {
		return this.measures;
	}
	
	public void setMeasures(final List<MeasuredValue> measures) {
		this.measures = measures;
	}
	
	public String getReportedAt() {
		return reportedAt;
	}
	
	public void setReportedAt(String reportedAt) {
		this.reportedAt = reportedAt;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(final String label) {
		this.label = label;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
