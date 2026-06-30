package com.adm.supervision.repository;

import com.adm.supervision.domain.Boutique;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Boutique entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoutiqueRepository extends JpaRepository<Boutique, Long>, JpaSpecificationExecutor<Boutique> {
    Optional<Boutique> findOneByCodeIgnoreCase(String code);
}
