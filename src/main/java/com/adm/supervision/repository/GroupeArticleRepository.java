package com.adm.supervision.repository;

import com.adm.supervision.domain.GroupeArticle;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GroupeArticle entity.
 */
@Repository
public interface GroupeArticleRepository extends JpaRepository<GroupeArticle, Long>, JpaSpecificationExecutor<GroupeArticle> {}
