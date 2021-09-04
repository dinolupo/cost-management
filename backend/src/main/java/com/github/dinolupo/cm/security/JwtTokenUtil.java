package com.github.dinolupo.cm.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    //TODO: externalize the secret
    private final String jwtSecret = "zdtlD3JKrtKkZwTTgsNFhqzjqP";
    private final long ACCESS_TOKEN_EXPIRATION = 10 * 60 * 1000; // 10 minutes
    private final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours

    public HashMap<String, String> generateTokens(HttpServletRequest request, User user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());

        var roles = user.getAuthorities().stream().map(
                GrantedAuthority::getAuthority).collect(Collectors.toList());

        var accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles)
                .sign(algorithm);

        var refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        var tokens = new HashMap<String, String>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    public boolean validate(String token) {
        //TODO: to be implemented
        var algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        var verifier = JWT.require(algorithm).build();
        try {
            var decodedJWT = verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            // decoding failed
            return false;
        }
    }

    public UsernamePasswordAuthenticationToken geAuthenticationToken(String token) {
        var decodedJWT = JWT.decode(token);
        var user = decodedJWT.getSubject();
        var roles =  decodedJWT.getClaim("roles").asArray(String.class);
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        var authToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
        return authToken;
    }


//
//    public String getUserId(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject().split(",")[0];
//    }
//
//    public String getUsername(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject().split(",")[1];
//    }
//
//    public Date getExpirationDate(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getExpiration();
//    }
//
//    public boolean validate(String token) {
//        try {
//            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
//            return true;
//        } catch (SignatureException ex) {
//            logger.error("Invalid JWT signature - {}", ex.getMessage());
//        } catch (MalformedJwtException ex) {
//            logger.error("Invalid JWT token - {}", ex.getMessage());
//        } catch (ExpiredJwtException ex) {
//            logger.error("Expired JWT token - {}", ex.getMessage());
//        } catch (UnsupportedJwtException ex) {
//            logger.error("Unsupported JWT token - {}", ex.getMessage());
//        } catch (IllegalArgumentException ex) {
//            logger.error("JWT claims string is empty - {}", ex.getMessage());
//        }
//        return false;
//    }

}