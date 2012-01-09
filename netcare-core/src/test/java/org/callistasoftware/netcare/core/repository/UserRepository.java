package org.callistasoftware.netcare.core.repository;

import org.callistasoftware.netcare.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User repository
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 *
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
