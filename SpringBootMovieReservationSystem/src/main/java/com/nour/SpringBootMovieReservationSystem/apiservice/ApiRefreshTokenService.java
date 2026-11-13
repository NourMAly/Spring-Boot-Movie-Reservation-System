package com.nour.SpringBootMovieReservationSystem.apiservice;

import com.nour.SpringBootMovieReservationSystem.apiservice.ApiRefreshTokenServiceImpl.Rotation;

public interface ApiRefreshTokenService {
    public String create(String username);

    public Rotation rotate(String rawToken);

    public void revoke(String rawToken);

    public void revokeAllForUser(String username);

}
