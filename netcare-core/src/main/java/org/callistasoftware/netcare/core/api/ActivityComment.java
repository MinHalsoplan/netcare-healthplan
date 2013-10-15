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

import java.io.Serializable;

import org.callistasoftware.netcare.core.api.impl.ActivityCommentImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize(as=ActivityCommentImpl.class)
public interface ActivityComment extends Serializable {

	/**
	 * Get the id of the comment
	 * @return
	 */
	Long getId();
	
	/**
	 * Get the comment
	 * @return
	 */
	String getComment();
	
	/**
	 * If the comment has been read
	 */
	boolean isMarkedAsRead();
	
	/**
	 * If the comment is decorated with a "like"
	 * @return
	 */
	boolean isLike();
	
	/**
	 * Is the comment hidden by admin?
	 * @return
	 */
	boolean isHiddenByAdmin();
	
	/**
	 * Is the comment hidden by the patient?
	 * @return
	 */
	boolean isHiddenByPatient();
	
	/**
	 * Get the reply
	 * @return
	 */
	String getReply();
	
	/**
	 * Get the care giver who commented
	 * @return
	 */
	String getCommentedBy();
	
	/**
	 * The care actor's care unit
	 * @return
	 */
	String getCommentedByCareUnit();
	
	/**
	 * Get timestamp for the comment
	 * @return
	 */
	String getCommentedAt();
	
	/**
	 * Get the replied timestamp
	 * @return
	 */
	String getRepliedAt();
	
	/**
	 * Get the name of the patient who replied
	 * @return
	 */
	String getRepliedBy();
	
	/**
	 * Get the activity
	 * @return
	 */
	String getActivityName();
	
	/**
	 * Get the date when activity was reported
	 * @return
	 */
	String getActivityReportedAt();	
	
	/**
	 * Get Healt Plan Name
	 * @return
	 */
	String getHealtPlanName();
	
	/**
	 * Get Patient
	 * @return
	 */
	PatientBaseView getPatient();


}
