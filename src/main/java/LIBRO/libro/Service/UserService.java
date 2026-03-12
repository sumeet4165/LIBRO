package LIBRO.libro.Service;

import LIBRO.libro.Payload.DTO.UserDto;

import java.util.List;

public interface UserService {

   public  UserDto  getCurrentUser();
   public List<UserDto> getAllUsers();


}
