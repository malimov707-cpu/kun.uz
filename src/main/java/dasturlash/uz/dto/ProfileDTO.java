package dasturlash.uz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProfileDTO {
    private Long id;

    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "Surname required")
    private String surname;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private ProfileStatus status;
    private ProfileRole role;
    private Boolean visible;
    private LocalDateTime createdDate;
    private Long photoId;
}
