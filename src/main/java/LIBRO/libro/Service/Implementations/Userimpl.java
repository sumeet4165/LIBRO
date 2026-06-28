package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Entities.User;
import LIBRO.libro.Exceptions.UserException;
import LIBRO.libro.Mapper.UserMapper;
import LIBRO.libro.Payload.DTO.UserDto;
import LIBRO.libro.Repositeries.UserRepo;
import LIBRO.libro.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor

public class Userimpl implements UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @Override
    public User getCurrentUser() {
//         from securty contest holder we can get
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepo.findByEmail(email);

        if(user==null){
            throw new UserException("User not found");
        }

        return user;

    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users=userRepo.findAll();

        return userMapper.toUserDtos(users);
    }

    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElseThrow(()->new UserException("User not found"));
    }
}
