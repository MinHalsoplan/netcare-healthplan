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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="nc_activity_type")
public class ActivityTypeEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(length=64, nullable=false)
	private String name;
	
	@Column(nullable=false)
	private MeasureUnit unit;
	
	@ManyToOne
	private ActivityCategoryEntity category;
	
	ActivityTypeEntity() {
	}
	
	ActivityTypeEntity(final String name, final ActivityCategoryEntity category, final MeasureUnit unit) {
		this();
		this.setName(name);
		this.setCategory(category);
		this.setUnit(unit);
	}
	
	public static ActivityTypeEntity newEntity(String name, final ActivityCategoryEntity category, MeasureUnit unit) {
		return new ActivityTypeEntity(name, category, unit);
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

	void setUnit(MeasureUnit unit) {
		this.unit = EntityUtil.notNull(unit);
	}

	public MeasureUnit getUnit() {
		return unit;
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
}
