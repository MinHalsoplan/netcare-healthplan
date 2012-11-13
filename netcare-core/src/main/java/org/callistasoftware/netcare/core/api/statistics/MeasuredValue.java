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

import java.util.ArrayList;
import java.util.List;

import org.callistasoftware.netcare.core.api.MeasureUnit;
import org.callistasoftware.netcare.core.api.Option;

public class MeasuredValue {
	
	private String name;
	private boolean interval;
	private Option valueType;
	private MeasureUnit unit;
	private Long definitionId;
	
	private List<ReportedValue> reportedValues;
	
	public MeasuredValue() {
		this.reportedValues = new ArrayList<ReportedValue>();
	}
	
	public List<ReportedValue> getReportedValues() {
		return reportedValues;
	}

	public void setReportedValues(List<ReportedValue> reportedValues) {
		this.reportedValues = reportedValues;
	}
	
	public Option getValueType() {
		return valueType;
	}

	public void setValueType(Option valueType) {
		this.valueType = valueType;
	}

	public MeasureUnit getUnit() {
		return unit;
	}

	public void setUnit(MeasureUnit unit) {
		this.unit = unit;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInterval() {
		return interval;
	}

	public void setInterval(boolean interval) {
		this.interval = interval;
	}

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}
}
