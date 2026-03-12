package LIBRO.libro.Controllers;


import LIBRO.libro.Payload.DTO.UserDto;
import LIBRO.libro.Service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")

public class UserController {


    private final UserService userService;



    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);

    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile() {
        return new ResponseEntity<>(userService.getCurrentUser(), HttpStatus.OK);

    }





}
