package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.dto.MovieRequest;
import com.nour.SpringBootMovieReservationSystem.dto.MovieResponse;

public interface ApiMovieService {
    public List<MovieResponse> findAllMovies();

    public MovieResponse findMovieById(int id);

    public List<MovieResponse> getMovieByName(String name);

    public MovieResponse addMovie(MovieRequest movieRequest);

    public MovieResponse editMovie(int id, MovieRequest movieRequest);

    public void deleteMovie(int id);

}
