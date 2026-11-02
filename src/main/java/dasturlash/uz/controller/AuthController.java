package dasturlash.uz.controller;

import dasturlash.uz.dto.AuthorizationDTO;
import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileDTO registration(@Valid @RequestBody RegistrationDTO request) {
        return authService.registration(request);
    }

    @PostMapping("/login")
    public ProfileDTO login(@Valid @RequestBody AuthorizationDTO request) {
        return authService.login(request);
    }
}
