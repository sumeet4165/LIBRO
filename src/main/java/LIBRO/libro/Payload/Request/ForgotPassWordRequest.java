package LIBRO.libro.Payload.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPassWordRequest {
    @NotNull(message = "email is required")
    private String email;


}
