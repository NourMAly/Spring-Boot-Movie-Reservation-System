package com.nour.SpringBootMovieReservationSystem.api;

import org.springframework.web.bind.annotation.RestController;

import com.nour.SpringBootMovieReservationSystem.dto.LoginRequest;
import com.nour.SpringBootMovieReservationSystem.dto.TokenResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @Value("${access.ttl.minutes:15}")
    private long ttlMinutes;

    public AuthController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public TokenResponse loginPage(@RequestBody LoginRequest request) {

        Instant now = Instant.now();
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        System.out.println(authentication.getAuthorities());
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .toList();

        if (roles.isEmpty()) {
            throw new BadCredentialsException("User has no roles assigned");
        }
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES))
                .claim("roles", roles)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), jwtClaimsSet)).getTokenValue();

        return new TokenResponse(token);
    }

}
