package com.adm.supervision.repository;

import com.adm.supervision.domain.ParametreGlobal;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ParametreGlobal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParametreGlobalRepository extends JpaRepository<ParametreGlobal, Long>, JpaSpecificationExecutor<ParametreGlobal> {}
