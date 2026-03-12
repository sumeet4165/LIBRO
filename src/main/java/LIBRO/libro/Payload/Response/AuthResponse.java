package LIBRO.libro.Payload.Response;

import LIBRO.libro.Payload.DTO.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String jwt;
    private String title;
    private String message;
    private UserDto user;




}
