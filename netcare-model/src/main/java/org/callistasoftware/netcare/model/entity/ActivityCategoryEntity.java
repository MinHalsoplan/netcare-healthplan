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
package org.callistasoftware.netcare.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="activity_category")
public class ActivityCategoryEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false, unique=true)
	private String name;
	
	@OneToMany(fetch=FetchType.LAZY)
	private List<ActivityTypeEntity> activityTypes;
	
	ActivityCategoryEntity() {
		this.setActivityTypes(new ArrayList<ActivityTypeEntity>());
	}
	
	ActivityCategoryEntity(final String name) {
		this();
		this.setName(name);
	}
	
	public static ActivityCategoryEntity newEntity(final String name) {
		return new ActivityCategoryEntity(name);
	}
	
	public Long getId() {
		return id;
	}
	
	void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	void setName(String name) {
		this.name = name;
	}

	public List<ActivityTypeEntity> getActivityTypes() {
		return activityTypes;
	}

	void setActivityTypes(List<ActivityTypeEntity> activityTypes) {
		this.activityTypes = activityTypes;
	}
}
