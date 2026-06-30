package com.adm.supervision.repository;

import com.adm.supervision.domain.SousFamilleArticle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SousFamilleArticle entity.
 */
@Repository
public interface SousFamilleArticleRepository
    extends JpaRepository<SousFamilleArticle, Long>, JpaSpecificationExecutor<SousFamilleArticle>
{
    default Optional<SousFamilleArticle> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SousFamilleArticle> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SousFamilleArticle> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sousFamilleArticle from SousFamilleArticle sousFamilleArticle left join fetch sousFamilleArticle.familleArticle",
        countQuery = "select count(sousFamilleArticle) from SousFamilleArticle sousFamilleArticle"
    )
    Page<SousFamilleArticle> findAllWithToOneRelationships(Pageable pageable);

    @Query("select sousFamilleArticle from SousFamilleArticle sousFamilleArticle left join fetch sousFamilleArticle.familleArticle")
    List<SousFamilleArticle> findAllWithToOneRelationships();

    @Query(
        "select sousFamilleArticle from SousFamilleArticle sousFamilleArticle left join fetch sousFamilleArticle.familleArticle where sousFamilleArticle.id =:id"
    )
    Optional<SousFamilleArticle> findOneWithToOneRelationships(@Param("id") Long id);
}
