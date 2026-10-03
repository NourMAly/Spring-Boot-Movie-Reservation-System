package com.nour.SpringBootMovieReservationSystem.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nour.SpringBootMovieReservationSystem.entity.Hall;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;
import com.nour.SpringBootMovieReservationSystem.service.HallService;
import com.nour.SpringBootMovieReservationSystem.service.MovieService;
import com.nour.SpringBootMovieReservationSystem.service.ShowtimeService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private HallService hallService;

    private MovieService movieService;

    private ShowtimeService showtimeService;

    public AdminController(HallService hallService, MovieService movieService, ShowtimeService showtimeService) {
        this.hallService = hallService;
        this.movieService = movieService;
        this.showtimeService = showtimeService;
    }

    @GetMapping()
    public String showAdminDashboard() {
        return "admin";
    }

    @GetMapping("/halls")
    public String showAdminHallsDashboard(Model model) {
        List<Hall> halls = hallService.findAllHalls();
        model.addAttribute("halls", halls);
        return "admin-halls";
    }

    @GetMapping("/halls/search")
    public String getHallById(@RequestParam String id, Model model) {
        Hall hall = hallService.findHallById(Integer.valueOf(id));
        if (hall == null) {
            model.addAttribute("error", "Hall not found");
            return "admin-halls";
        }
        model.addAttribute("halls", List.of(hall));
        return "admin-halls";
    }

    @GetMapping("/halls/add")
    public String showAddHallForm(Model model) {
        model.addAttribute("hall", new Hall());
        return "add-hall";
    }

    @PostMapping("/halls/add")
    public String addHall(@ModelAttribute Hall hall) {
        hallService.addHall(hall);
        return "redirect:/admin/halls";
    }

    @PostMapping("/halls/edit")
    public String editHallForm(@ModelAttribute("hall") Hall hall) {
        hallService.editHall(hall);
        return "redirect:/admin/halls";
    }

    @PostMapping("/halls/delete/{id}")
    public String deleteHall(@PathVariable int id) {
        Hall hall = hallService.findHallById(id);
        hallService.deleteHall(hall);
        return "redirect:/admin/halls";
    }

    @PostMapping("/halls/edit/{id}")
    public String editHallPage(@PathVariable int id, Model model) {
        Hall hall = hallService.findHallById(Integer.valueOf(id));
        model.addAttribute("hall", hall);
        // hallService.editHall(hall);
        return "edit-hall";
    }

    @GetMapping("/movies")
    public String showAdminMoviesDashboard(Model model) {
        List<Movie> movies = movieService.findAllMovies();
        model.addAttribute("movies", movies);
        return "admin-movies";
    }

    @GetMapping("/movies/search")
    public String getMoviesById(@RequestParam String id, Model model) {
        Movie movie = movieService.findMovieById(Integer.valueOf(id));
        if (movie == null) {
            model.addAttribute("error", "Movie not found");
            return "admin-movies";
        }

        model.addAttribute("movies", List.of(movie));

        return "admin-movies";
    }

    @GetMapping("/movies/add")
    public String showAddMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "add-movie";
    }

    @PostMapping("/movies/add")
    public String addMovie(@ModelAttribute Movie movie) {
        movieService.addMovie(movie);
        return "redirect:/admin/movies";
    }

    @PostMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable int id) {
        Movie movie = movieService.findMovieById(id);
        movieService.deleteMovie(movie);
        return "redirect:/admin/movies";
    }

    @PostMapping("/movies/edit")
    public String editMovieForm(@ModelAttribute("movie") Movie movie) {
        movieService.editMovie(movie);
        return "redirect:/admin/movies";
    }

    @PostMapping("/movies/edit/{id}")
    public String editMoviePage(@PathVariable int id, Model model) {
        Movie movie = movieService.findMovieById(id);
        model.addAttribute("movie", movie);
        return "edit-movie";
    }

    @GetMapping("/showtimes")
    public String showAdminShowtimesDashboard(Model model) {
        List<Showtime> showtimes = showtimeService.findAllShowtimes();
        model.addAttribute("showtimes", showtimes);
        return "admin-showtimes";
    }

    @GetMapping("/showtimes/search")
    public String getShowtimesById(@RequestParam int id, Model model) {
        Showtime showtime = showtimeService.findShowtimeById(id);
        if (showtime == null) {
            model.addAttribute("error", "Showtime not found");
            return "admin-showtimes";
        }

        model.addAttribute("showtimes", List.of(showtime));

        return "admin-showtimes";
    }

    @GetMapping("/showtimes/add")
    public String showAddShowtimeForm(Model model) {
        model.addAttribute("showtime", new Showtime());
        model.addAttribute("halls", hallService.findAllHalls());
        model.addAttribute("movies", movieService.findAllMovies());
        return "add-showtime";
    }

    @PostMapping("/showtimes/add")
    public String addShowtime(@ModelAttribute("showtime") Showtime showtime,
            @RequestParam int hallId,
            @RequestParam int movieId) {
        if (!showtimeService.isHallFullAtSameTime(showtime.getStartTime(), hallId, showtime.getId())) {
            showtime.setHall(hallService.findHallById(hallId));
            showtime.setMovie(movieService.findMovieById(movieId));
            showtimeService.addShowtime(showtime);
            return "redirect:/admin/showtimes";

        }
        return "redirect:/admin/showtimes/add?error=duplicate";
    }

    @PostMapping("/showtimes/delete/{id}")
    public String deleteShowtime(@PathVariable int id) {
        Showtime showtime = showtimeService.findShowtimeById(id);
        showtimeService.deleteShowtime(showtime);
        return "redirect:/admin/showtimes";
    }

    @PostMapping("/showtimes/edit")
    public String editShowtimeForm(@ModelAttribute("showtime") Showtime showtime,
            @RequestParam int hallId,
            @RequestParam int movieId,
            RedirectAttributes redirectAttributes) {
        if (!showtimeService.isHallFullAtSameTime(showtime.getStartTime(), hallId, showtime.getId())) {
            showtime.setHall(hallService.findHallById(hallId));
            showtime.setMovie(movieService.findMovieById(movieId));
            showtimeService.editShowtime(showtime);
            return "redirect:/admin/showtimes";
        }
        redirectAttributes.addAttribute("error", "duplicate");
        return "redirect:/admin/showtimes/edit/" + showtime.getId();
    }

    @GetMapping("/showtimes/edit/{id}")
    public String editShowtimePage(@PathVariable int id, Model model) {
        Showtime showtime = showtimeService.findShowtimeById(id);
        model.addAttribute("showtime", showtime);
        model.addAttribute("halls", hallService.findAllHalls());
        model.addAttribute("movies", movieService.findAllMovies());
        return "edit-showtime";
    }
}
