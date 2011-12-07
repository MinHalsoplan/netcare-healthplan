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
package org.callistasoftware.netcare.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class ScheduledActivityEntity implements Comparable<ScheduledActivityEntity> {
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date scheduledTime;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportedTime;
	
	@Column
	private int reportedValue;

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setReportedTime(Date reportedTime) {
		this.reportedTime = reportedTime;
	}

	public Date getReportedTime() {
		return reportedTime;
	}

	public void setReportedValue(int reportedValue) {
		this.reportedValue = reportedValue;
	}

	public int getReportedValue() {
		return reportedValue;
	}

	@Override
	public int compareTo(ScheduledActivityEntity r) {
		return scheduledTime.compareTo(r.getScheduledTime());
	}
}
