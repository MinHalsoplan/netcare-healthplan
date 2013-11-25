/**
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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

import org.callistasoftware.netcare.core.api.ReportingValues;
import org.callistasoftware.netcare.model.entity.ActivityItemValuesEntity;
import org.callistasoftware.netcare.model.entity.EstimationEntity;
import org.callistasoftware.netcare.model.entity.MeasurementEntity;
import org.callistasoftware.netcare.model.entity.MeasurementValueType;
import org.callistasoftware.netcare.model.entity.TextEntity;
import org.callistasoftware.netcare.model.entity.YesNoEntity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportingValuesImpl implements ReportingValues {

	private String id;
	private String type;
	private String subtype;

	private Object[] reportedValues;
	private Object[] targets;
	private Object[] minTargets;
	private Object[] maxTargets;

	private String label;
	private String unit;

	private String question;
	private int yes;
	private int no;

	private int index = 0;

	public ReportingValuesImpl(Long id, String type, int size) {
		this.id = id.toString();
		this.type = type;
		this.reportedValues = new Object[size];
	}

	public ReportingValuesImpl(Long id, String type, String label, int size) {
		this(id, type, size);
		this.label = label;
	}

	/**
	 * Used by YesNoQuestions.
	 * 
	 * @param id
	 * @param type
	 * @param label
	 * @param question
	 */
	public ReportingValuesImpl(Long id, String type, String label, String question) {
		this(id, type, label, 0);
		this.question = question;
		this.yes = 0;
		this.no = 0;
	}

	public ReportingValuesImpl(Long id, String type, String label, String unit, int size) {
		this(id, type, label, size);
		this.unit = unit;
	}

	/**
	 * Used by measurements.
	 * 
	 * @param id
	 * @param type
	 * @param subtype
	 * @param label
	 * @param unit
	 * @param size
	 */
	public ReportingValuesImpl(Long id, String type, String subtype, String label, String unit, int size) {
		this(id, type, label, unit, size);
		this.subtype = subtype;
		if (subtype.equals(MeasurementValueType.SINGLE_VALUE.name())) {
			this.targets = new Object[size];
		} else {
			this.minTargets = new Object[size];
			this.maxTargets = new Object[size];
		}
	}

	public void addItem(ActivityItemValuesEntity itemValue) {
		if (itemValue instanceof MeasurementEntity) {
			addMeasurementItem((MeasurementEntity) itemValue);
		} else if (itemValue instanceof EstimationEntity) {
			addEstimationItem((EstimationEntity) itemValue);
		} else if (itemValue instanceof TextEntity) {
			addTextItem((TextEntity) itemValue);
		} else if (itemValue instanceof YesNoEntity) {
			addYesNoItem((YesNoEntity) itemValue);
		}
		index++;
	}

	public void addMeasurementItem(MeasurementEntity itemValue) {
		long date = itemValue.getScheduledActivity().getActualTime().getTime();
		Long schedId = itemValue.getScheduledActivity().getId();

		Object[] triple = { date, itemValue.getReportedValue(), schedId };
		this.reportedValues[index] = triple;

		if (subtype.equals(MeasurementValueType.SINGLE_VALUE.name())) {
			Object[] targetPair = { date, itemValue.getTarget(), schedId };
			this.targets[index] = targetPair;
		} else {
			Object[] minTargetPair = { date, itemValue.getMinTarget(), schedId };
			this.minTargets[index] = minTargetPair;
			Object[] maxTargetPair = { date, itemValue.getMaxTarget(), schedId };
			this.maxTargets[index] = maxTargetPair;
		}

	}

	protected void addEstimationItem(EstimationEntity itemValue) {
		long date = itemValue.getScheduledActivity().getActualTime().getTime();
		Long schedId = itemValue.getScheduledActivity().getId();

		Object[] triple = { date, itemValue.getPerceivedSense(), schedId };
		this.reportedValues[index] = triple;
	}

	protected void addTextItem(TextEntity itemValue) {
		long date = itemValue.getScheduledActivity().getActualTime().getTime();
		Long schedId = itemValue.getScheduledActivity().getId();

		Object[] triple = { date, itemValue.getTextComment(), schedId };
		this.reportedValues[index] = triple;
	}

	protected void addYesNoItem(YesNoEntity itemValue) {
		if (itemValue.getAnswer()) {
			this.yes++;
		} else {
			this.no++;
		}

	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getSubtype() {
		return this.subtype;
	}

	@Override
	public Object[] getReportedValues() {
		return reportedValues;
	}

	@Override
	public Object[] getTargets() {
		return targets;
	}

	@Override
	public Object[] getMinTargets() {
		return minTargets;
	}

	@Override
	public Object[] getMaxTargets() {
		return maxTargets;
	}

	@Override
	public int getPercentYes() {
		int total = this.yes + this.no;
		int percent = 0;
		if (total != 0) {
			percent = 100 * this.yes / total;
		}
		return percent;
	}

	@Override
	public int getPercentNo() {
		return 100 - this.getPercentYes();
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public String getUnit() {
		return this.unit;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getQuestion() {
		return this.question;
	}

	private static final long serialVersionUID = 1L;

}
