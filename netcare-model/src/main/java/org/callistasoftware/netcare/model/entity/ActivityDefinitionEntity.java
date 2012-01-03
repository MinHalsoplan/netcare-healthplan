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
public class ActivityDefinitionEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(length=256, nullable=false)
	private String frequency;
	
	@Column(name="target")
	private int activityTarget;
	
	@Temporal(TemporalType.DATE)
	@Column(name="start_date")
	private Date startDate;

	@ManyToOne
	@JoinColumn(name="health_plan_id")
	private HealthPlanEntity healthPlan;
	
	@ManyToOne
	@JoinColumn(name="activity_type_id")
	private ActivityTypeEntity activityType;
	
	@ManyToOne
	@JoinColumn(name="created_by_id")
	private UserEntity createdBy;
    
	@OneToMany(mappedBy="activityDefinition", fetch=FetchType.LAZY)
	private List<ScheduledActivityEntity> scheduledActivities;

    ActivityDefinitionEntity() {
    	scheduledActivities = new LinkedList<ScheduledActivityEntity>();
	}
    
    public static ActivityDefinitionEntity newEntity(HealthPlanEntity healthPlanEntity, ActivityTypeEntity activityType, Frequency frequency, UserEntity createdBy) {
    	ActivityDefinitionEntity entity = new ActivityDefinitionEntity();
    	entity.setHealthPlan(healthPlanEntity);
    	entity.setActivityType(activityType);
    	entity.setFrequency(frequency);
    	entity.setStartDate(healthPlanEntity.getStartDate());
    	entity.setCreatedBy(createdBy);
    	
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
	 * Sets the target for this activity (for each execution), which will be used during follow up.
	 * 
	 * @param activityTarget the target in the same unit as specified by {@link ActivityTypeEntity}
	 * and the actual unit {@link MeasureUnit}
	 */
	public void setActivityTarget(int activityTarget) {
		this.activityTarget = activityTarget;
	}

	/**
	 * Returns the weekly target.
	 * 
	 * @return the weekly target in the same unit as specified by {@link ActivityTypeEntity}
	 */
	public int getActivityTarget() {
		return activityTarget;
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
	 * Returns the list of {@link ScheduledActivityEntity}
	 * 
	 * @return the list.
	 */
	public List<ScheduledActivityEntity> getScheduledActivities() {
		Collections.sort(scheduledActivities);
		return Collections.unmodifiableList(scheduledActivities);
	}
}
