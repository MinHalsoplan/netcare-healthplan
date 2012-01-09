package org.callistasoftware.netcare.model.entity;


public interface PermissionRestrictedEntity {

	/**
	 * Whether the user is allowed to view information
	 * on this restricted object
	 * @param user - The user who is trying to read
	 * @return
	 */
	boolean isReadAllowed(final UserEntity userId);
	
	/**
	 * Whether the user is allowed to modify information
	 * on the restricted object.
	 * @param user - The user who is trying to write
	 * @return
	 */
	boolean isWriteAllowed(final UserEntity userId);
}
