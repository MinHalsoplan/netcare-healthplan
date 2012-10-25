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

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "nc_activity_item_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "activity_item_type", discriminatorType = DiscriminatorType.STRING)
public class ActivityItemTypeEntity implements Comparable<ActivityItemTypeEntity> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", length = 32, nullable = false)
	private String name;

	@Column(name = "alarm_enabled")
	private boolean alarmEnabled;

	@Column(name = "seqno")
	private int seqno;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_type_id")
	private ActivityTypeEntity activityType;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public boolean isAlarmEnabled() {
		return alarmEnabled;
	}

	public void setAlarmEnabled(boolean alarmEnabled) {
		this.alarmEnabled = alarmEnabled;
	}

	public int getSeqno() {
		return seqno;
	}

	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}

	public ActivityTypeEntity getActivityType() {
		return activityType;
	}

	void setActivityType(ActivityTypeEntity activityType) {
		this.activityType = activityType;
	}

	@Override
	public int compareTo(ActivityItemTypeEntity m) {
		return (this.getSeqno() - m.getSeqno());
	}
}
