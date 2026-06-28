package LIBRO.libro.Service;

import LIBRO.libro.Entities.User;
import LIBRO.libro.Payload.DTO.UserDto;

import java.util.List;

public interface UserService {

   public User getCurrentUser();
   public List<UserDto> getAllUsers();
   public User findById(Long id);


}
