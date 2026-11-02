package dasturlash.uz.repository;

import dasturlash.uz.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByNameIgnoreCase(String name);
}
