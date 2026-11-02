package dasturlash.uz.entity;

import dasturlash.uz.enums.ProfileRoleEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "profile_role")
public class ProfileRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false)
    private ProfileRoleEnum roles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public ProfileRoleEnum getRoles() {
        return roles;
    }

    public void setRoles(ProfileRoleEnum roles) {
        this.roles = roles;
    }
}
