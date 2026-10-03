package com.nour.SpringBootMovieReservationSystem.service;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.Movie;

public interface MovieService {
    public Movie findMovieById(int id);

    public Movie addMovie(Movie movie);

    public void deleteMovie(Movie movie);

    public Movie editMovie(Movie movie);

    public List<Movie> findAllMovies();

    public List<Movie> findMoviesByName(String name);
}
