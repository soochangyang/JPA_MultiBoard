package scyang.mutilboard.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expirationTime;
    private final StringRedisTemplate redisTemplate;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expiration-time}") long expirationTime,
                            StringRedisTemplate redisTemplate) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
        this.redisTemplate = redisTemplate;
    }

    public String createToken(String email, String role){
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.expirationTime);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token){
        //Decrypt token
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        //Get role informatin from claims
        String role = claims.get("role", String.class);

        //Convert to GrantedAuthority object for Spring Security
        //"ROLE_" prefix is usually required for Spring Security to recognize it properly.
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

        //Create Principal to be stored in SecurityContext
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try{
            // token phasing
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            // redis check
            String isLogout = redisTemplate.opsForValue().get(token);
            if( !ObjectUtils.isEmpty(isLogout)){
                log.info("Logout token has been received");
                return false;
            }
            return true;
        } catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("Invalid JWT signature");
        } catch(ExpiredJwtException e){
            log.info("Expired JWT token");
        } catch(UnsupportedJwtException e){
            log.info("Unsupported JWT token");
        } catch(IllegalArgumentException e){
            log.info("JWT token is invalid.");
        }
        return false;
    }

    //Configurate Redis TTL
    public Long getExpiration(String token){
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }
}
