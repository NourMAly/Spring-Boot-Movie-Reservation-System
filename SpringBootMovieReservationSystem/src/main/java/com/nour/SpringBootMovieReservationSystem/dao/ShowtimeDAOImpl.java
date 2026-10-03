package com.nour.SpringBootMovieReservationSystem.dao;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class ShowtimeDAOImpl implements ShowtimeDAO {
    private EntityManager entityManager;

    public ShowtimeDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void addShowtime(Showtime showtime) {
        entityManager.persist(showtime);
    }

    @Override
    @Transactional
    public void deleteShowtime(Showtime showtime) {
        entityManager.remove(showtime);
    }

    @Override
    @Transactional
    public Showtime editShowtime(Showtime showtime) {
        return entityManager.merge(showtime);
    }

    @Override
    public List<Showtime> findAllShowtimes() {
        TypedQuery<Showtime> query = entityManager.createQuery("FROM Showtime", Showtime.class);
        return query.getResultList();
    }

    @Override
    public Showtime findShowtimeById(int id) {
        return entityManager.find(Showtime.class, id);
    }

    @Override
    public boolean isHallFullAtSameTime(LocalTime startTime, int hallId, int showtimeId) {
        TypedQuery<Showtime> query = entityManager
                .createQuery("FROM Showtime where startTime=:startTime AND hall.id=:hallId AND id<>:showtimeId",
                        Showtime.class);
        query.setParameter("hallId", hallId);
        query.setParameter("startTime", startTime);
        query.setParameter("showtimeId", showtimeId);
        return !query.getResultList().isEmpty();
    }

    @Override
    public List<Showtime> getMovieShowtimes(int movieId) {
        TypedQuery<Showtime> query = entityManager.createQuery("FROM Showtime WHERE movie.id=:movieId", Showtime.class);
        query.setParameter("movieId", movieId);
        return query.getResultList();
    }

}
