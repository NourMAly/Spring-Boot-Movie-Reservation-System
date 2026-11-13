package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.CustomException.InvalidRefreshTokenException;
import com.nour.SpringBootMovieReservationSystem.dao.RefreshTokenDAO;
import com.nour.SpringBootMovieReservationSystem.entity.RefreshToken;

import jakarta.transaction.Transactional;

@Service
public class ApiRefreshTokenServiceImpl implements ApiRefreshTokenService {

    public record Rotation(String username, String newRawToken) {
    }

    private RefreshTokenDAO refreshTokenDAO;
    private final SecureRandom random = new SecureRandom();

    @Value("${refresh.ttl.days:7}")
    private long ttlDays;

    public ApiRefreshTokenServiceImpl(RefreshTokenDAO refreshTokenDAO) {
        this.refreshTokenDAO = refreshTokenDAO;
    }

    @Override
    @Transactional
    public String create(String username) {
        return issue(username, UUID.randomUUID().toString());
    }

    @Override
    @Transactional(dontRollbackOn = InvalidRefreshTokenException.class)
    public Rotation rotate(String rawToken) {
        if (rawToken == null || rawToken.isBlank())
            throw new InvalidRefreshTokenException();

        RefreshToken t = refreshTokenDAO.findByHash(hash(rawToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (t.isRevoked()) { // already used → likely stolen
            refreshTokenDAO.revokeFamily(t.getFamilyId());
            throw new InvalidRefreshTokenException();
        }
        if (t.getExpiresAt().isBefore(Instant.now()))
            throw new InvalidRefreshTokenException();

        t.setRevoked(true); // rotation: old token is single-use
        return new Rotation(t.getUsername(), issue(t.getUsername(), t.getFamilyId()));
    }

    @Override
    @Transactional
    public void revoke(String rawToken) { // logout
        if (rawToken == null)
            return;
        refreshTokenDAO.findByHash(hash(rawToken)).ifPresent(t -> refreshTokenDAO.revokeFamily(t.getFamilyId()));
    }

    @Override
    @Transactional
    public void revokeAllForUser(String username) {
        refreshTokenDAO.revokeAllForUser(username);
    }

    private String issue(String username, String familyId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        // encode binary array of 32 bytes to string tobe sent in http cookie
        // binary data may contain values that dont correspond to printable chars so
        // enconding needed
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken t = new RefreshToken();
        t.setTokenHash(hash(raw));
        t.setUsername(username);
        t.setFamilyId(familyId);
        t.setExpiresAt(Instant.now().plus(ttlDays, ChronoUnit.DAYS));
        t.setRevoked(false);
        refreshTokenDAO.save(t);
        return raw; // raw value only ever leaves via the cookie
    }

    private static String hash(String raw) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
