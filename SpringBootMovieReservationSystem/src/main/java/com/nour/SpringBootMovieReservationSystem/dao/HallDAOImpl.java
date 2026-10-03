package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.Hall;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class HallDAOImpl implements HallDAO {

    private EntityManager entityManager;

    public HallDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void addHall(Hall hall) {
        entityManager.persist(hall);
    }

    @Override
    @Transactional
    public void deleteHall(Hall hall) {
        entityManager.remove(hall);
    }

    @Override
    @Transactional
    public Hall editHall(Hall hall) {
        return entityManager.merge(hall);
    }

    @Override
    public List<Hall> findAllHalls() {
        TypedQuery<Hall> query = entityManager.createQuery("FROM Hall", Hall.class);

        return query.getResultList();
    }

    @Override
    public Hall findHallById(int id) {
        return entityManager.find(Hall.class, id);
    }

}
