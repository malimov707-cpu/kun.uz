package dasturlash.uz.service;

import dasturlash.uz.entity.Profile;
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
    public Profile create(Profile profile) {
        if (profile.getPassword() == null || profile.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        profile.setVisible(true);
        return repository.save(profile);
    }

    @Transactional(readOnly = true)
    public Page<Profile> getAll(Pageable pageable) {
        return repository.findAllByVisibleTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Profile> filter(String name, String surname, String phone, ProfileRole role,
                                LocalDateTime createdFrom, LocalDateTime createdTo, Pageable pageable) {
        if (createdFrom != null && createdTo != null && createdFrom.isAfter(createdTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "createdFrom must be before createdTo");
        }
        return repository.searchVisible(name, surname, phone, role, createdFrom, createdTo, pageable);
    }

    @Transactional(readOnly = true)
    public Profile getById(Long id) {
        return repository.findById(id)
                .filter(Profile::isVisible)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil topilmadi"));
    }

    @Transactional
    public Profile update(Long id, Profile profile) {
        Profile existing = getById(id);
        if (profile.getName() != null) {
            existing.setName(profile.getName());
        }
        if (profile.getSurname() != null) {
            existing.setSurname(profile.getSurname());
        }
        if (profile.getEmail() != null) {
            existing.setEmail(profile.getEmail());
        }
        if (profile.getPhone() != null) {
            existing.setPhone(profile.getPhone());
        }
        if (profile.getPassword() != null && !profile.getPassword().isBlank()) {
            existing.setPassword(profile.getPassword());
        }
        if (profile.getStatus() != null) {
            existing.setStatus(profile.getStatus());
        }
        if (profile.getRole() != null) {
            existing.setRole(profile.getRole());
        }
        if (profile.getPhotoId() != null) {
            existing.setPhotoId(profile.getPhotoId());
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        getById(id).setVisible(false);
    }

    @Transactional
    public Profile updatePhoto(Long id, Long photoId) {
        Profile profile = getById(id);
        profile.setPhotoId(photoId);
        return profile;
    }
}
