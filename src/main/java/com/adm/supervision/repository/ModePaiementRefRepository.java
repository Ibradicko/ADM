package com.adm.supervision.repository;

import com.adm.supervision.domain.ModePaiementRef;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ModePaiementRef entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ModePaiementRefRepository extends JpaRepository<ModePaiementRef, Long>, JpaSpecificationExecutor<ModePaiementRef> {}
