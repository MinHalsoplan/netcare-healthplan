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
package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.Text;
import org.callistasoftware.netcare.model.entity.TextDefinitionEntity;
import org.callistasoftware.netcare.model.entity.TextEntity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextImpl extends ActivityItemValuesImpl implements Text {

	private String textComment;

	public static Text newFromEntity(TextEntity entity) {
		TextImpl yesno = new TextImpl();
		yesno.setId(entity.getId());
		yesno.setDefinition(TextDefinitionImpl.newFromEntity((TextDefinitionEntity) entity
				.getActivityItemDefinitionEntity()));
		yesno.textComment = entity.getTextComment();
		return yesno;
	}

	@Override
	public String getTextComment() {
		return this.textComment;
	}

	public void setTextComment(String textComment) {
		this.textComment = textComment;
	}
}
