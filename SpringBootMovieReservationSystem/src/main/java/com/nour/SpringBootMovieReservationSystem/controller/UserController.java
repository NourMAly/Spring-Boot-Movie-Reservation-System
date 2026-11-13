package com.nour.SpringBootMovieReservationSystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nour.SpringBootMovieReservationSystem.entity.User;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.entity.Reservation;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;
import com.nour.SpringBootMovieReservationSystem.service.UserService;
import com.nour.SpringBootMovieReservationSystem.service.MovieService;
import com.nour.SpringBootMovieReservationSystem.service.ReservationService;
import com.nour.SpringBootMovieReservationSystem.service.ShowtimeService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private MovieService movieService;
    private ShowtimeService showtimeService;
    private UserService userService;
    private ReservationService reservationService;

    public UserController(MovieService movieService, ShowtimeService showtimeService,
            UserService userService, ReservationService reservationService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
        this.userService = userService;
        this.reservationService = reservationService;
    }

    @GetMapping("/home")
    public String showuserHome() {
        return "user-home";
    }

    @GetMapping("/movies")
    public String showMovies(Model model) {
        model.addAttribute("movies", movieService.findAllMovies());
        return "movies";
    }

    @GetMapping("/movies/search")
    public String showMovieByName(@RequestParam String name, Model model) {
        List<Movie> movies = movieService.findMoviesByName(name);
        if (movies == null) {
            model.addAttribute("error", "Movie not found");
            return "movies";
        }
        model.addAttribute("movies", movies);
        return "movies";
    }

    @GetMapping("/movies/{id}")
    public String showShowtimesPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("movie", movieService.findMovieById(id));
        model.addAttribute("showtimes", showtimeService.getMovieShowtimes(id));
        return "showtimes";
    }

    @GetMapping("/movies/book/{id}/{username}")
    public String showReservationPage(@PathVariable("id") int id,
            @PathVariable("username") String username,
            Model model) {
        Showtime showtime = showtimeService.findShowtimeById(id);
        model.addAttribute("showtime", showtime);
        Movie movie = movieService.findMovieById(showtime.getMovie().getId());
        Hall hall = showtime.getHall();
        List<String> allSeats = hall.getSeats();
        List<String> reservedSeats = reservationService.findReservedSeatsInShowtime(id);
        List<String> availableSeats = new ArrayList<>(allSeats);
        availableSeats.removeAll(reservedSeats);
        User user = userService.findUserByUsername(username);
        model.addAttribute("movie", movie);
        model.addAttribute("user", user);
        model.addAttribute("reservedSeats", reservedSeats);
        return "reserve";
    }

    @PostMapping("/reservations/book")
    public String makeReservation(
            @RequestParam("showtimeId") int showtimeId,
            @RequestParam("seats") List<String> seats,
            Authentication authentication) {

        String userId = authentication.getName();

        User user = userService.findUserByUsername(userId);
        Showtime showtime = showtimeService.findShowtimeById(showtimeId);
        for (String s : seats)
            reservationService.addReservation(new Reservation(user, showtime, s));

        return "redirect:/user/reservations/list";
    }

    @GetMapping("/reservations/list")
    public String getAllReservations(Authentication authentication, Model model) {
        String userId = authentication.getName();

        User user = userService.findUserByUsername(userId);
        model.addAttribute("reservations", user.getReservations());
        return "reservations";
    }

}
