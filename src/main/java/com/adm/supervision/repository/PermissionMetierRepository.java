package com.adm.supervision.repository;

import com.adm.supervision.domain.PermissionMetier;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PermissionMetier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PermissionMetierRepository extends JpaRepository<PermissionMetier, Long>, JpaSpecificationExecutor<PermissionMetier> {}
