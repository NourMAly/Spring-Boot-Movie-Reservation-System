package com.nour.SpringBootMovieReservationSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.dao.MovieDAO;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;

@Service
public class MovieServiceImpl implements MovieService {
    private MovieDAO movieDAO;

    public MovieServiceImpl(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    @Override
    public Movie addMovie(Movie movie) {
        try {
            movieDAO.addMovie(movie);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return movie;
    }

    @Override
    public void deleteMovie(Movie movie) {
        try {
            movieDAO.deleteMovie(movie);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public Movie editMovie(Movie movie) {
        return movieDAO.editMovie(movie);
    }

    @Override
    public List<Movie> findAllMovies() {
        try {
            return movieDAO.findAllMovies();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Movie findMovieById(int id) {
        try {
            return movieDAO.findMovieById(id);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Movie> findMoviesByName(String name) {
        try {
            return movieDAO.findMoviesByName(name);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

}
