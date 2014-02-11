/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("text")
public class TextTypeEntity extends ActivityItemTypeEntity {

	@Column(name = "text_label")
	private String label;

	public static TextTypeEntity newEntity(ActivityTypeEntity activityTypeEntity, String name, String label, int seqno) {
		TextTypeEntity entity = new TextTypeEntity();
		entity.setActivityType(activityTypeEntity);
		entity.setName(name);
		entity.setLabel(label);
		entity.setSeqno(seqno);
		activityTypeEntity.addActivityItemType(entity);
		return entity;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
