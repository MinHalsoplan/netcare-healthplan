package org.callistasoftware.netcare.core.api;

public interface EstimationType extends ActivityItemType {

	/**
	 * Returns the minimum scale description.
	 */
	String getMinScaleText();

	/**
	 * Returns the maximum scale description
	 * 
	 * @return
	 */
	String getMaxScaleText();

}
