package com.nour.SpringBootMovieReservationSystem.apiservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nour.SpringBootMovieReservationSystem.CustomException.HallAlreadyOccupiedException;
import com.nour.SpringBootMovieReservationSystem.CustomException.HallNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.MovieNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ShowtimeNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.HallDAO;
import com.nour.SpringBootMovieReservationSystem.dao.MovieDAO;
import com.nour.SpringBootMovieReservationSystem.dao.ShowtimeDAO;
import com.nour.SpringBootMovieReservationSystem.dto.ShowtimeRequest;
import com.nour.SpringBootMovieReservationSystem.dto.ShowtimeResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

@ExtendWith(MockitoExtension.class)
public class ApiShowtimeServiceImplTest {

    @Mock
    private ShowtimeDAO showtimeDAO;
    @Mock
    private HallDAO hallDAO;
    @Mock
    private MovieDAO movieDAO;

    @InjectMocks
    private ApiShowtimeServiceImpl apiShowtimeServiceImpl;

    @Test
    public void getAllShowtimes_validRequest() {
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        LocalDate today = LocalDate.now();
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        showtime1.setMovie(m1);
        showtime2.setMovie(m1);
        showtime1.setId(1);
        showtime2.setId(2);
        showtime1.setShowDate(today.plusDays(2));
        showtime2.setShowDate(today.plusDays(5));
        showtime1.setStartTime(LocalTime.of(20, 0, 0));
        showtime2.setStartTime(LocalTime.of(15, 0, 0));

        Hall hall = new Hall(6, List.of("A1", "A2", "A3", "A4", "A5", "A6"));
        hall.setId(1);
        showtime1.setHall(hall);
        showtime2.setHall(hall);
        showtime1.setPrice(BigDecimal.valueOf(200));
        showtime2.setPrice(BigDecimal.valueOf(300));
        when(showtimeDAO.findAllShowtimes()).thenReturn(List.of(showtime1, showtime2));
        List<ShowtimeResponse> responses = apiShowtimeServiceImpl.getAllShowtimes();
        verify(showtimeDAO).findAllShowtimes();

        assertEquals(2, responses.size());

        assertEquals(1, responses.get(0).id());
        assertEquals(1, responses.get(0).hallNumber());
        assertEquals(1, responses.get(0).movieID());
        assertEquals(today.plusDays(2), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(BigDecimal.valueOf(200), responses.get(0).price());

        assertEquals(2, responses.get(1).id());
        assertEquals(1, responses.get(1).hallNumber());
        assertEquals(1, responses.get(1).movieID());
        assertEquals(today.plusDays(5), responses.get(1).showDate());
        assertEquals(LocalTime.of(15, 0, 0), responses.get(1).showTime());
        assertEquals(BigDecimal.valueOf(300), responses.get(1).price());
    }

    @Test
    public void getAllShowtimes_noShowtimes() {
        when(showtimeDAO.findAllShowtimes()).thenReturn(List.of());
        List<ShowtimeResponse> responses = apiShowtimeServiceImpl.getAllShowtimes();
        verify(showtimeDAO).findAllShowtimes();
        assertTrue(responses.isEmpty());
    }

    @Test
    public void getShowtimeById_validRequest() {
        Showtime showtime1 = new Showtime();
        LocalDate today = LocalDate.now();
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        showtime1.setMovie(m1);
        showtime1.setId(1);
        showtime1.setShowDate(today.plusDays(2));
        showtime1.setStartTime(LocalTime.of(20, 0, 0));
        Hall hall = new Hall(6, List.of("A1", "A2", "A3", "A4", "A5", "A6"));
        hall.setId(1);
        showtime1.setHall(hall);
        showtime1.setPrice(BigDecimal.valueOf(200));
        when(showtimeDAO.findShowtimeById(1)).thenReturn(showtime1);
        ShowtimeResponse response = apiShowtimeServiceImpl.getShowtimeById(1);
        verify(showtimeDAO).findShowtimeById(1);

        assertEquals(1, response.id());
        assertEquals(1, response.hallNumber());
        assertEquals(1, response.movieID());
        assertEquals(today.plusDays(2), response.showDate());
        assertEquals(LocalTime.of(20, 0, 0), response.showTime());
        assertEquals(BigDecimal.valueOf(200), response.price());
    }

    @Test
    public void getShowtimeById_noShowtime() {
        when(showtimeDAO.findShowtimeById(1)).thenReturn(null);
        assertThrows(ShowtimeNotFoundException.class, () -> apiShowtimeServiceImpl.getShowtimeById(1));
        verify(showtimeDAO).findShowtimeById(1);
    }

    @Test
    public void addShowtime_validRequest() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(20, 0, 0);
        ShowtimeRequest request = new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200));
        Hall hall = new Hall();
        hall.setId(1);
        Movie movie = new Movie();
        movie.setId(1);
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        showtime1.setMovie(movie);
        showtime2.setMovie(movie);
        showtime1.setId(1);
        showtime2.setId(2);
        showtime1.setShowDate(date);
        showtime2.setShowDate(today.plusDays(5));
        showtime1.setStartTime(LocalTime.of(14, 0, 0));
        showtime2.setStartTime(LocalTime.of(15, 0, 0));
        when(hallDAO.findHallById(1)).thenReturn(hall);
        when(movieDAO.findMovieById(1)).thenReturn(movie);
        when(showtimeDAO.hallShowtimes(1)).thenReturn(List.of(showtime1, showtime2));
        doAnswer(invocation -> {
            Showtime savedShowtime = invocation.getArgument(0);
            savedShowtime.setId(3);
            return null;
        }).when(showtimeDAO).addShowtime(any(Showtime.class));
        ShowtimeResponse response = apiShowtimeServiceImpl.addShowtime(request);
        ArgumentCaptor<Showtime> captor = ArgumentCaptor.forClass(Showtime.class);
        verify(showtimeDAO).addShowtime(captor.capture());

        Showtime saved = captor.getValue();

        assertEquals(3, saved.getId());
        assertEquals(date, saved.getShowDate());
        assertEquals(time, saved.getStartTime());
        assertEquals(1, saved.getHall().getId());
        assertEquals(1, saved.getMovie().getId());
        assertEquals(BigDecimal.valueOf(200), saved.getPrice());

        assertEquals(3, response.id());
        assertEquals(date, response.showDate());
        assertEquals(time, response.showTime());
        assertEquals(1, response.hallNumber());
        assertEquals(1, response.movieID());
        assertEquals(BigDecimal.valueOf(200), response.price());
    }

    @Test
    public void addShowtime_noHall() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(20, 0, 0);
        when(hallDAO.findHallById(1)).thenReturn(null);
        assertThrows(HallNotFoundException.class, () -> apiShowtimeServiceImpl
                .addShowtime(new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200))));
        verify(hallDAO).findHallById(1);
    }

    @Test
    public void addShowtime_noMovie() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(20, 0, 0);
        ShowtimeRequest request = new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200));
        Hall hall = new Hall();
        hall.setId(1);
        when(hallDAO.findHallById(1)).thenReturn(hall);
        when(movieDAO.findMovieById(1)).thenReturn(null);
        assertThrows(MovieNotFoundException.class, () -> apiShowtimeServiceImpl
                .addShowtime(request));
        verify(hallDAO).findHallById(1);
        verify(movieDAO).findMovieById(1);
    }

    @Test
    public void addShowtime_overlappingRequest() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(20, 0, 0);
        ShowtimeRequest request = new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200));
        Hall hall = new Hall();
        hall.setId(1);
        Movie movie = new Movie();
        movie.setId(1);
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        showtime1.setMovie(movie);
        showtime2.setMovie(movie);
        showtime1.setId(1);
        showtime2.setId(2);
        showtime1.setShowDate(date);
        showtime2.setShowDate(today.plusDays(5));
        showtime1.setStartTime(LocalTime.of(19, 0, 0));
        showtime2.setStartTime(LocalTime.of(15, 0, 0));
        when(hallDAO.findHallById(1)).thenReturn(hall);
        when(movieDAO.findMovieById(1)).thenReturn(movie);
        when(showtimeDAO.hallShowtimes(1)).thenReturn(List.of(showtime1, showtime2));
        assertThrows(HallAlreadyOccupiedException.class, () -> apiShowtimeServiceImpl.addShowtime(request));
        verify(showtimeDAO).hallShowtimes(1);
        verify(movieDAO).findMovieById(1);
        verify(hallDAO).findHallById(1);
    }

    @Test
    public void editShowtime_validRequest() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(20, 0, 0);
        ShowtimeRequest request = new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200));
        Hall hall = new Hall();
        hall.setId(1);
        Movie movie = new Movie();
        movie.setId(1);
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        showtime1.setMovie(movie);
        showtime2.setMovie(movie);
        showtime1.setId(1);
        showtime2.setId(2);
        showtime1.setShowDate(date);
        showtime2.setShowDate(today.plusDays(5));
        showtime1.setStartTime(LocalTime.of(14, 0, 0));
        showtime2.setStartTime(LocalTime.of(15, 0, 0));
        when(hallDAO.findHallById(1)).thenReturn(hall);
        when(movieDAO.findMovieById(1)).thenReturn(movie);
        when(showtimeDAO.findShowtimeById(1)).thenReturn(showtime1);
        when(showtimeDAO.hallShowtimes(1)).thenReturn(List.of(showtime2));

        ShowtimeResponse response = apiShowtimeServiceImpl.editShowtime(1, request);
        verify(showtimeDAO).hallShowtimes(1);
        verify(movieDAO).findMovieById(1);
        verify(hallDAO).findHallById(1);
        verify(showtimeDAO).findShowtimeById(1);

        assertEquals(1, showtime1.getId());
        assertEquals(date, showtime1.getShowDate());
        assertEquals(time, showtime1.getStartTime());
        assertEquals(1, showtime1.getHall().getId());
        assertEquals(1, showtime1.getMovie().getId());
        assertEquals(BigDecimal.valueOf(200), showtime1.getPrice());

        assertEquals(1, response.id());
        assertEquals(date, response.showDate());
        assertEquals(time, response.showTime());
        assertEquals(1, response.hallNumber());
        assertEquals(1, response.movieID());
        assertEquals(BigDecimal.valueOf(200), response.price());
    }

    @Test
    public void editShowtime_noShowtime() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(20, 0, 0);
        ShowtimeRequest request = new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200));
        when(showtimeDAO.findShowtimeById(1)).thenReturn(null);
        assertThrows(ShowtimeNotFoundException.class, () -> apiShowtimeServiceImpl.editShowtime(1, request));
        verify(showtimeDAO).findShowtimeById(1);
    }

    @Test
    public void editShowtime_noHall() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(20, 0, 0);
        ShowtimeRequest request = new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200));
        Showtime showtime1 = new Showtime();
        showtime1.setId(1);
        showtime1.setShowDate(date);
        showtime1.setStartTime(LocalTime.of(14, 0, 0));
        when(showtimeDAO.findShowtimeById(1)).thenReturn(showtime1);
        assertThrows(HallNotFoundException.class, () -> apiShowtimeServiceImpl.editShowtime(1, request));
        verify(showtimeDAO).findShowtimeById(1);
        verify(hallDAO).findHallById(1);
    }

    @Test
    public void editShowtime_noMovie() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(20, 0, 0);
        ShowtimeRequest request = new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200));
        Hall hall = new Hall();
        hall.setId(1);
        Showtime showtime1 = new Showtime();
        showtime1.setId(1);
        showtime1.setShowDate(date);
        showtime1.setStartTime(LocalTime.of(14, 0, 0));
        when(hallDAO.findHallById(1)).thenReturn(hall);
        when(showtimeDAO.findShowtimeById(1)).thenReturn(showtime1);
        assertThrows(MovieNotFoundException.class, () -> apiShowtimeServiceImpl.editShowtime(1, request));
        verify(showtimeDAO).findShowtimeById(1);
        verify(hallDAO).findHallById(1);
        verify(movieDAO).findMovieById(1);
    }

    @Test
    public void editShowtime_overlappingRequest() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(2);
        LocalTime time = LocalTime.of(19, 0, 0);
        ShowtimeRequest request = new ShowtimeRequest(1, 1, LocalDateTime.of(date, time), BigDecimal.valueOf(200));
        Hall hall = new Hall();
        hall.setId(1);
        Movie movie = new Movie();
        movie.setId(1);
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        showtime1.setMovie(movie);
        showtime2.setMovie(movie);
        showtime1.setId(1);
        showtime2.setId(2);
        showtime1.setShowDate(date);
        showtime2.setShowDate(today.plusDays(2));
        showtime1.setStartTime(LocalTime.of(14, 0, 0));
        showtime2.setStartTime(LocalTime.of(20, 0, 0));
        when(hallDAO.findHallById(1)).thenReturn(hall);
        when(movieDAO.findMovieById(1)).thenReturn(movie);
        when(showtimeDAO.findShowtimeById(1)).thenReturn(showtime1);
        when(showtimeDAO.hallShowtimes(1)).thenReturn(List.of(showtime1, showtime2));
        assertThrows(HallAlreadyOccupiedException.class, () -> apiShowtimeServiceImpl.editShowtime(1, request));
        verify(showtimeDAO).findShowtimeById(1);
        verify(hallDAO).findHallById(1);
        verify(movieDAO).findMovieById(1);
        verify(showtimeDAO).hallShowtimes(1);
    }

    @Test
    public void deleteShowtime_validRequest() {
        LocalDate today = LocalDate.now();
        Showtime showtime1 = new Showtime();
        showtime1.setId(1);
        showtime1.setShowDate(today.plusDays(2));
        showtime1.setStartTime(LocalTime.of(14, 0, 0));
        when(showtimeDAO.findShowtimeById(1)).thenReturn(showtime1);
        apiShowtimeServiceImpl.deleteShowtime(1);
        verify(showtimeDAO).findShowtimeById(1);
        verify(showtimeDAO).deleteShowtime(showtime1);
    }

    @Test
    public void deleteShowtime_noShowtime() {
        when(showtimeDAO.findShowtimeById(1)).thenReturn(null);
        assertThrows(ShowtimeNotFoundException.class, () -> apiShowtimeServiceImpl.deleteShowtime(1));
        verify(showtimeDAO).findShowtimeById(1);
    }

}
