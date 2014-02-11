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

public class ReportedValue {

	private String reportedAt;
	
	private Float reportedValue;
	private Float targetValue;
	
	private Float minTargetValue;
	private Float maxTargetValue;
	
	public Float getReportedValue() {
		return reportedValue;
	}
	
	public void setReportedValue(Float reportedValue) {
		this.reportedValue = reportedValue;
	}
	
	public Float getTargetValue() {
		return targetValue;
	}
	
	public void setTargetValue(Float targetValue) {
		this.targetValue = targetValue;
	}

	public Float getMinTargetValue() {
		return minTargetValue;
	}

	public void setMinTargetValue(Float minTargetValue) {
		this.minTargetValue = minTargetValue;
	}

	public Float getMaxTargetValue() {
		return maxTargetValue;
	}

	public void setMaxTargetValue(Float maxTargetValue) {
		this.maxTargetValue = maxTargetValue;
	}

	public String getReportedAt() {
		return reportedAt;
	}

	public void setReportedAt(String reportedAt) {
		this.reportedAt = reportedAt;
	}

	@Override
	public String toString() {
		return "== REPORTED VALUE == " + reportedValue + " | " + targetValue;
	}
}
