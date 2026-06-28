package LIBRO.libro.Config;

import LIBRO.libro.Domain.AuthProvider;
import LIBRO.libro.Domain.UserRole;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Repositeries.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepo userRepo;
    private final JwtProvider jwtProvider;

    @Value("${app.oauth2.redirect-uri:http://localhost:5173/oauth2/callback}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");
        String picture = oauthUser.getAttribute("picture");

        if (email == null || email.isBlank()) {
            response.sendRedirect(frontendRedirectUri + "?error=missing_email");
            return;
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            user = User.builder()
                    .email(email)
                    .fullName(name != null ? name : email)
                    .role(UserRole.ROLE_USER)
                    .authProvider(AuthProvider.GOOGLE)
                    .googleId(googleId)
                    .profileImage(picture)
                    .lastLogin(LocalDateTime.now())
                    .build();
        } else {
            user.setAuthProvider(AuthProvider.GOOGLE);
            user.setGoogleId(googleId);
            if (name != null && !name.isBlank()) {
                user.setFullName(name);
            }
            if (picture != null && !picture.isBlank()) {
                user.setProfileImage(picture);
            }
            user.setLastLogin(LocalDateTime.now());
            if (user.getRole() == null) {
                user.setRole(UserRole.ROLE_USER);
            }
        }

        user = userRepo.save(user);

        Authentication authForJwt = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authForJwt);

        String jwt = jwtProvider.generateToken(authForJwt);
        String encodedJwt = URLEncoder.encode(jwt, StandardCharsets.UTF_8);
        response.sendRedirect(frontendRedirectUri + "?token=" + encodedJwt);
    }
}
