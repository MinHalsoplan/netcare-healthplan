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
package org.callistasoftware.netcare.core.api.impl;

import java.util.Date;

import org.callistasoftware.netcare.core.api.ScheduledActivity;

public class ScheduledActivityImpl implements ScheduledActivity {

	private long id;
	private  String name;
	private int targetValue;
	private boolean due;
	private String scheduledTime;
	
	/** For test only */
	public static ScheduledActivity newBean(long id, Date time, int target, String name) {
		ScheduledActivityImpl a = new ScheduledActivityImpl();
		
		a.id = id;
		a.targetValue = target;
		a.name = name;
		a.due = time.after(new Date());
		a.scheduledTime = format(time);
		
		return a;
	}
	
	//
	static String format(Date time) {
		return String.format("%1$tF %1$tR", time);
	}
	
	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String getScheduledTime() {
		// TODO Auto-generated method stub
		return scheduledTime;
	}

	@Override
	public int getTargetValue() {
		// TODO Auto-generated method stub
		return targetValue;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public boolean isDue() {
		// TODO Auto-generated method stub
		return due;
	}
	
}
