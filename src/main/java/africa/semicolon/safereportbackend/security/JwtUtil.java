package africa.semicolon.safereportbackend.security;

import africa.semicolon.safereportbackend.data.models.Agency;
import africa.semicolon.safereportbackend.data.models.ResponderUnit;
import africa.semicolon.safereportbackend.data.models.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {
    private static final String SECRET_KEY = "MY_Very_Long_32_Digit_Secret_Key_Wey_Strong_Like_Die";
    private static final long EXPIRY = 1000 * 60 * 60 * 5;
    private final SecretKey key =new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
    private final MacAlgorithm algorithm = Jwts.SIG.HS256;

    public String generateToken(PrincipalUser user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        List<String> roleNames = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toList());
        claims.put("roles", roleNames);
        return createToken(claims, user.getUsername());
    }

    public String generateResponderToken(ResponderUnit responder){
        PrincipalUser user = new PrincipalUser(
                responder.getId(),
                responder.getUsername(),
                responder.getRoles(),
                responder.getPassword()
        );
        return generateToken(user);
    }
    public String generateResponderToken(Agency agency){
        PrincipalUser user = new PrincipalUser(
                agency.getId(),
                agency.getUsername(),
                agency.getRoles(),
                agency.getPassword()
        );
        return generateToken(user);
    }
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY))
                .signWith(key, algorithm)
                .compact();
    }
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }

}
