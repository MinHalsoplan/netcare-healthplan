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
package org.callistasoftware.netcare.model.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("estimation")
public class EstimationTypeEntity extends ActivityItemTypeEntity {

	@Column(name = "sense_label_low")
	private String senseLabelLow;

	@Column(name = "sense_label_high")
	private String senseLabelHigh;

	@Column(name = "sense_value_low")
	private Integer senseValueLow;

	@Column(name = "sense_value_high")
	private Integer senseValueHigh;

	public static EstimationTypeEntity newEntity(ActivityTypeEntity activityTypeEntity, String name,
			String minScaleText, String maxScaleText, Integer minScaleValue, Integer maxScaleValue, int seqno) {
		EstimationTypeEntity entity = new EstimationTypeEntity();
		entity.setActivityType(activityTypeEntity);
		entity.setName(name);
		entity.setSenseLabelLow(minScaleText);
		entity.setSenseLabelHigh(maxScaleText);
		entity.setSenseValueLow(minScaleValue);
		entity.setSenseValueHigh(maxScaleValue);
		entity.setSeqno(seqno);
		activityTypeEntity.addActivityItemType(entity);
		return entity;
	}

	public String getSenseLabelLow() {
		return senseLabelLow;
	}

	public void setSenseLabelLow(String senseLabelLow) {
		this.senseLabelLow = senseLabelLow;
	}

	public String getSenseLabelHigh() {
		return senseLabelHigh;
	}

	public void setSenseLabelHigh(String senseLabelHigh) {
		this.senseLabelHigh = senseLabelHigh;
	}

	public Integer getSenseValueLow() {
		return senseValueLow;
	}

	public void setSenseValueLow(Integer senseValueLow) {
		this.senseValueLow = senseValueLow;
	}

	public Integer getSenseValueHigh() {
		return senseValueHigh;
	}

	public void setSenseValueHigh(Integer senseValueHigh) {
		this.senseValueHigh = senseValueHigh;
	}
}
