package dasturlash.uz.service;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.repository.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProfileEntity create(ProfileEntity profilelEntity) {
        if (profilelEntity.getPassword() == null || profilelEntity.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        profilelEntity.setVisible(true);
        return repository.save(profilelEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProfileEntity> getAll(Pageable pageable) {
        return repository.findAllByVisibleTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProfileEntity> filter(String name, String surname, String phone, ProfileRole role,
                                      LocalDateTime createdFrom, LocalDateTime createdTo, Pageable pageable) {
        if (createdFrom != null && createdTo != null && createdFrom.isAfter(createdTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "createdFrom must be before createdTo");
        }
        return repository.searchVisible(name, surname, phone, role, createdFrom, createdTo, pageable);
    }

    @Transactional(readOnly = true)
    public ProfileEntity getById(Long id) {
        return repository.findById(id)
                .filter(ProfileEntity::isVisible)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil topilmadi"));
    }

    @Transactional
    public ProfileEntity update(Long id, ProfileEntity profilelEntity) {
        ProfileEntity existing = getById(id);
        if (profilelEntity.getName() != null) {
            existing.setName(profilelEntity.getName());
        }
        if (profilelEntity.getSurname() != null) {
            existing.setSurname(profilelEntity.getSurname());
        }
        if (profilelEntity.getEmail() != null) {
            existing.setEmail(profilelEntity.getEmail());
        }
        if (profilelEntity.getPhone() != null) {
            existing.setPhone(profilelEntity.getPhone());
        }
        if (profilelEntity.getPassword() != null && !profilelEntity.getPassword().isBlank()) {
            existing.setPassword(profilelEntity.getPassword());
        }
        if (profilelEntity.getStatus() != null) {
            existing.setStatus(profilelEntity.getStatus());
        }
        if (profilelEntity.getRole() != null) {
            existing.setRole(profilelEntity.getRole());
        }
        if (profilelEntity.getPhotoId() != null) {
            existing.setPhotoId(profilelEntity.getPhotoId());
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        getById(id).setVisible(false);
    }

    @Transactional
    public ProfileEntity updatePhoto(Long id, Long photoId) {
        ProfileEntity profilelEntity = getById(id);
        profilelEntity.setPhotoId(photoId);
        return profilelEntity;
    }
}
