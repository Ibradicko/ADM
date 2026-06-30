package com.adm.supervision.repository;

import com.adm.supervision.domain.ParametreCodeBarres;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ParametreCodeBarres entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParametreCodeBarresRepository
    extends JpaRepository<ParametreCodeBarres, Long>, JpaSpecificationExecutor<ParametreCodeBarres>
{
    Optional<ParametreCodeBarres> findFirstByActifTrueOrderByIdAsc();
}
