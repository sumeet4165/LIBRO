package LIBRO.libro.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtProvider {

    private  SecretKey Key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public  String generateToken(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities =authentication.getAuthorities();
        String roles=populateAuthorities(authorities);

        return Jwts.builder().issuedAt(new Date()).expiration(new Date(new Date().getTime()+86400000))
                .claim("email" , authentication.getName())
                .claim("authorities" , roles)
                .signWith(Key)
                .compact();
    }


    public  String getEmailFromToken( String jwt) {
        jwt=jwt.substring(7);

        Claims claims = Jwts.parser().verifyWith(Key).build().parseSignedClaims(jwt).getPayload();


        return String.valueOf(claims.get("email"));
    }


    public  String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Set<String> authoritiesSet = new HashSet<>();
        for(GrantedAuthority grantedAuthority : authorities){
            authoritiesSet.add(grantedAuthority.getAuthority());
        }

        return String.join(",", authoritiesSet);

    }
}


