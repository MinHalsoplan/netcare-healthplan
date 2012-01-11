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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.messages.MessageType;
import org.callistasoftware.netcare.core.api.messages.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of a service result
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 * @param <T>
 */
public class ServiceResultImpl<T extends Serializable> implements ServiceResult<T> {

	private static final Logger log = LoggerFactory.getLogger(ServiceResultImpl.class);
	
	private boolean success;
	
	private T data;
	
	private List<SystemMessage> successMessages;
	private List<SystemMessage> infoMessages;
	private List<SystemMessage> warningMessages;
	private List<SystemMessage> errorMessages;
	
	private ServiceResultImpl(final boolean success, final T data) {
		this.successMessages = new ArrayList<SystemMessage>();
		this.infoMessages = new ArrayList<SystemMessage>();
		this.warningMessages = new ArrayList<SystemMessage>();
		this.errorMessages = new ArrayList<SystemMessage>();
		
		this.success = success;
		this.data = data;
	}
	
	public static <T extends Serializable> ServiceResultImpl<T> createSuccessResult(final T data, final SystemMessage infoMessage) {
		log.debug("Constructing success message");
		final ServiceResultImpl<T> result = new ServiceResultImpl<T>(true, data);
		result.addMessage(infoMessage);
		
		return result;
	}
	
	public static <T extends Serializable> ServiceResultImpl<T> createFailedResult(final SystemMessage errMsg) {
		log.debug("Constructing fail message");
		final ServiceResultImpl<T> result = new ServiceResultImpl<T>(false, null);
		result.addMessage(errMsg);
		
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
	
	private void addErrorMessage(final SystemMessage msg) {
		if (msg.getType().equals(MessageType.ERROR)) {
			this.errorMessages.add(msg);
			return;
		}
		
		throw new IllegalStateException("Cannot add message of type " + msg.getType().name() + " error list");
	}

	@Override
	public List<SystemMessage> getWarningMessages() {
		return Collections.unmodifiableList(this.warningMessages);
	}
	
	private void addWarningMessage(final SystemMessage msg) {
		if (msg.getType().equals(MessageType.WARNING)) {
			this.warningMessages.add(msg);
			return;
		}
		
		throw new IllegalStateException("Cannot add message of type " + msg.getType().name() + " warning list");
	}

	@Override
	public List<SystemMessage> getInfoMessages() {
		return Collections.unmodifiableList(this.infoMessages);
	}
	
	private void addInfoMessage(final SystemMessage msg) {
		if (msg.getType().name().equals(MessageType.INFO.name())) {
			this.infoMessages.add(msg);
			return;
		}
		
		throw new IllegalStateException("Cannot add message of type " + msg.getType().name() + " info list");
	}

	@Override
	public List<SystemMessage> getSuccessMessages() {
		return Collections.unmodifiableList(this.successMessages);
	}
	
	private void addSuccessMessage(final SystemMessage msg) {
		if (msg.getType().equals(MessageType.SUCCESS)) {
			this.successMessages.add(msg);
			return;
		}
		
		throw new IllegalStateException("Cannot add message of type " + msg.getType().name() + " success list");
	}
	
	public void addMessage(final SystemMessage msg) {
		log.debug("Adding system message {} of type {}", msg.getMessage(), msg.getType().name());
		
		switch (msg.getType()) {
		case INFO: this.addInfoMessage(msg);return;
		case ERROR: this.addErrorMessage(msg); return;
		case SUCCESS: this.addSuccessMessage(msg); return;
		case WARNING: this.addWarningMessage(msg); return;
		}
	}

}
