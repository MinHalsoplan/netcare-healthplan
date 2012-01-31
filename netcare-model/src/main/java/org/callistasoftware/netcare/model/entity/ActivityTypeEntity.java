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
package org.callistasoftware.netcare.model.entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="nc_activity_type")
public class ActivityTypeEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(length=64, nullable=false)
	private String name;
	
	@Column(name="measuring_sense", length=1)
	private String measuringSense;
		
	@Column(name="sense_label_low")
	private String senseLabelLow;
	
	@Column(name="sense_label_high")
	private String senseLabelHigh;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private ActivityCategoryEntity category;
	
	@OneToMany(mappedBy="activityType", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, orphanRemoval=true)
	private List<MeasurementTypeEntity> measurementTypes;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="care_unit_id")
	private CareUnitEntity careUnit;

	
	ActivityTypeEntity() {
		measurementTypes = new LinkedList<MeasurementTypeEntity>();
	}
	
	ActivityTypeEntity(final String name, final ActivityCategoryEntity category) {
		this();
		this.setName(name);
		this.setCategory(category);
	}
	
	public static ActivityTypeEntity newEntity(String name, final ActivityCategoryEntity category) {
		return new ActivityTypeEntity(name, category);
	}

	public Long getId() {
		return id;
	}

	void setName(String name) {
		this.name = EntityUtil.notNull(name);
	}

	public String getName() {
		return name;
	}

	public ActivityCategoryEntity getCategory() {
		return category;
	}

	void setCategory(ActivityCategoryEntity category) {
		this.category = category;
	}

	void setId(Long id) {
		this.id = id;
	}

	public boolean isMeasuringSense() {
		return "Y".equals(measuringSense);
	}

	public void setMeasuringSense(boolean measuringSense) {
		this.measuringSense = (measuringSense) ? "Y" : null;
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

	public boolean addMeasurementType(MeasurementTypeEntity measurementType) {
		if (!measurementTypes.contains(measurementType)) {
			return measurementTypes.add(measurementType);
		}
		return false;
	}
	
	public boolean removeMeasurementType(MeasurementTypeEntity measurementType) {
		return measurementTypes.remove(measurementType);
	}
	
	public List<MeasurementTypeEntity> getMeasurementTypes() {
		return Collections.unmodifiableList(measurementTypes);
	}

	public CareUnitEntity getCareUnit() {
		return careUnit;
	}

	public void setCareUnit(CareUnitEntity careUnit) {
		this.careUnit = careUnit;
	}
}
