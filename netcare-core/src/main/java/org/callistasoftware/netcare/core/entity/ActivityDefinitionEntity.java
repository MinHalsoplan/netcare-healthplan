/**
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
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
package org.callistasoftware.netcare.core.entity;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	@ManyToOne
	@JoinColumn(name="ordination_id")
	private OrdinationEntity ordination;
	
	@ManyToOne
	@JoinColumn(name="activity_type_id")
	private ActivityTypeEntity activityType;
	
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name = "nc_scheduled_acitivty", joinColumns = {@JoinColumn(name="activity_def_id")})
	private List<ScheduledActivityEntity> scheduledActivities;
    
    
    ActivityDefinitionEntity() {
    	scheduledActivities = new LinkedList<ScheduledActivityEntity>();
	}
    
    public static ActivityDefinitionEntity newEntity(OrdinationEntity ordinationEntity, ActivityTypeEntity activityType, Frequency frequency) {
    	ActivityDefinitionEntity entity = new ActivityDefinitionEntity();
    	entity.setOrdination(ordinationEntity);
    	entity.setActivityType(activityType);
    	entity.setFrequency(frequency);
    	ordinationEntity.addActivityDefinition(entity);
    	return entity;
    }

	public Long getId() {
		return id;
	}
	
	protected void setOrdination(OrdinationEntity ordination) {
		this.ordination = EntityUtil.notNull(ordination);
	}

	public OrdinationEntity getOrdination() {
		return ordination;
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
	
	protected boolean addScheduledActivityEntity(ScheduledActivityEntity scheduledActivityEntity) {
		if (!scheduledActivities.contains(scheduledActivityEntity)) {
			return scheduledActivities.add(scheduledActivityEntity);
		}
		return false;
	}
	
	protected boolean removeScheduledActivityEntity(ScheduledActivityEntity scheduledActivityEntity) {
		return scheduledActivities.remove(scheduledActivityEntity);
	}
	
	public List<ScheduledActivityEntity> getScheduledActivities() {
		return Collections.unmodifiableList(scheduledActivities);
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
}