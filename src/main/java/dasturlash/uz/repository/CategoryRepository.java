package dasturlash.uz.repository;

import dasturlash.uz.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Page<CategoryEntity> findAllByVisibleTrue(Pageable pageable);
    Optional<CategoryEntity> findByCategoryKey(String categoryKey);
}
