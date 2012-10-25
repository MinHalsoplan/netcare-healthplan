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

import org.callistasoftware.netcare.core.api.MeasurementType;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.model.entity.MeasurementTypeEntity;
import org.springframework.context.i18n.LocaleContextHolder;

public class MeasurementTypeImpl extends ActivityItemTypeImpl implements MeasurementType {

	private boolean alarm;
	private Option valueType;
	private Option unit;

	public static MeasurementType newFromEntity(final MeasurementTypeEntity entity) {
		final MeasurementTypeImpl dto = new MeasurementTypeImpl();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setSeqno(entity.getSeqno());

		dto.setAlarm(entity.isAlarmEnabled());
		dto.setUnit(new Option(entity.getUnit().name(), LocaleContextHolder.getLocale()));
		dto.setValueType(new Option(entity.getValueType().name(), LocaleContextHolder.getLocale()));

		return dto;
	}

	@Override
	public Option getValueType() {
		return this.valueType;
	}

	public void setValueType(final Option valueType) {
		this.valueType = valueType;
	}

	@Override
	public Option getUnit() {
		return this.unit;
	}

	public void setUnit(final Option unit) {
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
	public String toString() {
		final StringBuffer buf = new StringBuffer();

		buf.append("==== Measurement type ====\n");
		buf.append("Id: ").append(this.getId()).append("\n");
		buf.append("Name: ").append(this.getName()).append("\n");
		buf.append("Seqno: ").append(this.getSeqno()).append("\n");
		buf.append("Unit: ").append(this.getUnit().getCode()).append("\n");
		buf.append("Type: ").append(this.getValueType().getCode()).append("\n");
		buf.append("==========================\n");

		return buf.toString();
	}
}
