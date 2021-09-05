package com.github.dinolupo.cm.security.boundary;

import com.github.dinolupo.cm.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.ObjectUtils.isEmpty;

@RestController
public class RefreshTokenController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("/refreshtoken")
    ResponseEntity<?> getRefreshedToken(HttpServletRequest request) {
        final String header = request.getHeader(AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            throw new AuthenticationServiceException("Authorization Bearer header not present");
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        if (!jwtTokenUtil.validate(token)) {
            throw new AuthenticationServiceException("Authorization Bearer token is not valid");
        }

        HashMap<String, String> tokens = jwtTokenUtil.generateJWTAccessToken(request, token);
        return ResponseEntity.ok(tokens);

    }

}
