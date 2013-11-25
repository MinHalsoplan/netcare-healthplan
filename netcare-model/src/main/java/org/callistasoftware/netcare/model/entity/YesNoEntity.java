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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("yesno")
public class YesNoEntity extends ActivityItemValuesEntity {

	@Column(name = "question_answer")
	private Boolean answer;

	public static ActivityItemValuesEntity newEntity(ScheduledActivityEntity scheduledActivityEntity,
			ActivityItemDefinitionEntity activityItemDefinitionEntity) {
		YesNoEntity entity = new YesNoEntity();
		entity.setScheduledActivity(scheduledActivityEntity);
		entity.setActivityItemDefinitionEntity(activityItemDefinitionEntity);
		return entity;
	}

	public Boolean getAnswer() {
		return answer;
	}

	public void setAnswer(Boolean answer) {
		this.answer = answer;
	}
}
