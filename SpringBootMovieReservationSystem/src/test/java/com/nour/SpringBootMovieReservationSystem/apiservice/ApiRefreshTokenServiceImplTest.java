package com.nour.SpringBootMovieReservationSystem.apiservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nour.SpringBootMovieReservationSystem.CustomException.InvalidRefreshTokenException;
import com.nour.SpringBootMovieReservationSystem.dao.RefreshTokenDAO;
import com.nour.SpringBootMovieReservationSystem.entity.RefreshToken;

@ExtendWith(MockitoExtension.class)
public class ApiRefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenDAO refreshTokenDAO;

    @InjectMocks
    private ApiRefreshTokenServiceImpl apiRefreshTokenServiceImpl;

    @Test
    public void rotate_validRequest() {
        RefreshToken refreshToken = new RefreshToken();
        String fId = UUID.randomUUID().toString();
        refreshToken.setFamilyId(fId);
        refreshToken.setUsername("nm");
        refreshToken.setRevoked(false);
        refreshToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        when(refreshTokenDAO.findByHash(anyString())).thenReturn(Optional.of(refreshToken));

        ReflectionTestUtils.setField(apiRefreshTokenServiceImpl, "ttlDays", 7L);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        ApiRefreshTokenServiceImpl.Rotation rotation = apiRefreshTokenServiceImpl.rotate("rawToken");

        verify(refreshTokenDAO).findByHash(anyString());
        verify(refreshTokenDAO).save(captor.capture());

        RefreshToken newToken = captor.getValue();

        // Old token is revoked
        assertTrue(refreshToken.isRevoked());

        // Returned rotation
        assertEquals("nm", rotation.username());
        assertNotNull(rotation.newRawToken());
        assertNotEquals("rawToken", rotation.newRawToken());

        // New token properties
        assertEquals("nm", newToken.getUsername());
        assertEquals(fId, newToken.getFamilyId());
        assertFalse(newToken.isRevoked());

        // Database stores the hash, not the raw token
        assertNotEquals(rotation.newRawToken(), newToken.getTokenHash());

        try {
            assertEquals(
                    HexFormat.of().formatHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(rotation.newRawToken().getBytes(StandardCharsets.UTF_8))),
                    newToken.getTokenHash());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rotate_nullToken() {
        assertThrows(InvalidRefreshTokenException.class, () -> apiRefreshTokenServiceImpl.rotate(null));
        verifyNoInteractions(refreshTokenDAO);
    }

    @Test
    public void rotate_blankToken() {
        assertThrows(InvalidRefreshTokenException.class, () -> apiRefreshTokenServiceImpl.rotate(""));
        verifyNoInteractions(refreshTokenDAO);
    }

    @Test
    public void rotate_noTokenHash() {
        when(refreshTokenDAO.findByHash(anyString())).thenReturn(Optional.empty());
        assertThrows(InvalidRefreshTokenException.class, () -> apiRefreshTokenServiceImpl.rotate("oldtoken"));
        verify(refreshTokenDAO).findByHash(anyString());
    }

    @Test
    public void rotate_revokedToken() {
        RefreshToken refreshToken = new RefreshToken();
        String fId = UUID.randomUUID().toString();
        refreshToken.setFamilyId(fId);
        refreshToken.setUsername("nm");
        refreshToken.setRevoked(true);
        refreshToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        when(refreshTokenDAO.findByHash(anyString())).thenReturn(Optional.of(refreshToken));
        assertThrows(InvalidRefreshTokenException.class, () -> apiRefreshTokenServiceImpl.rotate("oldtoken"));
        verify(refreshTokenDAO).findByHash(anyString());
        verify(refreshTokenDAO).revokeFamily(fId);
    }

    @Test
    public void rotate_expiredToken() {
        RefreshToken refreshToken = new RefreshToken();
        String fId = UUID.randomUUID().toString();
        refreshToken.setFamilyId(fId);
        refreshToken.setUsername("nm");
        refreshToken.setRevoked(false);
        refreshToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
        when(refreshTokenDAO.findByHash(anyString())).thenReturn(Optional.of(refreshToken));
        assertThrows(InvalidRefreshTokenException.class, () -> apiRefreshTokenServiceImpl.rotate("oldtoken"));
        verify(refreshTokenDAO).findByHash(anyString());
        verify(refreshTokenDAO, never()).revokeFamily(anyString());
    }
}
