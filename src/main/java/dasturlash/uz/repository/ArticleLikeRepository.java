package dasturlash.uz.repository;

import dasturlash.uz.entity.ArticleLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleLikeRepository extends JpaRepository<ArticleLikeEntity, Long> {
    @Query("select count(l) from ArticleLikeEntity l where l.article.id = :articleId and l.status = true")
    long countActiveByArticleId(@Param("articleId") String articleId);
}
