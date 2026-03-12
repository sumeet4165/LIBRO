package LIBRO.libro.Controllers;


import LIBRO.libro.Exceptions.UserException;
import LIBRO.libro.Payload.DTO.UserDto;
import LIBRO.libro.Payload.Request.ForgotPassWordRequest;
import LIBRO.libro.Payload.Request.LoginRequest;
import LIBRO.libro.Payload.Request.ResetPassWordRequest;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Payload.Response.AuthResponse;
import LIBRO.libro.Service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthConroller {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserDto user) throws UserException {
        AuthResponse authResponse = authService.register(user);

     return ResponseEntity.ok(authResponse);


    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid LoginRequest request) throws UserException {
        AuthResponse authResponse = authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(authResponse);


    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassWord(@RequestBody ForgotPassWordRequest req) throws UserException, MessagingException {
        authService.createResetPassWordToken(req.getEmail());

        ApiResponse apiResponse = new ApiResponse( "A reset link was sent to you account" , true);

        return ResponseEntity.ok(apiResponse);


    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> forgotPassWord(@RequestBody ResetPassWordRequest req) throws Exception {
        authService.resetPassWordT(req.getToken(), req.getPassword());

        ApiResponse apiResponse = new ApiResponse( " PassWord Reset Succesfully " , true);

        return ResponseEntity.ok(apiResponse);


    }


}

