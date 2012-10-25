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
package org.callistasoftware.netcare.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="nc_activity_comment")
public class ActivityCommentEntity implements PermissionRestrictedEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private String comment;
	
	@Column
	private String reply;
	
	@ManyToOne
	private CareActorEntity commentedBy;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date commentedAt;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date repliedAt;
	
	@ManyToOne
	private ScheduledActivityEntity activity;

	ActivityCommentEntity() {
		
	}
	
	public static ActivityCommentEntity newEntity(final String comment, final CareActorEntity commentedBy, final ScheduledActivityEntity activity) {
		final ActivityCommentEntity ent = new ActivityCommentEntity();
		ent.setActivity(activity);
		ent.setComment(comment);
		ent.setCommentedBy(commentedBy);
		ent.setCommentedAt(new Date());
		
		return ent;
	}
	
	public Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	void setComment(String comment) {
		this.comment = comment;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public CareActorEntity getCommentedBy() {
		return commentedBy;
	}

	void setCommentedBy(CareActorEntity commentedBy) {
		this.commentedBy = commentedBy;
	}

	public Date getCommentedAt() {
		return commentedAt;
	}

	void setCommentedAt(Date commentedAt) {
		this.commentedAt = commentedAt;
	}

	public Date getRepliedAt() {
		return repliedAt;
	}

	public void setRepliedAt(Date repliedAt) {
		this.repliedAt = repliedAt;
	}

	public ScheduledActivityEntity getActivity() {
		return activity;
	}

	void setActivity(ScheduledActivityEntity activity) {
		this.activity = activity;
	}

	@Override
	public boolean isReadAllowed(UserEntity user) {
		return this.isWriteAllowed(user);
	}

	@Override
	public boolean isWriteAllowed(UserEntity user) {
		if (user.isCareActor()) {
			return user.getId().equals(this.getCommentedBy().getId());
		}
		
		return user.getId().equals(this.getActivity().getActivityDefinitionEntity().getHealthPlan().getForPatient().getId());
	}
}
