package com.adm.supervision.repository;

import com.adm.supervision.domain.FamilleArticle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FamilleArticle entity.
 */
@Repository
public interface FamilleArticleRepository extends JpaRepository<FamilleArticle, Long>, JpaSpecificationExecutor<FamilleArticle> {
    default Optional<FamilleArticle> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FamilleArticle> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FamilleArticle> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select familleArticle from FamilleArticle familleArticle left join fetch familleArticle.groupeArticle",
        countQuery = "select count(familleArticle) from FamilleArticle familleArticle"
    )
    Page<FamilleArticle> findAllWithToOneRelationships(Pageable pageable);

    @Query("select familleArticle from FamilleArticle familleArticle left join fetch familleArticle.groupeArticle")
    List<FamilleArticle> findAllWithToOneRelationships();

    @Query(
        "select familleArticle from FamilleArticle familleArticle left join fetch familleArticle.groupeArticle where familleArticle.id =:id"
    )
    Optional<FamilleArticle> findOneWithToOneRelationships(@Param("id") Long id);
}
