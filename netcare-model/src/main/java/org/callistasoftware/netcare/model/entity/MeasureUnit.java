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


/**
 * Known units for measurements. <p>
 * 
 * Please Note: this unit has to be well known with a well defined semantics in order to provide
 * more qualified user-experience down the road. As an example can the mobile device be used as 
 * a measurement device.
 * 
 * @author Peter
 *
 */
public enum MeasureUnit {
	MINUTE,
	METER,
	STEP,
	KILOGRAM,
	GRAM,
	PRESSURE_MMHG,
	FLOW_MLMIN,
	LITER,
	MILLILITER,
	MMOL_LITER,
	QUANTIY,
}
