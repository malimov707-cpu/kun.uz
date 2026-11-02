package dasturlash.uz.repository;

import dasturlash.uz.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Page<Region> findAllByVisibleTrue(Pageable pageable);
    Optional<Region> findByItemKey(String itemKey);
}
