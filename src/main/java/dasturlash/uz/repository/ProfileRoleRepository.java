package dasturlash.uz.repository;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.entity.ProfileRoleEntity;
import dasturlash.uz.enums.ProfileRoleEnum;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfileRoleRepository extends CrudRepository<ProfileRoleEntity, Integer> {

    @Query("select role.roles from ProfileRoleEntity role where role.profileId = ?1")
    List<ProfileRoleEnum> getRoleListByProfileId(Long profileId);

    @Transactional
    @Modifying
    @Query("delete from ProfileRoleEntity role where role.profileId = ?1 and role.roles = ?2")
    void deleteByIdAndRoleEnum(Long profileId, ProfileRoleEnum role);

    @Transactional
    @Modifying
    @Query("delete from ProfileRoleEntity role where role.profileId = ?1")
    void deleteByProfileId(Long profileId);
}