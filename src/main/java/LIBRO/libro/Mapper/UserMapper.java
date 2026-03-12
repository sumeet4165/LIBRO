package LIBRO.libro.Mapper;


import LIBRO.libro.Entities.User;
import LIBRO.libro.Payload.DTO.UserDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserMapper {
    public  UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());
        userDto.setPhone(user.getPhone());
        userDto.setRole(user.getRole());
        userDto.setLastLogin(user.getLastLogin());
        return userDto;
    }


    public List<UserDto> toUserDtos(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(toUserDto(user));
        }
        return userDtos;
    }

    public Set<UserDto> toUserDtoSet(Set<User> users) {
        Set<UserDto> userDtos = new HashSet<>();
        for (User user : users) {
            userDtos.add(toUserDto(user));
        }
        return userDtos;
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFullName(userDto.getFullName());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());
        user.setCreatedAt(LocalDateTime.now());

        return user;

    }


}
