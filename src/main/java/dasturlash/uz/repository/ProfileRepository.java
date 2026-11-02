package dasturlash.uz.repository;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findByEmailIgnoreCase(String email);

    Optional<ProfileEntity> findByPhone(String phone);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    @Query("""
            select p from ProfileEntity p
            where p.visible = true
              and (:name is null or lower(p.name) like lower(concat('%', :name, '%')))
              and (:surname is null or lower(p.surname) like lower(concat('%', :surname, '%')))
              and (:phone is null or p.phone like concat('%', :phone, '%'))
              and (:role is null or p.role = :role)
              and (:createdFrom is null or p.createdDate >= :createdFrom)
              and (:createdTo is null or p.createdDate <= :createdTo)
            """)
    Page<ProfileEntity> searchVisible(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("phone") String phone,
            @Param("role") ProfileRole role,
            @Param("createdFrom") java.time.LocalDateTime createdFrom,
            @Param("createdTo") java.time.LocalDateTime createdTo,
            Pageable pageable);

    Page<ProfileEntity> findAllByVisibleTrue(Pageable pageable);
}
