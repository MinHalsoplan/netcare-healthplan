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

import java.util.Set;
import java.util.TreeSet;

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
@Table(name="activity_definition")
public class ActivityDefinitionEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column
	private String frequency;
	
	@Column
	private int activityTarget;

	@ManyToOne
	private OrdinationEntity ordination;
	
	@ManyToOne
	private ActivityTypeEntity activityType;

    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name = "scheduled_acitivty", joinColumns = {@JoinColumn(name="activity_def_id")})
	Set<ScheduledActivityEntity> scheduledActivities;
    
    
    public ActivityDefinitionEntity() {
    	scheduledActivities = new TreeSet<ScheduledActivityEntity>();
	}
    
	public Long getId() {
		return id;
	}
	
	public void setOrdination(OrdinationEntity ordination) {
		this.ordination = ordination;
	}

	public OrdinationEntity getOrdination() {
		return ordination;
	}

	public void setActivityType(ActivityTypeEntity activityType) {
		this.activityType = activityType;
	}

	public ActivityTypeEntity getActivityType() {
		return activityType;
	}

	public void setFrequency(Frequency frequency) {
		this.frequency = Frequency.marshal(frequency);
	}

	public Frequency getFrequency() {
		return Frequency.unmarshal(frequency);
	}
	
	public Set<ScheduledActivityEntity> getScheduledActivities() {
		return scheduledActivities;
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
