package com.adm.supervision.repository;

import com.adm.supervision.domain.UniteMesure;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UniteMesure entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UniteMesureRepository extends JpaRepository<UniteMesure, Long>, JpaSpecificationExecutor<UniteMesure> {}
