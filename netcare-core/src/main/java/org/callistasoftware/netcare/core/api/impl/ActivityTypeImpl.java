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
package org.callistasoftware.netcare.core.api.impl;

import java.util.List;
import java.util.Locale;

import org.callistasoftware.netcare.core.api.ActivityCategory;
import org.callistasoftware.netcare.core.api.ActivityItemType;
import org.callistasoftware.netcare.core.api.ActivityType;
import org.callistasoftware.netcare.core.api.Option;
import org.callistasoftware.netcare.model.entity.AccessLevel;
import org.callistasoftware.netcare.model.entity.ActivityItemTypeEntity;
import org.callistasoftware.netcare.model.entity.ActivityTypeEntity;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Implementation of an activity type
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 * 
 */
public class ActivityTypeImpl implements ActivityType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private Option accessLevel;
	private ActivityCategoryImpl category;

	private ActivityItemType[] activityItems;

	//
	public ActivityTypeImpl() {
		this.accessLevel = new Option(AccessLevel.CAREUNIT.name(), LocaleContextHolder.getLocale());
		this.activityItems = new ActivityItemTypeImpl[0];
	}

	public static ActivityType newFromEntity(final ActivityTypeEntity entity, final Locale l) {
		final ActivityTypeImpl dto = new ActivityTypeImpl();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setAccessLevel(new Option(entity.getAccessLevel().name(), l));
		dto.setCategory((ActivityCategoryImpl) ActivityCategoryImpl.newFromEntity(entity.getCategory()));
		final ActivityItemType[] values = new ActivityItemTypeImpl[entity.getActivityItemTypes().size()];
		for (int i = 0; i < values.length; i++) {
			ActivityItemTypeEntity activityItemTypeEntity = entity.getActivityItemTypes().get(i);
			values[i] = ActivityItemTypeImpl.newFromEntity(activityItemTypeEntity);
		}

		dto.activityItems = values;

		return dto;
	}

	public static ActivityType[] newFromEntities(final List<ActivityTypeEntity> entities, final Locale l) {
		final ActivityType[] dtos = new ActivityTypeImpl[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = ActivityTypeImpl.newFromEntity(entities.get(i), l);
		}

		return dtos;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public ActivityCategory getCategory() {
		return this.category;
	}

	public void setCategory(final ActivityCategoryImpl category) {
		this.category = category;
	}

	@Override
	public ActivityItemType[] getActivityItems() {
		return this.activityItems;
	}

	public void setActivityItems(final ActivityItemTypeImpl[] activityItems) {
		this.activityItems = activityItems;
	}

	public void setActivityItems(ActivityItemType[] activityItems) {
		this.activityItems = activityItems;
	}

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append("==== Activity Type ====\n");
		buf.append("Id: ").append(this.getId()).append("\n");
		buf.append("Name: ").append(this.getName()).append("\n");
		buf.append(this.getCategory());

		for (final ActivityItemType t : this.getActivityItems()) {
			buf.append(t);
		}

		return buf.toString();
	}

	@Override
	public Option getAccessLevel() {
		return this.accessLevel;
	}

	public void setAccessLevel(final Option accessLevel) {
		this.accessLevel = accessLevel;
	}
}
