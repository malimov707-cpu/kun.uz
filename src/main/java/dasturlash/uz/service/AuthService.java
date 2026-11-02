package dasturlash.uz.service;

import dasturlash.uz.dto.AuthorizationDTO;
import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ProfileDTO registration(RegistrationDTO dto) {
        String email = normalizeEmail(dto.getEmail());
        String phone = normalizePhone(dto.getPhone());
        if (email == null && phone == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone is required");
        }
        if (email != null && profileRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (phone != null && profileRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already exists");
        }

        ProfileEntity profile = new ProfileEntity();
        profile.setName(dto.getName().trim());
        profile.setSurname(dto.getSurname().trim());
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setPassword(passwordEncoder.encode(dto.getPassword()));
        profile.setVisible(true);
        profile.setStatus(ProfileStatus.ACTIVE);
        profile.setRole(ProfileRole.ROLE_USER);

        return toProfileDTO(profileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public ProfileDTO login(AuthorizationDTO dto) {
        String login = dto.getLogin().trim();
        ProfileEntity profile = findByLogin(login)
                .filter(ProfileEntity::isVisible)
                .filter(item -> item.getStatus() == ProfileStatus.ACTIVE)
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(dto.getPassword(), profile.getPassword())) {
            throw invalidCredentials();
        }
        return toProfileDTO(profile);
    }

    private Optional<ProfileEntity> findByLogin(String login) {
        if (login.contains("@")) {
            return profileRepository.findByEmailIgnoreCase(login.toLowerCase(Locale.ROOT));
        }
        return profileRepository.findByPhone(login);
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email/phone or password is incorrect");
    }

    private String normalizeEmail(String email) {
        return email == null || email.isBlank() ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String phone) {
        return phone == null || phone.isBlank() ? null : phone.trim();
    }

    private ProfileDTO toProfileDTO(ProfileEntity profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setSurname(profile.getSurname());
        dto.setEmail(profile.getEmail());
        dto.setPhone(profile.getPhone());
        dto.setStatus(profile.getStatus());
        dto.setRole(profile.getRole());
        dto.setVisible(profile.isVisible());
        dto.setCreatedDate(profile.getCreatedDate());
        dto.setPhotoId(profile.getPhotoId());
        return dto;
    }
}
