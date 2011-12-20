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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.messages.SystemMessage;

/**
 * Default implementation of a service result
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 * @param <T>
 */
public class ServiceResultImpl<T extends Serializable> implements ServiceResult<T> {

	private boolean success;
	
	private T data;
	
	private List<SystemMessage> infoMessages;
	private List<SystemMessage> warningMessages;
	private List<SystemMessage> errorMessages;
	
	private ServiceResultImpl(final boolean success, final T data) {
		this.infoMessages = new ArrayList<SystemMessage>();
		this.warningMessages = new ArrayList<SystemMessage>();
		this.errorMessages = new ArrayList<SystemMessage>();
		
		this.success = success;
		this.data = data;
	}
	
	public static <T extends Serializable> ServiceResultImpl<T> createSuccessResult(final T data, final SystemMessage infoMessage) {
		final ServiceResultImpl<T> result = new ServiceResultImpl<T>(true, data);
		result.addInfoMessage(infoMessage);
		
		return result;
	}
	
	public static <T extends Serializable> ServiceResultImpl<T> createFailedResult(final SystemMessage errMsg) {
		final ServiceResultImpl<T> result = new ServiceResultImpl<T>(false, null);
		result.addErrorMessage(errMsg);
		
		return result;
	}
	
	@Override
	public boolean isSuccess() {
		return this.success;
	}

	@Override
	public boolean hasWarnings() {
		return !this.warningMessages.isEmpty();
	}

	@Override
	public T getData() {
		return this.data;
	}

	@Override
	public List<SystemMessage> getErrorMessages() {
		return Collections.unmodifiableList(this.errorMessages);
	}
	
	public void addErrorMessage(final SystemMessage msg) {
		this.errorMessages.add(msg);
	}

	@Override
	public List<SystemMessage> getWarningMessages() {
		return Collections.unmodifiableList(this.warningMessages);
	}
	
	public void addWarningMessage(final SystemMessage msg) {
		this.warningMessages.add(msg);
	}

	@Override
	public List<SystemMessage> getInfoMessages() {
		return Collections.unmodifiableList(this.infoMessages);
	}
	
	public void addInfoMessage(final SystemMessage msg) {
		this.infoMessages.add(msg);
	}

}
