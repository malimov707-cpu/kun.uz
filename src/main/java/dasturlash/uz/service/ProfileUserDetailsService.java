package dasturlash.uz.service;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.repository.ProfileRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ProfileUserDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;

    public ProfileUserDetailsService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        ProfileEntity profilelEntity;
        if (login.contains("@")) {
            profilelEntity = profileRepository.findByEmailIgnoreCase(login.toLowerCase(Locale.ROOT))
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        } else {
            profilelEntity = profileRepository.findByPhone(login)
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        }

        if (!profilelEntity.isVisible() || profilelEntity.getRole() == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        return User.withUsername(login)
                .password(profilelEntity.getPassword())
                .authorities(profilelEntity.getRole().name())
                .disabled(profilelEntity.getStatus() != dasturlash.uz.enums.ProfileStatus.ACTIVE)
                .build();
    }
}
