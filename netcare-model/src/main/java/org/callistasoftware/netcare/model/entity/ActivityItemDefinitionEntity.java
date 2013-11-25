/**
 * Copyright (C) 2011,2012 Landstingen Jonkoping <landstinget@lj.se>
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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "nc_activity_item_definition")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "activity_item_type", discriminatorType = DiscriminatorType.STRING)
public class ActivityItemDefinitionEntity implements Comparable<ActivityItemDefinitionEntity> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="active", nullable=false)
	private boolean active = true;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_def_id")
	private ActivityDefinitionEntity activityDefinition;

	@ManyToOne(optional = false)
	@JoinColumn(name = "activity_item_type_id")
	private ActivityItemTypeEntity activityItemType;

	ActivityItemDefinitionEntity() {
		setActive(true);
	}
	
	public Long getId() {
		return id;
	}

	public ActivityDefinitionEntity getActivityDefinition() {
		return activityDefinition;
	}

	void setActivityDefinition(ActivityDefinitionEntity activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	public ActivityItemTypeEntity getActivityItemType() {
		return activityItemType;
	}

	public void setActivityItemType(ActivityItemTypeEntity activityItemType) {
		this.activityItemType = activityItemType;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public int compareTo(ActivityItemDefinitionEntity m) {
		return this.getActivityItemType().compareTo(m.getActivityItemType());
	}
}
