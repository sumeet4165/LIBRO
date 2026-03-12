package LIBRO.libro.Service;

import LIBRO.libro.Exceptions.UserException;
import LIBRO.libro.Payload.DTO.UserDto;
import LIBRO.libro.Payload.Response.AuthResponse;
import jakarta.mail.MessagingException;

import javax.security.auth.login.LoginException;

public interface AuthService {

        AuthResponse login(String email, String password) throws UserException;

        AuthResponse register(UserDto Req) throws UserException;

        void createResetPassWordToken(String email) throws UserException, MessagingException;

        void resetPassWordT(String token,String newpass) throws Exception;
}
