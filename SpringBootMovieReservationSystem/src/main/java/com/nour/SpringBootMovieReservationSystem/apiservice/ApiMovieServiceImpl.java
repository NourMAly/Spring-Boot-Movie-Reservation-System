package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.dao.MovieDAO;
import com.nour.SpringBootMovieReservationSystem.dto.MovieResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;

@Service
public class ApiMovieServiceImpl implements ApiMovieService {

    private MovieDAO movieDAO;

    public ApiMovieServiceImpl(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    @Override
    public List<MovieResponse> findAllMovies() {
        List<Movie> movies = movieDAO.findAllMovies();
        if (movies.isEmpty())
            throw new RuntimeException("No movies found.");
        List<MovieResponse> responses = new ArrayList<>();
        for (Movie m : movies) {
            responses.add(new MovieResponse(m.getId(), m.getMovieName(), m.getGenre(), m.getPosterURL(),
                    m.getDescription(), m.getRunTimeInMinutes()));
        }
        return responses;
    }

    @Override
    public List<MovieResponse> getMovieByName(String name) {
        List<Movie> movies = movieDAO.findMoviesByName(name);
        if (movies.isEmpty())
            throw new RuntimeException("No movies found.");
        List<MovieResponse> responses = new ArrayList<>();
        for (Movie m : movies) {
            responses.add(new MovieResponse(m.getId(), m.getMovieName(), m.getGenre(), m.getPosterURL(),
                    m.getDescription(), m.getRunTimeInMinutes()));
        }
        return responses;
    }

}
