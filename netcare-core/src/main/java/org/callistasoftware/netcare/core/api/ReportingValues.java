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
package org.callistasoftware.netcare.core.api;

import java.io.Serializable;

/**
 * Keeps values used for reporting,
 */
public interface ReportingValues extends Serializable {

	/**
	 * This is the parent's acitivty id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * The type can be any of the types in ActivityItemType.
	 * 
	 * @return the type
	 */
	String getType();

	/**
	 * If the type is 'measurement' the activity has a subtype that is either
	 * SINGLE_VALUE or INTERVAL.
	 * 
	 * @return the subtype
	 */
	String getSubtype();

	/**
	 * This is the label at the top of the diagram.
	 * 
	 * @return the label
	 */
	String getLabel();

	/**
	 * Represents all plotted values in a diagram.
	 * 
	 * @return the values
	 */
	Object[] getReportedValues();

	/**
	 * If the type is a single measurement, the target values are plotted out.
	 * 
	 * @return the targets
	 */
	Object[] getTargets();

	/**
	 * If the type is an interval measurement, the min target values are plotted
	 * out.
	 * 
	 * @return the min targets
	 */
	Object[] getMinTargets();

	/**
	 * If the type is an interval measurement, the max target values are plotted
	 * out.
	 * 
	 * @return the max targets
	 */
	Object[] getMaxTargets();

	/**
	 * The unit plotted out on the y-axis.
	 * 
	 * @return the unit
	 */
	String getUnit();

	/**
	 * If the type is yesno, this is the number of yes's.
	 * 
	 * @return the yes's
	 */
	int getPercentYes();

	/**
	 * If the type is yesno, this is the number of no's.
	 * 
	 * @return the no's
	 */
	int getPercentNo();

	/**
	 * IF the type is yesno, this is the question.
	 * 
	 * @return the question
	 */
	String getQuestion();

}