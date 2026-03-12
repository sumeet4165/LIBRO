package LIBRO.libro.Service.Implementations;


import LIBRO.libro.Config.JwtProvider;
import LIBRO.libro.Domain.UserRole;
import LIBRO.libro.Entities.ResetPassWordToken;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Exceptions.UserException;
import LIBRO.libro.Mapper.UserMapper;
import LIBRO.libro.Payload.DTO.UserDto;
import LIBRO.libro.Payload.Response.AuthResponse;
import LIBRO.libro.Repositeries.PassWordResetTokenRepo;
import LIBRO.libro.Repositeries.UserRepo;
import LIBRO.libro.Service.AuthService;
import LIBRO.libro.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceimpl  implements AuthService {


    private final UserRepo userRepo;
    private final JwtProvider jwtProvider;

    private final UserMapper userMapper;
    private final CustomUserDetailsService customUserDetailsService;

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final PassWordResetTokenRepo  passWordResetTokenRepo;
    @Override
    public AuthResponse login(String email, String password) throws UserException {

        Authentication authentication = authenticate(email,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

//        Collection<? extends GrantedAuthority> authorities =authentication.getAuthorities();
//        String role=authorities.iterator().next().getAuthority();


        String jwt= jwtProvider.generateToken(authentication);



        User user=userRepo.findByEmail(email);
        user.setLastLogin(LocalDateTime.now());


        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setTitle("Login Succes");
        authResponse.setMessage(" Welcome back " + email);
        authResponse.setUser(userMapper.toUserDto(user));

        return authResponse;

    }


    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        if(userDetails==null) throw new UserException("User Not found with email : " + email);

        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new UserException("Invalid  password");

        }

        return new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());

    }


    @Override
    public AuthResponse register(UserDto Req) throws UserException {



        User user=userRepo.findByEmail(Req.getEmail());
        if(user!=null){
            throw new UserException("Email already in use");
        }

        User created = new User();
        created.setPassword(passwordEncoder.encode(Req.getPassword()));
        created.setEmail(Req.getEmail());
        created.setPhone(Req.getPhone());
        created.setFullName(Req.getFullName());
        created.setLastLogin(LocalDateTime.now());
        created.setRole(UserRole.ROLE_USER);



        User saved = userRepo.save(created);

//        set securoty contesholder
        Authentication authentication = new UsernamePasswordAuthenticationToken(saved.getEmail(), saved.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);



//        jwt create

        String jwt= jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setTitle("Welcome "+ created.getFullName());
        authResponse.setMessage(" register success ");
        authResponse.setUser(userMapper.toUserDto(saved));


        return authResponse;
    }

    @Transactional
    public void createResetPassWordToken(String email) throws UserException, MessagingException {
        String frontendUrl="";

    User user=userRepo.findByEmail(email);
    if(user==null){
        throw new UserException("Email dont exists ");

    }

    String token= UUID.randomUUID().toString();

    ResetPassWordToken resetPassWordToken = ResetPassWordToken.builder()
            .token(token)
            .user(user)
            .expiryDate(LocalDateTime.now().plusMinutes(5))
            .build();

    passWordResetTokenRepo.save(resetPassWordToken);

    String resetLink=frontendUrl+token;
    String Subject="PassWord Reset Request ";
    String body="you requested to reset you password . Use this link (valid for 5 min) " + resetLink;


//    sent email
        emailService.sendEmail(user.getEmail(),Subject,body);




    }

    @Transactional
    public void resetPassWordT(String token, String newpass) throws Exception {
        ResetPassWordToken passWordResetToken = passWordResetTokenRepo.findByToken(token).orElseThrow(()->
                new Exception("token not valid"));

        if(passWordResetToken.isExpired()){
            passWordResetTokenRepo.delete(passWordResetToken);
            throw new Exception("token has expired");
        }

        User user=passWordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newpass));
        userRepo.save(user);

        passWordResetTokenRepo.delete(passWordResetToken);


    }
}
