package com.adm.supervision.repository;

import com.adm.supervision.domain.LotEtiquettes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LotEtiquettes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LotEtiquettesRepository extends JpaRepository<LotEtiquettes, Long>, JpaSpecificationExecutor<LotEtiquettes> {}
