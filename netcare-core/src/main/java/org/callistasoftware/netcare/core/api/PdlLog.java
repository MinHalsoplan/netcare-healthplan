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
package org.callistasoftware.netcare.core.api;

import java.io.Serializable;

import org.callistasoftware.netcare.core.api.impl.PdlLogImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Keeps scheduled activity information, used to display a list of activities.
 * 
 * @author Peter
 */
@JsonDeserialize(as = PdlLogImpl.class)
public interface PdlLog extends Serializable {

	public long getId();

	public String getHsaId();

	public String getCareActorName();

	public String getCivicId();

	public String getPatientName();

	public String getAction();
	
	public String getHealtPlanName();

}
