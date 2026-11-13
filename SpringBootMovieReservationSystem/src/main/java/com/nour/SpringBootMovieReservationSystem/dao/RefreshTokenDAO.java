package com.nour.SpringBootMovieReservationSystem.dao;

import java.time.Instant;
import java.util.Optional;

import com.nour.SpringBootMovieReservationSystem.entity.RefreshToken;

public interface RefreshTokenDAO {

    public void save(RefreshToken t);

    public Optional<RefreshToken> findByHash(String hash);

    public void revokeFamily(String familyId);

    public void revokeAllForUser(String username);

    public int deleteExpired(Instant now);

}
