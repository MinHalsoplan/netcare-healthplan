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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
@Table(name="nc_activity_definition")
public class ActivityDefinitionEntity implements PermissionRestrictedEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name="uuid", length=40, nullable=false)
	private String uuid;
	
	@Column(length=256, nullable=false)
	private String frequency;
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_time", nullable=false)
	private Date createdTime;

	@Temporal(TemporalType.DATE)
	@Column(name="start_date")
	private Date startDate;

	@Column(name="removed_flag")
	private boolean removedFlag;
	
	@Column(name="is_public_definition", nullable=false)
	private boolean publicDefinition;
	
	@ManyToOne
	@JoinColumn(name="health_plan_id")
	private HealthPlanEntity healthPlan;
	
	@ManyToOne
	@JoinColumn(name="activity_type_id")
	private ActivityTypeEntity activityType;
	
	@ManyToOne
	@JoinColumn(name="created_by_id")
	private UserEntity createdBy;
    
	@OneToMany(mappedBy="activityDefinition", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, orphanRemoval=true)
	private List<ScheduledActivityEntity> scheduledActivities;
	
	@OneToMany(mappedBy="activityDefinition", fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval=true)
	private List<MeasurementDefinitionEntity> measurementDefinitions;
	

    ActivityDefinitionEntity() {
    	scheduledActivities = new LinkedList<ScheduledActivityEntity>();
    	measurementDefinitions = new LinkedList<MeasurementDefinitionEntity>();
    	uuid = UUID.randomUUID().toString();
    	createdTime = new Date();
    	publicDefinition = true;
    	removedFlag = false;
	}
    
    public static ActivityDefinitionEntity newEntity(HealthPlanEntity healthPlanEntity, ActivityTypeEntity activityType, Frequency frequency, UserEntity createdBy) {
    	ActivityDefinitionEntity entity = new ActivityDefinitionEntity();
    	entity.setHealthPlan(healthPlanEntity);
    	entity.setActivityType(activityType);
    	entity.setFrequency(frequency);
    	entity.setStartDate(healthPlanEntity.getStartDate());
    	entity.setCreatedBy(createdBy);
    	for (MeasurementTypeEntity measurementType : activityType.getMeasurementTypes()) {
    		MeasurementDefinitionEntity e = MeasurementDefinitionEntity.newEntity(entity, measurementType);
    		entity.measurementDefinitions.add(e);
    	}
    	healthPlanEntity.addActivityDefinition(entity);
    	
    	return entity;
    }

	public Long getId() {
		return id;
	}
	
	protected void setHealthPlan(HealthPlanEntity healthPlan) {
		this.healthPlan = EntityUtil.notNull(healthPlan);
	}

	public HealthPlanEntity getHealthPlan() {
		return healthPlan;
	}

	public void setActivityType(ActivityTypeEntity activityType) {
		this.activityType = EntityUtil.notNull(activityType);
	}

	public ActivityTypeEntity getActivityType() {
		return activityType;
	}

	public void setFrequency(Frequency frequency) {
		this.frequency = Frequency.marshal(EntityUtil.notNull(frequency));
	}

	public Frequency getFrequency() {
		return Frequency.unmarshal(frequency);
	}
	
	public ScheduledActivityEntity createScheduledActivityEntity(Date scheduledTime) {
		ScheduledActivityEntity entity = ScheduledActivityEntity.newEntity(this, scheduledTime);
		return entity;
	}
	
	
	/**
	 * Sets the start date of this activity.
	 * 
	 * @param startDate the start date, must be in the interval of the health plan.
	 */
	public void setStartDate(Date startDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(EntityUtil.notNull(startDate));
		startDate = EntityUtil.dayBegin(cal).getTime();
		
		Date min = getHealthPlan().getStartDate();
		Date max = getHealthPlan().getEndDate();
		if (startDate.compareTo(min) < 0 || startDate.compareTo(max) > 0) {
			throw new IllegalArgumentException("Invalid start date, out of health plan range: " + startDate);
		}		
		this.startDate = startDate;
	}

	/**
	 * Returns the start date.
	 * 
	 * @return start date.
	 */
	public Date getStartDate() {
		return startDate;
	}


	void setCreatedBy(UserEntity createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Returns the user who created this.
	 * 
	 * @return created by.
	 */
	public UserEntity getCreatedBy() {
		return createdBy;
	}

	/**
	 * Returns the globally unique identifier.
	 * 
	 * @return the globally unique identifier.
	 */
	public String getUUID() {
		return uuid;
	}

	/**
	 * Returns the creation timestamp.
	 * 
	 * @return the timestamp.
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	/**
	 * Returns the list of {@link ScheduledActivityEntity}
	 * 
	 * @return the list (unmodifable).
	 */
	public List<ScheduledActivityEntity> getScheduledActivities() {
		Collections.sort(scheduledActivities);
		return Collections.unmodifiableList(scheduledActivities);
	}
	
	/**
	 * Returns the list of {@link MeasurementDefinitionEntity}
	 * 
	 * @return the list (unmodifable).
	 */
	public List<MeasurementDefinitionEntity> getMeasurementDefinitions() {
		Collections.sort(measurementDefinitions);
		return Collections.unmodifiableList(measurementDefinitions);		
	}
	

	/**
	 * Schedules activities from a specified start date.
	 * 
	 * @param startDate the start date.
	 * @return the list of scheduled activities.
	 */
	protected List<ScheduledActivityEntity> scheduleActivities0(Date startDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		this.scheduledActivities = new LinkedList<ScheduledActivityEntity>();
		Frequency freq = getFrequency();
		while (cal.getTime().compareTo(getHealthPlan().getEndDate()) <= 0) {
			if (freq.isDaySet(cal)) {
				scheduleActivity(this.scheduledActivities, cal, freq.getDay(cal.get(Calendar.DAY_OF_WEEK)));
				// single event.
				if (freq.getWeekFrequency() == 0) {
					break;
				}
			}
			if (freq.getWeekFrequency() > 1 && cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY &&
					cal.getTime().compareTo(getStartDate()) > 0) {
				cal.add(Calendar.DATE, 8*(freq.getWeekFrequency()-1));
			} else {
				cal.add(Calendar.DATE, 1);
			}
		}
		return this.scheduledActivities;		
	}
	
	/**
	 * Returns scheduled activities.
	 */
	public List<ScheduledActivityEntity> scheduleActivities() {	
		return scheduleActivities0(getStartDate());
	}
	
	/**
	 * Appends scheduled activities to a list.
	 * 
	 * @param list the list to append activities to.
	 * @param day the actual day.
	 * @return a list of scheduled activities, empty if none is applicable.
	 */
	private void scheduleActivity(List<ScheduledActivityEntity> list, Calendar day, FrequencyDay fday) {
		for (FrequencyTime t : fday.getTimes()) {
			day.set(Calendar.HOUR_OF_DAY, t.getHour());
			day.set(Calendar.MINUTE, t.getMinute());
			ScheduledActivityEntity scheduledActivity = createScheduledActivityEntity(day.getTime());
			list.add(scheduledActivity);
		}
	}

	@Override
	public boolean isReadAllowed(UserEntity user) {
		return this.isWriteAllowed(user);
	}

	@Override
	public boolean isWriteAllowed(UserEntity user) {
		if (user.isCareGiver()) {
			final CareGiverEntity cg = (CareGiverEntity) user;
			return cg.getCareUnit().getId().equals(this.getHealthPlan().getIssuedBy().getCareUnit().getId());
		}
		
		return this.getHealthPlan().getForPatient().getId().equals(user.getId());
	}

	public boolean isRemovedFlag() {
		return removedFlag;
	}

	public void setRemovedFlag(boolean removedFlag) {
		this.removedFlag = removedFlag;
	}

	public boolean isPublicDefinition() {
		return publicDefinition;
	}

	public void setPublicDefinition(boolean publicDefinition) {
		this.publicDefinition = publicDefinition;
	}

}
