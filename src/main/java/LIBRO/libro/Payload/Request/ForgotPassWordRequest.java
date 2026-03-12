package LIBRO.libro.Payload.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgotPassWordRequest {
    @NotNull(message = "email is required")
    private String email;


}
