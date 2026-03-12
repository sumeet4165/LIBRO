package LIBRO.libro.Payload.Request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "email is required")
    private String email;

    @NotNull(message = "password is required")
    private String password;
}
