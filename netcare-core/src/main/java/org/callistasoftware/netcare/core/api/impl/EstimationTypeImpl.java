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

import org.callistasoftware.netcare.core.api.EstimationType;
import org.callistasoftware.netcare.model.entity.EstimationTypeEntity;

public class EstimationTypeImpl extends ActivityItemTypeImpl implements EstimationType {

	private String minScaleText;
	private String maxScaleText;
	private Integer minScaleValue;
	private Integer maxScaleValue;

	public static EstimationType newFromEntity(EstimationTypeEntity entity) {
		final EstimationTypeImpl dto = new EstimationTypeImpl();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setSeqno(entity.getSeqno());

		dto.setMinScaleText(entity.getSenseLabelLow());
		dto.setMaxScaleText(entity.getSenseLabelHigh());
		dto.setMinScaleValue(entity.getSenseValueLow());
		dto.setMaxScaleValue(entity.getSenseValueHigh());

		return dto;
	}

	@Override
	public String getActivityItemTypeName() {
		return ESTIMATION_ITEM_TYPE;
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
	public String toString() {
		final StringBuffer buf = new StringBuffer();

		buf.append("==== Estimation type ====\n");
		buf.append("Id: ").append(this.getId()).append("\n");
		buf.append("Name: ").append(this.getName()).append("\n");
		buf.append("Seqno: ").append(this.getSeqno()).append("\n");
		buf.append("LabelMin: ").append(this.getMinScaleText()).append("\n");
		buf.append("LabelMax: ").append(this.getMaxScaleText()).append("\n");
		buf.append("ValueMin: ").append(this.getMinScaleValue()).append("\n");
		buf.append("ValueMax: ").append(this.getMaxScaleValue()).append("\n");
		buf.append("==========================\n");

		return buf.toString();
	}

}
