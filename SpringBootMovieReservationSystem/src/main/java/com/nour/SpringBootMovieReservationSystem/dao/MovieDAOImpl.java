package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.Movie;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class MovieDAOImpl implements MovieDAO {

    private EntityManager entityManager;

    public MovieDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void addMovie(Movie movie) {
        entityManager.persist(movie);
    }

    @Override
    @Transactional
    public void deleteMovie(Movie movie) {
        entityManager.remove(movie);
    }

    @Override
    @Transactional
    public Movie editMovie(Movie movie) {
        return entityManager.merge(movie);
    }

    @Override
    public Movie findMovieById(int id) {
        return entityManager.find(Movie.class, id);
    }

    @Override
    public List<Movie> findAllMovies() {
        TypedQuery<Movie> query = entityManager.createQuery("FROM Movie", Movie.class);
        return query.getResultList();
    }

    @Override
    public List<Movie> findMoviesByName(String name) {
        TypedQuery<Movie> query = entityManager.createQuery("FROM Movie WHERE movieName LIKE :name", Movie.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

}
