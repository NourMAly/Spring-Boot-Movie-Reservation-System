package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nour.SpringBootMovieReservationSystem.entity.Movie;

public interface MovieDAO {
    public Movie findMovieById(int id);

    public void addMovie(Movie movie);

    public void deleteMovie(Movie movie);

    public Movie editMovie(Movie movie);

    public List<Movie> findAllMovies();

    public Page<Movie> findAllMoviesPaginated(Pageable pageable);

    public List<Movie> findMoviesByName(String name);

}
