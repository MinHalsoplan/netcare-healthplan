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
package org.callistasoftware.netcare.core.api.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.callistasoftware.netcare.core.api.Pair;

public class ReportedActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	private Float goal;

	private List<Pair<String, Float>> reportedValues;

	public ReportedActivity() {
		this.setReportedValues(new ArrayList<Pair<String,Float>>());
	}
	
	public Float getGoal() {
		return goal;
	}

	public void setGoal(Float goal) {
		this.goal = goal;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Pair<String, Float>> getReportedValues() {
		return reportedValues;
	}

	public void setReportedValues(List<Pair<String, Float>> reportedValues) {
		this.reportedValues = reportedValues;
	}
}
