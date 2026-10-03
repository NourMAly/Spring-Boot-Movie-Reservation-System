package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.Reservation;

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
            for (String s : rev.getSeats()) {
                seats.add(s);
            }
        }
        return seats;
    }

    @Override
    public List<Reservation> findAllCustomerReservations(String username) {
        TypedQuery<Reservation> query = entityManager.createQuery("FROM Reservtion WHERE customer.username=:username",
                Reservation.class);
        query.setParameter("username", username);
        return query.getResultList();
    }

}
