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

import java.util.List;

import org.callistasoftware.netcare.core.api.ActivityComment;
import org.callistasoftware.netcare.core.api.PatientBaseView;
import org.callistasoftware.netcare.core.api.util.DateUtil;
import org.callistasoftware.netcare.model.entity.ActivityCommentEntity;
import org.callistasoftware.netcare.model.entity.PatientEntity;

public class ActivityCommentImpl implements ActivityComment {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String comment;
	private String reply;
	private String commentedBy;
	private String commentedByCareUnit;
	private String commentedAt;
	private String repliedAt;
	private String repliedBy;
	private String activityName;
	private String activityReportedAt;
	
	private boolean markedAsRead;
	private boolean like;
	
	private boolean hiddenByAdmin;
	private boolean hiddenByPatient;
	
	private String healtPlanName;
	private PatientBaseView patient;
	
	ActivityCommentImpl() {
	
	}
	
	ActivityCommentImpl(final ActivityCommentEntity entity) {
		this.id = entity.getId();
		this.comment = entity.getComment();
		this.activityName = entity.getActivity().getActivityDefinitionEntity().getActivityType().getName();
		this.activityReportedAt = DateUtil.toDateTime(entity.getActivity().getReportedTime());
		this.commentedAt = DateUtil.toDate(entity.getCommentedAt());
		this.commentedBy = entity.getCommentedBy().getFirstName() + " " + entity.getCommentedBy().getSurName();
		this.commentedByCareUnit = entity.getCommentedBy().getCareUnit().getName();
		
		this.like = entity.isLike();
		this.markedAsRead = entity.isMarkedAsRead();
		
		this.hiddenByAdmin = entity.isHiddenByAdmin();
		this.hiddenByPatient = entity.isHiddenByPatient();
		
		if (entity.getRepliedAt() != null) {
			this.reply = entity.getReply();
			this.repliedAt = DateUtil.toDateTime(entity.getRepliedAt());
			
			final PatientEntity p = entity.getActivity().getActivityDefinitionEntity().getHealthPlan().getForPatient();
			this.repliedBy = p.getFirstName() + " (" + p.getCivicRegistrationNumber() + ")";
		}
		this.healtPlanName = entity.getActivity().getActivityDefinitionEntity().getHealthPlan().getName();
		PatientEntity p = entity.getActivity().getActivityDefinitionEntity().getHealthPlan().getForPatient();
		this.patient = PatientBaseViewImpl.newFromEntity(p);
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

	@Override
	public String getRepliedBy() {
		return this.repliedBy;
	}

	@Override
	public String getCommentedByCareUnit() {
		return this.commentedByCareUnit;
	}

	@Override
	public boolean isMarkedAsRead() {
		return markedAsRead;
	}

	@Override
	public boolean isLike() {
		return like;
	}

	@Override
	public boolean isHiddenByAdmin() {
		return hiddenByAdmin;
	}
	
	@Override
	public boolean isHiddenByPatient() {
		return hiddenByPatient;
	}

	public String getHealtPlanName() {
		return healtPlanName;
	}
	public PatientBaseView getPatient() {
		return patient;
	}

}
