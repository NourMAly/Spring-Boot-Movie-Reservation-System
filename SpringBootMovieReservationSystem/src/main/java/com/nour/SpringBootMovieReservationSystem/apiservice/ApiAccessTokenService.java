package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class ApiAccessTokenService {
    private final JwtEncoder jwtEncoder;

    @Value("${access.ttl.minutes:15}")
    private long ttlMinutes;

    public ApiAccessTokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String createAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .toList();

        if (roles.isEmpty()) {
            throw new BadCredentialsException("User has no roles assigned");
        }
        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES))
                .claim("roles", roles)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), jwtClaimsSet)).getTokenValue();
        return token;
    }

}
