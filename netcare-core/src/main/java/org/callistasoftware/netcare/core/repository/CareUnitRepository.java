package org.callistasoftware.netcare.core.repository;

import org.callistasoftware.netcare.model.entity.CareUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareUnitRepository extends JpaRepository<CareUnitEntity, Long> {

	/**
	 * Find the care unit with the specified hsa id
	 * @param hsaId
	 * @return
	 */
	CareUnitEntity findByHsaId(final String hsaId);
}
