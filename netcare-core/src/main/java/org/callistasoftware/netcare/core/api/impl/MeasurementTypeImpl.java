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

public class MeasurementTypeImpl implements MeasurementType {

	private Long id;
	private String name;
	private boolean alarm;
	private Option valueType;
	private Option unit;
	private int seqno;
	
	public static MeasurementType newFromEntity(final MeasurementTypeEntity entity) {
		final MeasurementTypeImpl dto = new MeasurementTypeImpl();
		
		dto.id = entity.getId();
		dto.setName(entity.getName());
		dto.setAlarm(entity.isAlarmEnabled());
		dto.setUnit(new Option(entity.getUnit().name(), LocaleContextHolder.getLocale()));
		dto.setValueType(new Option(entity.getValueType().name(), LocaleContextHolder.getLocale()));
		dto.setSeqno(entity.getSeqno());
		
		return dto;
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

	public int getSeqno() {
		return seqno;
	}

	void setSeqno(int seqno) {
		this.seqno = seqno;
	}
}
