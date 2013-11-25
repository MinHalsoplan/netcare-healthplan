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
package org.callistasoftware.netcare.core.api.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

	public static final long MILLIS_PER_MINUTE = (60L * 1000L);
	public static final long MILLIS_PER_HOUR = (60L * MILLIS_PER_MINUTE);
	public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
	
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	
	/**
	 * Format the give date object. Return null if date is null
	 * @param date
	 * @return
	 */
	public static String toDateTime(final Date date) {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
		return date != null ? sdf.format(date) : null;
	}
	
	public static final String toDate(final Date date) {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		return date != null ? sdf.format(date) : null;
	}
}
