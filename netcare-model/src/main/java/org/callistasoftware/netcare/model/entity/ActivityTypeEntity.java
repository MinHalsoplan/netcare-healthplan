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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * An ActivityType corresponds to a "template". An ActivityType can be assigned
 * to a specific CareUnit, available to all care units within a CountyCouncil or
 * to all county councils, ie national.
 * 
 * Each ActivityType holds information on the care unit who created it and what
 * region that care unit belongs to.
 * 
 * Every ActivityType also has an AccessLevel set (NATIONAL, COUNTY_COUNCIL,
 * CAREUNIT) which decides who has the posiibility to see and use the template.
 * 
 * If an ActivityType is copied a new care unit is set together with that care
 * units regional affilitation.
 */
@Entity
@Table(name = "nc_activity_type")
public class ActivityTypeEntity implements PermissionRestrictedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 64, nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private ActivityCategoryEntity category;

	@OneToMany(mappedBy = "activityType", fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<ActivityItemTypeEntity> activityItemTypes;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "care_unit_id")
	private CareUnitEntity careUnit;

	@Column(name = "accessLevel", nullable = false)
	@Enumerated(EnumType.STRING)
	private AccessLevel accessLevel;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "county_council_id")
	private CountyCouncilEntity countyCouncil;
	
	@Transient
	private Boolean inUse;

	ActivityTypeEntity() {
		activityItemTypes = new LinkedList<ActivityItemTypeEntity>();
	}

	ActivityTypeEntity(final String name, final ActivityCategoryEntity category, final CareUnitEntity careUnit,
			CountyCouncilEntity countyCouncil, AccessLevel accessLevel) {
		this();
		this.setName(name);
		this.setCategory(category);
		this.setCareUnit(careUnit);
		this.setCountyCouncil(careUnit.getCountyCouncil());
		this.setAccessLevel(accessLevel);
	}

	public static ActivityTypeEntity newEntity(String name, final ActivityCategoryEntity category,
			final CareUnitEntity careUnit, AccessLevel accessLevel) {
		return new ActivityTypeEntity(name, category, careUnit, careUnit.getCountyCouncil(), accessLevel);
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = EntityUtil.notNull(name);
	}

	public String getName() {
		return name;
	}

	public ActivityCategoryEntity getCategory() {
		return category;
	}

	public void setCategory(ActivityCategoryEntity category) {
		this.category = category;
	}

	void setId(Long id) {
		this.id = id;
	}

	public boolean addActivityItemType(ActivityItemTypeEntity activityItemType) {
		if (!activityItemTypes.contains(activityItemType)) {
			return activityItemTypes.add(activityItemType);
		}
		return false;
	}

	public boolean removeActivityItemType(ActivityItemTypeEntity activityItemType) {
		return activityItemTypes.remove(activityItemType);
	}

	public List<ActivityItemTypeEntity> getActivityItemTypes() {
		return this.activityItemTypes;
	}

	public CareUnitEntity getCareUnit() {
		return careUnit;
	}

	public void setCareUnit(CareUnitEntity careUnit) {
		this.careUnit = careUnit;
	}

	public AccessLevel getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

	public CountyCouncilEntity getCountyCouncil() {
		return countyCouncil;
	}

	public void setCountyCouncil(CountyCouncilEntity countyCouncil) {
		this.countyCouncil = countyCouncil;
	}
	
	public void setInUse(Boolean inUse) {
		this.inUse = inUse;
	}
	
	public Boolean getInUse() {
		return inUse;
	}

	@Override
	public boolean isReadAllowed(UserEntity user) {
		if (!user.isCareActor()) {
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
		if (!user.isCareActor()) {
			return false;
		}

		final CareActorEntity ca = (CareActorEntity) user;
		
		if (getAccessLevel().equals(AccessLevel.CAREUNIT)) {
		
			if (ca.getCareUnit().getHsaId().equals(this.getCareUnit().getHsaId())) {
				return true;
			}
		}
		
		if (getAccessLevel().equals(AccessLevel.COUNTY_COUNCIL)) {
			
			if (ca.hasRole(RoleEntity.COUNTY_COUNCIL_ADMINISTRATOR) 
					&& getCountyCouncil().getId().equals(ca.getCareUnit().getCountyCouncil().getId())) {
				return true;
			}
			
		}
		
		if (getAccessLevel().equals(AccessLevel.NATIONAL)) {
			
			if (ca.hasRole(RoleEntity.NATION_ADMINISTRATOR)) {
				return true;
			}
		}

		return false;
	}
}
