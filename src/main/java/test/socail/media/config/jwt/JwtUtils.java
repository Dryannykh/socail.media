package test.socail.media.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import test.socail.media.error.TokenError;
import test.socail.media.services.impl.UserDetailsImpl;

import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtils {
    @Autowired
    private Environment env;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + Integer.parseInt(Objects.requireNonNull(env.getProperty("expiration")))))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(env.getProperty("secret")));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            throw new TokenError("Invalid JWT token: {}" + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new TokenError("JWT token is expired: {}" + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new TokenError("JWT token is unsupported: {}" + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new TokenError("JWT claims string is empty: {}" + e.getMessage());
        }
    }
}
