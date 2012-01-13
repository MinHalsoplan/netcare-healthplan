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
package org.callistasoftware.netcare.core.api;


public interface Alarm {

	/**
	 * The id of this alarm
	 * @return
	 */
	Long getId();
	
	/**
	 * Get the care unit hsa id
	 * @return
	 */
	String getCareUnitHsaId();
	
	/**
	 * Get for which patient the alarm was triggered
	 * @return
	 */
	Patient getPatient();
	
	/**
	 * The date the alarm was created
	 * @return
	 */
	String getCreatedTime();
	
	/**
	 * The date when the alarm was resolved
	 * @return
	 */
	String getResolvedTime();
	
	/**
	 * The care giver who resolved the alarm
	 * @return
	 */
	CareGiverBaseView getResolvedBy();
	
	/**
	 * The cause of the alarm
	 * @return
	 */
	Option getCause();
	
	/**
	 * The entity this alarm refers to
	 * @return
	 */
	Long getEntityReferenceId();
}
