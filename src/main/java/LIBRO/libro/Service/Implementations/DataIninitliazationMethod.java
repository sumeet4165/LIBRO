package LIBRO.libro.Service.Implementations;


import LIBRO.libro.Domain.UserRole;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Repositeries.UserRepo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor

public class DataIninitliazationMethod implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args){
     initializeAdminUser();
    }

    private void initializeAdminUser(){
        String adminEmail="you@gmail.com";
        String adminPassword="123";

        if(userRepo.findByEmail(adminEmail)==null){
            User newuser=User.builder()
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .fullName("Sumeet")
                    .role(UserRole.ROLE_ADMIN)
                    .build();

            userRepo.save(newuser);


        }

    }

}
