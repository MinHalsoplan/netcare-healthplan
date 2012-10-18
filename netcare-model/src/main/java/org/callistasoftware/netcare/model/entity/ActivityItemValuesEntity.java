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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "nc_activity_item_values")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "activity_item_type", discriminatorType = DiscriminatorType.STRING)
public class ActivityItemValuesEntity implements Comparable<ActivityItemValuesEntity> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "scheduled_activity_id")
	private ScheduledActivityEntity scheduledActivity;

	@ManyToOne(optional = false)
	@JoinColumn(name = "activity_item_definition_id")
	private ActivityItemDefinitionEntity activityItemDefinitionEntity;

	public Long getId() {
		return id;
	}

	public ScheduledActivityEntity getScheduledActivity() {
		return scheduledActivity;
	}

	void setScheduledActivity(ScheduledActivityEntity scheduledActivity) {
		this.scheduledActivity = scheduledActivity;
	}

	public ActivityItemDefinitionEntity getActivityItemDefinitionEntity() {
		return activityItemDefinitionEntity;
	}

	public void setActivityItemDefinitionEntity(ActivityItemDefinitionEntity activityItemDefinitionEntity) {
		this.activityItemDefinitionEntity = activityItemDefinitionEntity;
	}

	@Override
	public int compareTo(ActivityItemValuesEntity m) {
		return this.getActivityItemDefinitionEntity().compareTo(m.getActivityItemDefinitionEntity());
	}

}
