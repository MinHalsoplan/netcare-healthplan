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
package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.YesNo;
import org.callistasoftware.netcare.model.entity.YesNoDefinitionEntity;
import org.callistasoftware.netcare.model.entity.YesNoEntity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YesNoImpl extends ActivityItemValuesImpl implements YesNo {

	private Boolean answer;

	public static YesNo newFromEntity(YesNoEntity entity) {
		YesNoImpl yesno = new YesNoImpl();
		yesno.setId(entity.getId());
		yesno.setDefinition(YesNoDefinitionImpl.newFromEntity((YesNoDefinitionEntity) entity
				.getActivityItemDefinitionEntity()));
		yesno.answer = entity.getAnswer();
		return yesno;
	}

	@Override
	public Boolean getAnswer() {
		return this.answer;
	}

	public void setAnswer(Boolean answer) {
		this.answer = answer;
	}
}
