package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.Movie;

public interface MovieDAO {
    public Movie findMovieById(int id);

    public void addMovie(Movie movie);

    public void deleteMovie(Movie movie);

    public Movie editMovie(Movie movie);

    public List<Movie> findAllMovies();

    public List<Movie> findMoviesByName(String name);

}
