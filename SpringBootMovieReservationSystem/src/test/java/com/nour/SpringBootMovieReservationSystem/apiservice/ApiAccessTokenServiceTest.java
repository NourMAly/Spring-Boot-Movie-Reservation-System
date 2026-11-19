package com.nour.SpringBootMovieReservationSystem.apiservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@ExtendWith(MockitoExtension.class)
public class ApiAccessTokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private ApiAccessTokenService apiAccessTokenService;

    @Test
    public void createAccessToken_validRequest() {
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"),
                new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("generated-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        String token = apiAccessTokenService.createAccessToken("nm", authorities);
        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());
        JwtClaimsSet claims = captor.getValue().getClaims();
        assertEquals("nm", claims.getSubject());
        assertEquals(List.of("CUSTOMER", "EMPLOYEE"), claims.getClaim("roles"));
        assertEquals("generated-token", token);
    }

    @Test
    public void createAccessToken_incorrectRolePrefix() {
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLECUSTOMER"));
        assertThrows(BadCredentialsException.class, () -> apiAccessTokenService.createAccessToken("nm", authorities));
        verifyNoInteractions(jwtEncoder);
    }

    @Test
    public void createAccessToken_mixedAuthorities() {
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_CUSTOMER"),
                new SimpleGrantedAuthority("ROLE_EMPLOYEE"),
                new SimpleGrantedAuthority("READ_PRIVILEGE"));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("generated-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        apiAccessTokenService.createAccessToken("nm", authorities);

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());

        assertEquals(List.of("CUSTOMER", "EMPLOYEE"),
                captor.getValue().getClaims().getClaim("roles"));
    }
}
