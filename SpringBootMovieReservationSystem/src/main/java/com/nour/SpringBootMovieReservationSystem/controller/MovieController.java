package com.nour.SpringBootMovieReservationSystem.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MovieController {
    private MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movies")
    public String getAllMovies(Model model) {
        List<Movie> movies = movieService.findAllMovies();
        model.addAttribute("movies", movies);
        return "movies";
    }

    @GetMapping("/movies/search")
    public String getMoviesByName(@RequestParam String name, Model model) {
        List<Movie> movies = movieService.findMoviesByName(name);
        if (movies == null) {
            model.addAttribute("error", "Movie not found");
            return "movies";
        }

        model.addAttribute("movies", movies);

        return "movies";
    }
}
