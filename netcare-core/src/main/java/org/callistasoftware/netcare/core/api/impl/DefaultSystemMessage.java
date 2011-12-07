package org.callistasoftware.netcare.core.api.impl;

import org.callistasoftware.netcare.core.api.SystemMessage;

/**
 * Default implementation of a system message
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public class DefaultSystemMessage implements SystemMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	
	public DefaultSystemMessage(final String code, final String message) {
		this.code = code;
		this.message = message;
	}
	
	public DefaultSystemMessage(final String message) {
		this.message = message;
	}
	
	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
