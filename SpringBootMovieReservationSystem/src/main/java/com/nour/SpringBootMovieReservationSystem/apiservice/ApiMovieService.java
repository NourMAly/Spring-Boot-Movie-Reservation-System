package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.dto.MovieResponse;

public interface ApiMovieService {
    public List<MovieResponse> findAllMovies();

    public List<MovieResponse> getMovieByName(String name);

}
