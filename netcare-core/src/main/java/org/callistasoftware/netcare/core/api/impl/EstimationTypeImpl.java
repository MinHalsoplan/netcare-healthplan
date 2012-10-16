package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.EstimationType;
import org.callistasoftware.netcare.model.entity.EstimationTypeEntity;

public class EstimationTypeImpl extends ActivityItemTypeImpl implements EstimationType {

	private String minScaleText;
	private String maxScaleText;

	public static EstimationType newFromEntity(EstimationTypeEntity entity) {
		final EstimationTypeImpl dto = new EstimationTypeImpl();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setSeqno(entity.getSeqno());

		dto.setMinScaleText(entity.getSenseLabelLow());
		dto.setMaxScaleText(entity.getSenseLabelHigh());

		return dto;
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
	public String toString() {
		final StringBuffer buf = new StringBuffer();

		buf.append("==== Estimation type ====\n");
		buf.append("Id: ").append(this.getId()).append("\n");
		buf.append("Name: ").append(this.getName()).append("\n");
		buf.append("Seqno: ").append(this.getSeqno()).append("\n");
		buf.append("LabelMin: ").append(this.getMinScaleText()).append("\n");
		buf.append("LabelMax: ").append(this.getMaxScaleText()).append("\n");
		buf.append("==========================\n");

		return buf.toString();
	}

}
