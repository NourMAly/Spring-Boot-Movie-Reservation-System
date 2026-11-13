package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.Reservation;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class ReservationDAOImpl implements ReservationDAO {
    private EntityManager entityManager;

    public ReservationDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void addReservation(Reservation reservation) {
        entityManager.persist(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Reservation reservation) {
        entityManager.remove(reservation);
    }

    @Override
    @Transactional
    // Customer: owner-scoped. Returns how many seat rows were removed.
    public int deleteByBookingRefAndUsername(String ref, String username) {
        return entityManager.createQuery(
                "delete from Reservation r where r.bookingRef = :ref and r.user.username = :u")
                .setParameter("ref", ref)
                .setParameter("u", username)
                .executeUpdate();
    }

    // Employee: no owner filter
    @Override
    public int deleteByBookingRef(String ref) {
        return entityManager.createQuery("delete from Reservation r where r.bookingRef = :ref")
                .setParameter("ref", ref)
                .executeUpdate();
    }

    @Override
    public Optional<Showtime> findShowtimeOfBooking(String ref, String username) {
        return entityManager.createQuery(
                "select distinct r.showtime from Reservation r " +
                        "where r.bookingRef = :ref and r.user.username = :u",
                Showtime.class)
                .setParameter("ref", ref)
                .setParameter("u", username)
                .getResultStream().findFirst();
    }

    @Override
    public List<Reservation> findAllReservations() {
        TypedQuery<Reservation> query = entityManager.createQuery("FROM Reservation", Reservation.class);
        return query.getResultList();
    }

    @Override
    public Reservation findReservationById(int id) {
        return entityManager.find(Reservation.class, id);
    }

    @Override
    public List<String> findReservedSeatsInShowtime(int id) {
        TypedQuery<Reservation> query = entityManager.createQuery("FROM Reservation WHERE showtime.id=:id",
                Reservation.class);
        query.setParameter("id", id);

        List<String> seats = new ArrayList<>();
        for (Reservation rev : query.getResultList()) {
            seats.add(rev.getSeatLabel());
        }
        return seats;
    }

    @Override
    public List<Reservation> findAllUserReservations(String username) {
        TypedQuery<Reservation> query = entityManager.createQuery("FROM Reservation WHERE user.username=:username",
                Reservation.class);
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    public List<Reservation> findAllByUsername(String username) {
        return entityManager.createQuery(
                "select r from Reservation r " +
                        "join fetch r.showtime s " +
                        "join fetch s.movie " +
                        "where r.user.username = :u " +
                        "order by s.showDate, s.startTime, r.bookingRef, r.seatLabel",
                Reservation.class)
                .setParameter("u", username)
                .getResultList();
    }

    @Override
    public List<Reservation> findAllReservationsEmployee() {
        return entityManager.createQuery(
                "select r from Reservation r " +
                        "join fetch r.showtime s " +
                        "join fetch s.movie " +
                        "order by s.showDate, s.startTime, r.bookingRef, r.seatLabel",
                Reservation.class)
                .getResultList();
    }

    @Override
    public List<String> getAllUserShowtimeSeats(int userId, int showtimeId) {
        TypedQuery<String> query = entityManager.createQuery(
                "SELECT r.seatLabel FROM Reservation r WHERE r.user.id=:userId AND r.showtime.id=:showtimeId",
                String.class);
        query.setParameter("userId", userId);
        query.setParameter("showtimeId", showtimeId);
        return query.getResultList();
    }

    @Override
    public List<Integer> getUserShowtimes(int id) {
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT DISTINCT r.showtime.id FROM Reservation r WHERE r.user.id=:id",
                Integer.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

}
