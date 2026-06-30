package com.adm.supervision.repository;

import com.adm.supervision.domain.ProfilMetier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfilMetier entity.
 *
 * When extending this class, extend ProfilMetierRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ProfilMetierRepository
    extends ProfilMetierRepositoryWithBagRelationships, JpaRepository<ProfilMetier, Long>, JpaSpecificationExecutor<ProfilMetier>
{
    Optional<ProfilMetier> findOneByCodeIgnoreCase(String code);

    default Optional<ProfilMetier> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<ProfilMetier> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<ProfilMetier> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
