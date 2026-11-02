package dasturlash.uz.repository;

import dasturlash.uz.entity.ArticleEntity;
import dasturlash.uz.enums.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, String> {
    @Query("""
            select distinct a from ArticleEntity a join a.articleTypes t
            where t.id in :typeIds and a.visible = true and a.status = :status
            order by a.createdDate desc
            """)
    List<ArticleEntity> findLatestByTypes(@Param("typeIds") List<Long> typeIds,
                                          @Param("status") ArticleStatus status, Pageable pageable);

    List<ArticleEntity> findByVisibleTrueAndStatusOrderByCreatedDateDesc(ArticleStatus status, Pageable pageable);

    @Query("""
            select distinct a from ArticleEntity a
            where a.visible = true and (:status is null or a.status = :status)
              and a.id not in :excludedIds
            order by a.createdDate desc
            """)
    List<ArticleEntity> findLatestExcludingIds(@Param("excludedIds") List<String> excludedIds,
                                               @Param("status") ArticleStatus status, Pageable pageable);

    @Query("""
            select distinct a from ArticleEntity a join a.articleTypes t
            where t.id in :typeIds and a.visible = true and a.status = :status and a.id <> :articleId
            order by a.createdDate desc
            """)
    List<ArticleEntity> findLatestByTypesExceptArticle(@Param("typeIds") List<Long> typeIds,
                                                        @Param("articleId") String articleId,
                                                        @Param("status") ArticleStatus status,
                                                        Pageable pageable);

    List<ArticleEntity> findByVisibleTrueAndStatusOrderByViewCountDescCreatedDateDesc(
            ArticleStatus status, Pageable pageable);

    @Query("""
            select distinct a from ArticleEntity a join a.tags tag
            where lower(tag.name) = lower(:tagName) and a.visible = true and a.status = :status
              and a.id <> :excludedArticleId
            order by a.createdDate desc
            """)
    List<ArticleEntity> findByTagNameExceptArticle(@Param("tagName") String tagName,
                                      @Param("excludedArticleId") String excludedArticleId,
                                      @Param("status") ArticleStatus status, Pageable pageable);

    @Query("""
            select distinct a from ArticleEntity a join a.tags tag
            where a.id = :articleId and lower(tag.name) = lower(:tagName)
              and a.visible = true and a.status = :status
            """)
    java.util.Optional<ArticleEntity> findByIdAndTagName(@Param("articleId") String articleId,
                                                         @Param("tagName") String tagName,
                                                         @Param("status") ArticleStatus status);

    @Query("""
            select distinct a from ArticleEntity a join a.articleTypes t
            where t.id in :typeIds and a.region.itemKey = :regionKey
              and a.visible = true and a.status = :status
            order by a.createdDate desc
            """)
    List<ArticleEntity> findLatestByTypesAndRegionKey(@Param("typeIds") List<Long> typeIds,
                                                      @Param("regionKey") String regionKey,
                                                      @Param("status") ArticleStatus status,
                                                      Pageable pageable);

    @Query("""
            select a from ArticleEntity a
            where a.region.itemKey = :regionKey and a.visible = true and a.status = :status
            order by a.createdDate desc
            """)
    Page<ArticleEntity> findByRegionKey(@Param("regionKey") String regionKey,
                                        @Param("status") ArticleStatus status, Pageable pageable);

    @Query("""
            select a from ArticleEntity a
            where a.category.categoryKey = :categoryKey and a.visible = true and a.status = :status
            order by a.createdDate desc
            """)
    List<ArticleEntity> findLatestByCategoryKey(@Param("categoryKey") String categoryKey,
                                                @Param("status") ArticleStatus status, Pageable pageable);

    @Query("""
            select a from ArticleEntity a
            where a.category.categoryKey = :categoryKey and a.visible = true and a.status = :status
            order by a.createdDate desc
            """)
    Page<ArticleEntity> findByCategoryKey(@Param("categoryKey") String categoryKey,
                                          @Param("status") ArticleStatus status, Pageable pageable);

    @Query("""
            select a from ArticleEntity a
            where a.visible = true and (:status is null or a.status = :status)
              and (:id is null or a.id = :id)
              and (:title is null or lower(a.title) like lower(concat('%', :title, '%')))
              and (:regionId is null or a.region.id = :regionId)
              and (:categoryId is null or a.category.id = :categoryId)
              and (:createdFrom is null or a.createdDate >= :createdFrom)
              and (:createdTo is null or a.createdDate <= :createdTo)
              and (:publishedFrom is null or a.publishedDate >= :publishedFrom)
              and (:publishedTo is null or a.publishedDate <= :publishedTo)
              and (:moderatorId is null or a.moderator.id = :moderatorId)
              and (:publisherId is null or a.publisher.id = :publisherId)
            order by a.createdDate desc
            """)
    Page<ArticleEntity> search(@Param("id") String id,
                               @Param("title") String title,
                               @Param("regionId") Long regionId,
                               @Param("categoryId") Long categoryId,
                               @Param("createdFrom") java.time.LocalDateTime createdFrom,
                               @Param("createdTo") java.time.LocalDateTime createdTo,
                               @Param("publishedFrom") java.time.LocalDateTime publishedFrom,
                               @Param("publishedTo") java.time.LocalDateTime publishedTo,
                               @Param("moderatorId") Long moderatorId,
                               @Param("publisherId") Long publisherId,
                               @Param("status") ArticleStatus status, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ArticleEntity a set a.viewCount = a.viewCount + 1 where a.id = :id and a.visible = true")
    @org.springframework.transaction.annotation.Transactional
    int incrementViewCount(@Param("id") String id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ArticleEntity a set a.sharedCount = a.sharedCount + 1 where a.id = :id and a.visible = true")
    @org.springframework.transaction.annotation.Transactional
    int incrementSharedCount(@Param("id") String id);
}
