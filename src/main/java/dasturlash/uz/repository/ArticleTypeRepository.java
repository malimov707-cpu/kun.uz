package dasturlash.uz.repository;

import dasturlash.uz.entity.ArticleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleTypeRepository extends JpaRepository<ArticleType, Long> {
    Page<ArticleType> findAllByVisibleTrue(Pageable pageable);
    Optional<ArticleType> findByItemKey(String itemKey);
}
