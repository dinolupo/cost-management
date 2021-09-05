package com.github.dinolupo.cm.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.dinolupo.cm.security.control.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    @Autowired
    UserService userService;

    //TODO: externalize the secret
    private final String jwtSecret = "zdtlD3JKrtKkZwTTgsNFhqzjqP";
    private final long ACCESS_TOKEN_EXPIRATION = 1 * 60 * 1000; // 10 minutes
    private final long REFRESH_TOKEN_EXPIRATION = 1 * 60 * 1000; // 24 hours
    //private final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours

    public HashMap<String, String> generateJWTAccessToken(HttpServletRequest request, String refreshToken)
            throws UsernameNotFoundException {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        var decodedJWT = JWT.decode(refreshToken);
        var username = decodedJWT.getSubject();

        User user = (User) userService.loadUserByUsername(username);
        String accessToken = generateJWT(ACCESS_TOKEN, request, user);
        var tokens = new HashMap<String, String>();
        tokens.put(ACCESS_TOKEN, accessToken);
        return tokens;
    }

//    public HashMap<String, String> generateJWTAccessToken(HttpServletRequest request, User user) {
//        var accessToken = generateJWT(ACCESS_TOKEN, request, user);
//        var tokens = new HashMap<String, String>();
//        tokens.put(ACCESS_TOKEN, accessToken);
//        return tokens;
//    }

    public HashMap<String, String> generateJWTAccessAndRefreshTokens(HttpServletRequest request, User user) {
        var accessToken = generateJWT(ACCESS_TOKEN, request, user);
        var tokens = new HashMap<String, String>();
        tokens.put(ACCESS_TOKEN, accessToken);
        var refreshToken = generateJWT(REFRESH_TOKEN, request, user);
        tokens.put(REFRESH_TOKEN, refreshToken);
        return tokens;
    }

    private String generateJWT(String type, HttpServletRequest request, User user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        var token = "";
        switch (type) {
            case ACCESS_TOKEN:
                var roles = user.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority).collect(Collectors.toList());
                token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", roles)
                        .sign(algorithm);
                break;
            case REFRESH_TOKEN:
                token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                        .withIssuer(request.getRequestURL().toString())
                        .sign(algorithm);
                break;
            default:
                break;
        }
        return token;
    }

    public boolean validate(String token) {
        var algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        var verifier = JWT.require(algorithm).build();
        try {
            var decodedJWT = verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            // decoding failed
            log.error(exception.getMessage());
            return false;
        }
    }

    // in case of access_token, create authentication based only on token information
    public UsernamePasswordAuthenticationToken geAuthenticationTokenFromAccessToken(String token) {
        var decodedJWT = JWT.decode(token);
        var username = decodedJWT.getSubject();
        if (decodedJWT.getClaim("roles").isNull()) {
            throw new JWTVerificationException("roles claim is missing, maybe this is the refresh token?");
        }
        var roles =  decodedJWT.getClaim("roles").asArray(String.class);
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        return authToken;
    }

    // in case of refresh_token, create authentication reading the user from user service
    public UsernamePasswordAuthenticationToken getAuthenticationTokenFromRefreshToken(String token) {
        var decodedJWT = JWT.decode(token);
        var username = decodedJWT.getSubject();
        User user = (User) userService.loadUserByUsername(username);
        var authToken = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
        return authToken;
    }

}