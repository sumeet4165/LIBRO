package LIBRO.libro.Payload.DTO;

import LIBRO.libro.Domain.AuthProvider;
import LIBRO.libro.Domain.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {


    private Long id;

    @NotNull(message = "email is required")

    private String email;

    @NotNull(message = "full name is required")
    private String fullName;

    @NotNull(message = "password is required")
    private String password;

    private UserRole role;

    private String phone;
    private String username;
    private LocalDateTime lastLogin;





}
