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
@Table(name = "nc_activity_type")
public class ActivityTypeEntity implements PermissionRestrictedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 64, nullable = false)
	private String name;

	@Column(name = "measuring_sense")
	private boolean measuringSense;

	@Column(name = "sense_label_low")
	private String senseLabelLow;

	@Column(name = "sense_label_high")
	private String senseLabelHigh;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private ActivityCategoryEntity category;

	@OneToMany(mappedBy = "activityType", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
	private List<ActivityItemTypeEntity> activityItemTypes;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "care_unit_id")
	private CareUnitEntity careUnit;

	ActivityTypeEntity() {
		activityItemTypes = new LinkedList<ActivityItemTypeEntity>();
	}

	ActivityTypeEntity(final String name, final ActivityCategoryEntity category, final CareUnitEntity careUnit) {
		this();
		this.setName(name);
		this.setCategory(category);
		this.setCareUnit(careUnit);
	}

	public static ActivityTypeEntity newEntity(String name, final ActivityCategoryEntity category,
			final CareUnitEntity careUnit) {
		return new ActivityTypeEntity(name, category, careUnit);
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
		return measuringSense;
	}

	public void setMeasuringSense(boolean measuringSense) {
		this.measuringSense = measuringSense;
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

	public boolean addActivityItemType(ActivityItemTypeEntity activityItemType) {
		if (!activityItemTypes.contains(activityItemType)) {
			int seqno = activityItemTypes.size() + 1;
			activityItemType.setSeqno(seqno);
			return activityItemTypes.add(activityItemType);
		}
		return false;
	}

	public boolean removeActivityItemType(ActivityItemTypeEntity activityItemType) {
		return activityItemTypes.remove(activityItemType);
	}

	public List<ActivityItemTypeEntity> getActivityItemTypes() {
		Collections.sort(activityItemTypes);
		return Collections.unmodifiableList(activityItemTypes);
	}

	public CareUnitEntity getCareUnit() {
		return careUnit;
	}

	void setCareUnit(CareUnitEntity careUnit) {
		this.careUnit = careUnit;
	}

	@Override
	public boolean isReadAllowed(UserEntity user) {
		if (!user.isCareGiver()) {
			final PatientEntity p = (PatientEntity) user;
			for (final HealthPlanEntity hp : p.getHealthPlans()) {
				if (hp.getCareUnit().getHsaId().equals(this.getCareUnit().getHsaId())) {
					return true;
				}
			}

			return false;
		} else {
			return this.isWriteAllowed(user);
		}
	}

	@Override
	public boolean isWriteAllowed(UserEntity user) {
		if (!user.isCareGiver()) {
			return false;
		}

		final CareGiverEntity cg = (CareGiverEntity) user;
		if (cg.getCareUnit().getHsaId().equals(this.getCareUnit().getHsaId())) {
			return true;
		}

		return false;
	}
}
