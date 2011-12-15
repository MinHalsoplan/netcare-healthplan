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
package org.callistasoftware.netcare.core.api;

import java.io.Serializable;
import java.util.List;

import org.callistasoftware.netcare.core.api.messages.SystemMessage;

/**
 * Defines a result of a service operation
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface ServiceResult<T extends Serializable> {

	/**
	 * True if the operation was successful
	 * @return
	 */
	boolean isSuccess();
	
	/**
	 * True if the call was successful but it has warnings
	 * attached
	 * @return
	 */
	boolean hasWarnings();
	
	/**
	 * Get the data that was returned from the service call. Null
	 * if the call was unsuccessful
	 * @return
	 */
	T getData();
	
	/**
	 * Get the error messages attached to the service
	 * result
	 * @return
	 */
	List<SystemMessage> getErrorMessages();
	
	/**
	 * Get any warning messages attached to the service 
	 * result
	 * @return
	 */
	List<SystemMessage> getWarningMessages();
	
	/**
	 * Get any info messages attached to the service 
	 * result
	 * @return
	 */
	List<SystemMessage> getInfoMessages();
}
