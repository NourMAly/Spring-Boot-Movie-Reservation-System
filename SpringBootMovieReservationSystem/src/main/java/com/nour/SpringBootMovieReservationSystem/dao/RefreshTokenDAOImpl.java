package com.nour.SpringBootMovieReservationSystem.dao;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.RefreshToken;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Repository
public class RefreshTokenDAOImpl implements RefreshTokenDAO {

    private EntityManager entityManager;

    public RefreshTokenDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int deleteExpired(Instant now) {
        return entityManager.createQuery("delete from RefreshToken t where t.expiresAt < :now")
                .setParameter("now", now).executeUpdate();
    }

    @Override
    public Optional<RefreshToken> findByHash(String hash) {

        return entityManager.createQuery("select t from RefreshToken t where t.tokenHash=:h", RefreshToken.class)
                .setParameter("h", hash)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream().findFirst();
    }

    @Override
    public void revokeAllForUser(String username) {
        entityManager.createQuery("update RefreshToken t set t.revoked = true where t.username = :u")
                .setParameter("u", username).executeUpdate();
    }

    @Override
    public void revokeFamily(String familyId) {
        entityManager.createQuery("update RefreshToken t set t.revoked = true where t.familyId = :f")
                .setParameter("f", familyId).executeUpdate();

    }

    @Override
    public void save(RefreshToken t) {
        entityManager.persist(t);
    }

}
