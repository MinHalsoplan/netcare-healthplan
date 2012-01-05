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

import org.callistasoftware.netcare.core.api.PatientEvent;

/**
 * Implements {@link PatientEvent}.
 * 
 * @author Peter
 *
 */
public class PatientEventImpl implements PatientEvent {
	private static final long serialVersionUID = 1L;
	private int numReports;
	private int dueReports;
	
	private PatientEventImpl() {}

	//
	private PatientEventImpl(int numReports, int dueReports) {
		this();
		this.numReports = numReports;
		this.dueReports = dueReports;
	}
	
	/** Creates a PatientEvent object. */
	public static PatientEvent newPatientEvent(int numReports, int dueReports) {
		PatientEvent pe = new PatientEventImpl(numReports, dueReports);
		return pe;
	}
	
	@Override
	public int getNumReports() {
		return numReports;
	}

	@Override
	public int getDueReports() {
		return dueReports;
	}

}
