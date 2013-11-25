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

import org.callistasoftware.netcare.core.api.ActivityItemType;
import org.callistasoftware.netcare.core.api.MeasureUnit;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.model.entity.ActivityItemTypeEntity;
import org.callistasoftware.netcare.model.entity.EstimationTypeEntity;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.callistasoftware.netcare.model.entity.TextTypeEntity;
import org.callistasoftware.netcare.model.entity.YesNoTypeEntity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.i18n.LocaleContextHolder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityItemTypeImpl implements ActivityItemType {

	private Long id;
	private String name;
	private int seqno;
	private String activityItemTypeName;

	// Estimation
	private String minScaleText;
	private String maxScaleText;
	private Integer minScaleValue;
	private Integer maxScaleValue;

	// Measurement
	private boolean alarm;
	private Option valueType;
	private MeasureUnit unit;

	// YesNo
	private String question;

	// Text
	private String label;

	public static ActivityItemType newFromEntity(ActivityItemTypeEntity entity) {
		final ActivityItemTypeImpl dto = new ActivityItemTypeImpl();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setSeqno(entity.getSeqno());

		if (entity instanceof EstimationTypeEntity) {
			EstimationTypeEntity e = (EstimationTypeEntity) entity;
			dto.setActivityItemTypeName(ESTIMATION_ITEM_TYPE);
			dto.setMinScaleText(e.getSenseLabelLow());
			dto.setMaxScaleText(e.getSenseLabelHigh());
			dto.setMinScaleValue(e.getSenseValueLow());
			dto.setMaxScaleValue(e.getSenseValueHigh());
		} else if (entity instanceof MeasurementTypeEntity) {
			MeasurementTypeEntity e = (MeasurementTypeEntity) entity;
			dto.setActivityItemTypeName(MEASUREMENT_ITEM_TYPE);
			dto.setAlarm(e.isAlarmEnabled());
			dto.setUnit(MeasureUnitImpl.newFromEntity(((MeasurementTypeEntity) entity).getUnit()));
			dto.setValueType(new Option(e.getValueType().name(), LocaleContextHolder.getLocale()));
		} else if (entity instanceof YesNoTypeEntity) {
			YesNoTypeEntity e = (YesNoTypeEntity) entity;
			dto.setActivityItemTypeName(YESNO_ITEM_TYPE);
			dto.setQuestion(e.getQuestion());
		} else if (entity instanceof TextTypeEntity) {
			TextTypeEntity e = (TextTypeEntity) entity;
			dto.setActivityItemTypeName(TEXT_ITEM_TYPE);
			dto.setLabel(e.getLabel());
		}
		return dto;
	}

	@Override
	public String getActivityItemTypeName() {
		return this.activityItemTypeName;
	}

	public void setActivityItemTypeName(String activityItemTypeName) {
		this.activityItemTypeName = activityItemTypeName;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int getSeqno() {
		return seqno;
	}

	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}

	@Override
	public String getMinScaleText() {
		return this.minScaleText;
	}

	public void setMinScaleText(final String minScaleText) {
		this.minScaleText = minScaleText;
	}

	@Override
	public String getMaxScaleText() {
		return this.maxScaleText;
	}

	public void setMaxScaleText(final String maxScaleText) {
		this.maxScaleText = maxScaleText;
	}

	@Override
	public Integer getMinScaleValue() {
		return this.minScaleValue;
	}

	public void setMinScaleValue(Integer minScaleValue) {
		this.minScaleValue = minScaleValue;
	}

	@Override
	public Integer getMaxScaleValue() {
		return maxScaleValue;
	}

	public void setMaxScaleValue(Integer maxScaleValue) {
		this.maxScaleValue = maxScaleValue;
	}

	@Override
	public Option getValueType() {
		return this.valueType;
	}

	public void setValueType(final Option valueType) {
		this.valueType = valueType;
	}

	@Override
	public MeasureUnit getUnit() {
		return this.unit;
	}

	public void setUnit(final MeasureUnit unit) {
		this.unit = unit;
	}

	@Override
	public boolean isAlarm() {
		return this.alarm;
	}

	public void setAlarm(final boolean alarm) {
		this.alarm = alarm;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();

		buf.append("==== Item type ====\n");
		buf.append("Id: ").append(this.getId()).append("\n");
		buf.append("Name: ").append(this.getName()).append("\n");
		buf.append("Seqno: ").append(this.getSeqno()).append("\n");
		buf.append("LabelMin: ").append(this.getMinScaleText()).append("\n");
		buf.append("LabelMax: ").append(this.getMaxScaleText()).append("\n");
		buf.append("ValueMin: ").append(this.getMinScaleValue()).append("\n");
		buf.append("ValueMax: ").append(this.getMaxScaleValue()).append("\n");
		buf.append("Unit: ").append(this.getUnit() != null ? this.getUnit().getName() : "").append("\n");
		buf.append("Type: ").append(this.getValueType() != null ? this.getValueType().getCode() : "").append("\n");
		buf.append("Question: ").append(this.getQuestion()).append("\n");
		buf.append("Label: ").append(this.getLabel()).append("\n");
		buf.append("==========================\n");

		return buf.toString();
	}
}
