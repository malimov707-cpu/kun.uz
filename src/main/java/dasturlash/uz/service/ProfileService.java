package dasturlash.uz.service;

import dasturlash.uz.entity.Profile;
import dasturlash.uz.repository.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public Profile create(Profile profile) {
        return repository.save(profile);
    }

    public Page<Profile> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Profile getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil topilmadi"));
    }

    public Profile update(Long id, Profile profile) {
        getById(id);
        profile.setId(id);
        return repository.save(profile);
    }

    public void delete(Long id) {
        repository.delete(getById(id));
    }
}
