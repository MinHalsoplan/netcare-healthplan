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

import org.callistasoftware.netcare.core.api.ActivityComment;
import org.callistasoftware.netcare.core.api.util.DateUtil;
import org.callistasoftware.netcare.model.entity.ActivityCommentEntity;

public class ActivityCommentImpl implements ActivityComment {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String comment;
	private String reply;
	private String commentedBy;
	private String commentedAt;
	private String repliedAt;
	private String activityName;
	private String activityReportedAt;
	
	ActivityCommentImpl(final ActivityCommentEntity entity) {
		this.id = entity.getId();
		this.comment = entity.getComment();
		this.reply = entity.getReply();
		this.activityName = entity.getActivity().getActivityDefinitionEntity().getActivityType().getName();
		this.activityReportedAt = DateUtil.toDateTime(entity.getActivity().getReportedTime());
		this.commentedAt = DateUtil.toDateTime(entity.getCommentedAt());
		this.repliedAt = DateUtil.toDateTime(entity.getRepliedAt());
		this.commentedBy = entity.getCommentedBy().getName();
	}
	
	public static ActivityComment newFromEntity(final ActivityCommentEntity entity) {
		return new ActivityCommentImpl(entity);
	}
	
	public static ActivityComment[] newFromEntities(final List<ActivityCommentEntity> entities) {
		final ActivityComment[] dtos = new ActivityComment[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			dtos[i] = ActivityCommentImpl.newFromEntity(entities.get(i));
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
	public String getComment() {
		return this.comment;
	}

	@Override
	public String getReply() {
		return this.reply;
	}

	@Override
	public String getCommentedBy() {
		return this.commentedBy;
	}

	@Override
	public String getCommentedAt() {
		return this.commentedAt;
	}

	@Override
	public String getRepliedAt() {
		return this.repliedAt;
	}

	@Override
	public String getActivityName() {
		return this.activityName;
	}

	@Override
	public String getActivityReportedAt() {
		return this.activityReportedAt;
	}

}
