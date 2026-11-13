package com.nour.SpringBootMovieReservationSystem.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nour.SpringBootMovieReservationSystem.CustomException.InvalidRefreshTokenException;
import com.nour.SpringBootMovieReservationSystem.apiservice.ApiAccessTokenService;
import com.nour.SpringBootMovieReservationSystem.apiservice.ApiRefreshTokenService;
import com.nour.SpringBootMovieReservationSystem.apiservice.ApiRefreshTokenServiceImpl.Rotation;
import com.nour.SpringBootMovieReservationSystem.apiservice.ApiUserService;
import com.nour.SpringBootMovieReservationSystem.apiservice.RefreshCookies;
import com.nour.SpringBootMovieReservationSystem.dto.LoginRequest;
import com.nour.SpringBootMovieReservationSystem.dto.RegisterRequest;
import com.nour.SpringBootMovieReservationSystem.dto.TokenResponse;
import com.nour.SpringBootMovieReservationSystem.dto.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final ApiUserService apiUserService;
    private final ApiAccessTokenService accessTokenService;
    private final ApiRefreshTokenService refreshTokenService;
    private final RefreshCookies refreshCookies;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
            ApiUserService apiUserService, ApiAccessTokenService accessTokenService,
            ApiRefreshTokenService refreshTokenService, RefreshCookies refreshCookies) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.apiUserService = apiUserService;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.refreshCookies = refreshCookies;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginPage(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String access = accessTokenService.createAccessToken(authentication.getName(), authentication.getAuthorities());
        String refresh = refreshTokenService.create(authentication.getName());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookies.issue(refresh).toString())
                .body(new TokenResponse(access));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = "refresh", required = false) String rawToken) {

        Rotation rot = refreshTokenService.rotate(rawToken);

        // Fresh DB check: this is what makes disabling a user or changing a role
        // effective
        UserDetails user = userDetailsService.loadUserByUsername(rot.username());
        if (!user.isEnabled()) {
            refreshTokenService.revokeAllForUser(rot.username());
            throw new InvalidRefreshTokenException();
        }

        String access = accessTokenService.createAccessToken(user.getUsername(), user.getAuthorities());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookies.issue(rot.newRawToken()).toString())
                .body(new TokenResponse(access));
        // .toString on Response cookie to create set-cookie header that browser
        // understands
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh", required = false) String rawToken) {
        refreshTokenService.revoke(rawToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshCookies.clear().toString())
                .build();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerCustomer(@Valid @RequestBody RegisterRequest registerRequest) {
        apiUserService.registerUser(registerRequest);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> viewProfile(@AuthenticationPrincipal Jwt jwt) {
        UserResponse response = apiUserService.viewProfile(jwt.getSubject(), jwt.getClaimAsStringList("roles"));
        return ResponseEntity.ok().body(response);
    }

}
