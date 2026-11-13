package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshCookies {
    @Value("${cookie.secure:true}")
    private boolean secure;
    @Value("${refresh.ttl.days:7}")
    private long ttlDays;

    public ResponseCookie issue(String value) {
        return build(value, Duration.ofDays(ttlDays));
    }

    public ResponseCookie clear() {
        return build("", Duration.ZERO);
    }

    private ResponseCookie build(String value, Duration maxAge) {
        return ResponseCookie.from("refresh", value)
                .httpOnly(true) // JavaScript can't read it
                .secure(secure) // HTTPS only in production
                .sameSite("Strict") // not sent on cross-site requests
                .path("/api/v1/auth") // only sent to the auth endpoints
                .maxAge(maxAge)
                .build();
    }
}
