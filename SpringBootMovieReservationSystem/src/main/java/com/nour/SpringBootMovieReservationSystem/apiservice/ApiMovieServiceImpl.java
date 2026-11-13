package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.CustomException.MovieNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.MovieDAO;
import com.nour.SpringBootMovieReservationSystem.dto.MovieRequest;
import com.nour.SpringBootMovieReservationSystem.dto.MovieResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;

import jakarta.transaction.Transactional;

@Service
public class ApiMovieServiceImpl implements ApiMovieService {

    private MovieDAO movieDAO;

    public ApiMovieServiceImpl(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    @Override
    public List<MovieResponse> findAllMovies() {
        List<Movie> movies = movieDAO.findAllMovies();
        List<MovieResponse> responses = new ArrayList<>();
        if (!movies.isEmpty()) {
            for (Movie m : movies) {
                responses.add(new MovieResponse(m.getId(), m.getMovieName(), m.getGenre(), m.getPosterURL(),
                        m.getDescription(), m.getRunTimeInMinutes()));
            }
        }
        return responses;
    }

    @Override
    public MovieResponse findMovieById(int id) {
        Movie movie = movieDAO.findMovieById(id);
        if (movie == null)
            throw new MovieNotFoundException("No movie found with id: " + id);
        return new MovieResponse(movie.getId(), movie.getMovieName(), movie.getGenre(), movie.getPosterURL(),
                movie.getDescription(), movie.getRunTimeInMinutes());
    }

    @Override
    public List<MovieResponse> getMovieByName(String name) {
        List<Movie> movies = movieDAO.findMoviesByName(name);
        List<MovieResponse> responses = new ArrayList<>();
        if (!movies.isEmpty()) {
            for (Movie m : movies) {
                responses.add(new MovieResponse(m.getId(), m.getMovieName(), m.getGenre(), m.getPosterURL(),
                        m.getDescription(), m.getRunTimeInMinutes()));
            }
        }
        return responses;
    }

    @Override
    public MovieResponse addMovie(MovieRequest movieRequest) {
        Movie movie = new Movie(movieRequest.name(), movieRequest.genre(), movieRequest.posterFileName(),
                movieRequest.description(), movieRequest.runtimeInMinutes());
        movieDAO.addMovie(movie);
        return new MovieResponse(movie.getId(), movie.getMovieName(), movie.getGenre(), movie.getPosterURL(),
                movie.getDescription(), movie.getRunTimeInMinutes());
    }

    @Override
    @Transactional
    public MovieResponse editMovie(int id, MovieRequest movieRequest) {
        Movie movie = movieDAO.findMovieById(id);
        if (movie == null)
            throw new MovieNotFoundException("No movie found with id: " + id);
        movie.setMovieName(movieRequest.name());
        movie.setGenre(movieRequest.genre());
        movie.setPosterURL(movieRequest.posterFileName());
        movie.setDescription(movieRequest.description());
        movie.setRunTimeInMinutes(movieRequest.runtimeInMinutes());
        return new MovieResponse(movie.getId(), movie.getMovieName(), movie.getGenre(), movie.getPosterURL(),
                movie.getDescription(), movie.getRunTimeInMinutes());
    }

    @Override
    @Transactional
    public void deleteMovie(int id) {
        Movie movie = movieDAO.findMovieById(id);
        if (movie == null)
            throw new MovieNotFoundException("No movie found with id: " + id);
        movieDAO.deleteMovie(movie);
    }

}
