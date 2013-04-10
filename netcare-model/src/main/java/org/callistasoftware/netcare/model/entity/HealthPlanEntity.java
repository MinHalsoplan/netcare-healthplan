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

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="nc_health_plan")
public class HealthPlanEntity implements PermissionRestrictedEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false, length=64)
	private String name;
	
	@Column(name="start_date", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name="end_date", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	@Column(nullable=false)
	private int duration;
	
	@Column(name="duration_unit", nullable=false)
	private DurationUnit durationUnit;
	
	@Column(name="auto_renewal")
	private boolean autoRenewal;
	
	@Column(name="reminder_done")
	private boolean reminderDone;

    @Column(name="active")
    private boolean active = true;

	@ManyToOne
	@JoinColumn(name="issued_by_care_giver_id")
	private CareActorEntity issuedBy;
	
	@ManyToOne
	@JoinColumn(name="owned_by_care_unit_id")
	private CareUnitEntity careUnit;
	
	@ManyToOne
	@JoinColumn(name="for_patient_id")
	private PatientEntity forPatient;
	
	@OneToMany(mappedBy="healthPlan", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, orphanRemoval=true)
	private List<ActivityDefinitionEntity> activityDefinitions;

	HealthPlanEntity() {
		activityDefinitions = new LinkedList<ActivityDefinitionEntity>();
		reminderDone = false;
	}

	public static HealthPlanEntity newEntity(CareActorEntity issuedBy, PatientEntity forPatient, String name, Date startDate, int duration, DurationUnit unit) {
		HealthPlanEntity entity = new HealthPlanEntity();
		entity.setIssuedBy(issuedBy);
		entity.setForPatient(forPatient);
		entity.setName(name);
		entity.setStartDate(startDate);
		entity.setDurationUnit(unit);
		entity.setDuration(duration);
		entity.setCareUnit(issuedBy.getCareUnit());
		return entity;
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

	public void setStartDate(Date startDate) {
		this.startDate = new DateTime(startDate).withTimeAtStartOfDay().toDate();
		calculateEnd();
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	protected void setIssuedBy(CareActorEntity issuedBy) {
		this.issuedBy = EntityUtil.notNull(issuedBy);
	}

	public CareActorEntity getIssuedBy() {
		return issuedBy;
	}
	
	public CareUnitEntity getCareUnit() {
		return this.careUnit;
	}
	
	void setCareUnit(final CareUnitEntity careUnit) {
		this.careUnit = careUnit;
	}

	void setActivityDefinitions(List<ActivityDefinitionEntity> activityDefinitions) {
		this.activityDefinitions = activityDefinitions;
	}
	
	public boolean addActivityDefinition(ActivityDefinitionEntity activityDefinitionEntity) {
		if (!getActivityDefinitions().contains(activityDefinitionEntity)) {
			return activityDefinitions.add(activityDefinitionEntity);
		}
		return false;
	}
	
	public boolean removeActivityDefinition(ActivityDefinitionEntity activityDefinitionEntity) {
		return activityDefinitions.remove(activityDefinitionEntity);
	}

	public List<ActivityDefinitionEntity> getActivityDefinitions() {
		return Collections.unmodifiableList(activityDefinitions);
	}

	public void setDuration(int duration) {
		if (duration <= 0 || duration > 48) {
			throw new IllegalArgumentException("Invalid duration: " + duration);
		}
		this.duration = duration;
		calculateEnd();
	}

	public int getDuration() {
		return duration;
	}

	public void setDurationUnit(DurationUnit durationUnit) {
		this.durationUnit = EntityUtil.notNull(durationUnit);
		calculateEnd();
	}

	public boolean isActive() {
        return active;
	}

    public void setActive(boolean active) {
        this.active = active;
    }


	public DurationUnit getDurationUnit() {
		return durationUnit;
	}

	protected void calculateEnd() {
		if (durationUnit != null && startDate != null) {

            final DateTime dt = new DateTime(getStartDate());
            if (getDurationUnit().equals(DurationUnit.MONTH)) {
                setEndDate(dt.plusMonths(getDuration()).withTime(23, 59, 59, 999).toDate());
            } else {
                setEndDate(dt.plusWeeks(getDuration()).withTime(23, 59, 59, 999).toDate());
            }
		} else {
			endDate = null;
		}
	}

	protected void setForPatient(PatientEntity forPatient) {
		this.forPatient = EntityUtil.notNull(forPatient);
	}

	public PatientEntity getForPatient() {
		return forPatient;
	}

	/**
	 * Returns if this health plan shall renew itself automatically. <p>
	 * 
	 * @return true if automatically renewal is enabled, otherwise false.
	 */
	public boolean isAutoRenewal() {
		return autoRenewal;
	}

	/**
	 * Sets the auto renewal property.
	 * 
	 * @param autoRenewal true if automatically renewal is enabled, otherwise false.
	 */
	public void setAutoRenewal(boolean autoRenewal) {
        if (!isActive()) {
            throw new IllegalStateException("Cannot enable auto renewal since the health plan " + getId() + " is not active");
        }

		this.autoRenewal = autoRenewal;
	}

	@Override
	public boolean isReadAllowed(UserEntity userId) {
		return this.isWriteAllowed(userId);
	}

	@Override
	public boolean isWriteAllowed(UserEntity userId) {
		final boolean careActor = userId.isCareActor();
		if (careActor) {
			return ((CareActorEntity) userId).getCareUnit().getHsaId().equals(this.getCareUnit().getHsaId());
		}
		
		return this.getForPatient().getId().equals(userId.getId());
	}

	public boolean isReminderDone() {
		return reminderDone;
	}

	public void setReminderDone(boolean reminderDone) {
		this.reminderDone = reminderDone;
	}

    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
