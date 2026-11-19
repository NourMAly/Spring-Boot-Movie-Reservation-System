package com.nour.SpringBootMovieReservationSystem.apiservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nour.SpringBootMovieReservationSystem.CustomException.MovieNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ShowtimeNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.MovieDAO;
import com.nour.SpringBootMovieReservationSystem.dao.ReservationDAO;
import com.nour.SpringBootMovieReservationSystem.dao.ShowtimeDAO;
import com.nour.SpringBootMovieReservationSystem.dto.CustomerMovieResponse;
import com.nour.SpringBootMovieReservationSystem.dto.CustomerShowtimeResponse;
import com.nour.SpringBootMovieReservationSystem.dto.SeatMapResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

@ExtendWith(MockitoExtension.class)
public class ApiMoviesShowtimesServiceImplTest {

    @Mock
    private MovieDAO movieDAOMock;
    @Mock
    private ShowtimeDAO showtimeDAOMock;
    @Mock
    private ReservationDAO reservationDAOMock;

    @InjectMocks
    private ApiMoviesShowtimesServiceImpl apiMoviesShowtimesServiceImpl;

    @Test
    public void getAllMoviesAndShowtimes_validRequest() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        Movie m2 = new Movie("Moana", "Animation", "m.png", "moana live action", 90);
        m2.setId(2);
        when(movieDAOMock.findAllMovies()).thenReturn(List.of(m1, m2));
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        LocalDate today = LocalDate.now();

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
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of(showtime1, showtime2));
        when(showtimeDAOMock.getMovieShowtimes(2)).thenReturn(List.of(showtime1, showtime2));
        List<CustomerMovieResponse> responses = apiMoviesShowtimesServiceImpl.getAllMoviesAndShowtimes();
        verify(movieDAOMock).findAllMovies();
        verify(showtimeDAOMock).getMovieShowtimes(1);
        verify(showtimeDAOMock).getMovieShowtimes(2);

        assertEquals("Inception", responses.get(0).name());
        assertEquals("Fantasy", responses.get(0).genre());
        assertEquals("cobb", responses.get(0).description());
        assertEquals("i.png", responses.get(0).posterURL());
        assertEquals(150, responses.get(0).runtimeInMinutes());
        assertEquals(today.plusDays(2), responses.get(0).showtimes().get(0).showDate());
        assertEquals(showtime1.getStartTime(), responses.get(0).showtimes().get(0).showTime());
        assertEquals(showtime1.getHall().getId(), responses.get(0).showtimes().get(0).hallNumber());
        assertEquals(BigDecimal.valueOf(200), responses.get(0).showtimes().get(0).price());
        assertEquals(today.plusDays(5), responses.get(0).showtimes().get(1).showDate());
        assertEquals(showtime2.getStartTime(), responses.get(0).showtimes().get(1).showTime());
        assertEquals(showtime2.getHall().getId(), responses.get(0).showtimes().get(1).hallNumber());
        assertEquals(BigDecimal.valueOf(300), responses.get(0).showtimes().get(1).price());

        assertEquals("Moana", responses.get(1).name());
        assertEquals("Animation", responses.get(1).genre());
        assertEquals("moana live action", responses.get(1).description());
        assertEquals("m.png", responses.get(1).posterURL());
        assertEquals(90, responses.get(1).runtimeInMinutes());
        assertEquals(today.plusDays(2), responses.get(1).showtimes().get(0).showDate());
        assertEquals(showtime1.getStartTime(), responses.get(1).showtimes().get(0).showTime());
        assertEquals(showtime1.getHall().getId(), responses.get(1).showtimes().get(0).hallNumber());
        assertEquals(BigDecimal.valueOf(200), responses.get(1).showtimes().get(0).price());
        assertEquals(today.plusDays(5), responses.get(1).showtimes().get(1).showDate());
        assertEquals(showtime2.getStartTime(), responses.get(1).showtimes().get(1).showTime());
        assertEquals(showtime2.getHall().getId(), responses.get(1).showtimes().get(1).hallNumber());
        assertEquals(BigDecimal.valueOf(300), responses.get(1).showtimes().get(1).price());

        assertEquals(2, responses.size());
    }

    @Test
    public void getAllMoviesAndShowtimes_noMovies() {
        when(movieDAOMock.findAllMovies()).thenReturn(List.of());
        List<CustomerMovieResponse> responses = apiMoviesShowtimesServiceImpl.getAllMoviesAndShowtimes();
        verify(movieDAOMock).findAllMovies();
        assertTrue(responses.isEmpty());
    }

    @Test
    public void getAllMoviesAndShowtimes_noShowtimes() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        Movie m2 = new Movie("Moana", "Animation", "m.png", "moana live action", 90);
        m2.setId(2);
        when(movieDAOMock.findAllMovies()).thenReturn(List.of(m1, m2));
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of());
        when(showtimeDAOMock.getMovieShowtimes(2)).thenReturn(List.of());
        List<CustomerMovieResponse> responses = apiMoviesShowtimesServiceImpl.getAllMoviesAndShowtimes();
        verify(movieDAOMock).findAllMovies();
        verify(showtimeDAOMock).getMovieShowtimes(1);
        verify(showtimeDAOMock).getMovieShowtimes(2);
        assertTrue(responses.get(0).showtimes().isEmpty());
        assertTrue(responses.get(1).showtimes().isEmpty());
        assertEquals(2, responses.size());

        assertEquals("Inception", responses.get(0).name());
        assertEquals("Fantasy", responses.get(0).genre());
        assertEquals("cobb", responses.get(0).description());
        assertEquals("i.png", responses.get(0).posterURL());
        assertEquals(150, responses.get(0).runtimeInMinutes());

        assertEquals("Moana", responses.get(1).name());
        assertEquals("Animation", responses.get(1).genre());
        assertEquals("moana live action", responses.get(1).description());
        assertEquals("m.png", responses.get(1).posterURL());
        assertEquals(90, responses.get(1).runtimeInMinutes());
    }

    @Test
    public void getAllMovieShowtimes_validRequest() {
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        LocalDate today = LocalDate.now();

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

        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of(showtime1, showtime2));
        List<CustomerShowtimeResponse> responses = apiMoviesShowtimesServiceImpl.getAllMovieShowtimes(1);
        verify(showtimeDAOMock).getMovieShowtimes(1);
        assertEquals(2, responses.size());

        assertEquals(today.plusDays(2), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(1, responses.get(0).hallNumber());
        assertEquals(BigDecimal.valueOf(200), responses.get(0).price());

        assertEquals(today.plusDays(5), responses.get(1).showDate());
        assertEquals(LocalTime.of(15, 0, 0), responses.get(1).showTime());
        assertEquals(1, responses.get(1).hallNumber());
        assertEquals(BigDecimal.valueOf(300), responses.get(1).price());
    }

    @Test
    public void getAllMovieShowtimes_noShowtimes() {
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of());
        List<CustomerShowtimeResponse> responses = apiMoviesShowtimesServiceImpl.getAllMovieShowtimes(1);
        verify(showtimeDAOMock).getMovieShowtimes(1);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void getMovieAndItsShowtimes_validRequest() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        when(movieDAOMock.findMovieById(1)).thenReturn(m1);
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        LocalDate today = LocalDate.now();

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
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of(showtime1, showtime2));

        CustomerMovieResponse response = apiMoviesShowtimesServiceImpl.getMovieAndItsShowtimes(1);
        verify(movieDAOMock).findMovieById(1);
        verify(showtimeDAOMock).getMovieShowtimes(1);

        assertEquals("Inception", response.name());
        assertEquals("Fantasy", response.genre());
        assertEquals("cobb", response.description());
        assertEquals("i.png", response.posterURL());
        assertEquals(150, response.runtimeInMinutes());
        assertEquals(today.plusDays(2), response.showtimes().get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), response.showtimes().get(0).showTime());
        assertEquals(1, response.showtimes().get(0).hallNumber());
        assertEquals(BigDecimal.valueOf(200), response.showtimes().get(0).price());

        assertEquals(today.plusDays(5), response.showtimes().get(1).showDate());
        assertEquals(LocalTime.of(15, 0, 0), response.showtimes().get(1).showTime());
        assertEquals(1, response.showtimes().get(1).hallNumber());
        assertEquals(BigDecimal.valueOf(300), response.showtimes().get(1).price());
    }

    @Test
    public void getMovieAndItsShowtimes_noMovie() {
        when(movieDAOMock.findMovieById(1)).thenReturn(null);
        assertThrows(MovieNotFoundException.class, () -> apiMoviesShowtimesServiceImpl.getMovieAndItsShowtimes(1));
        verify(movieDAOMock).findMovieById(1);
    }

    @Test
    public void getMovieAndItsShowtimes_noShowtimes() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        when(movieDAOMock.findMovieById(1)).thenReturn(m1);
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of());
        CustomerMovieResponse response = apiMoviesShowtimesServiceImpl.getMovieAndItsShowtimes(1);
        verify(movieDAOMock).findMovieById(1);
        verify(showtimeDAOMock).getMovieShowtimes(1);

        assertEquals("Inception", response.name());
        assertEquals("Fantasy", response.genre());
        assertEquals("cobb", response.description());
        assertEquals("i.png", response.posterURL());
        assertEquals(150, response.runtimeInMinutes());
        assertTrue(response.showtimes().isEmpty());
    }

    @Test
    public void getSeatMapForShowtime_validRequest_noReservedSeats() {
        Showtime showtime1 = new Showtime();
        LocalDate today = LocalDate.now();

        showtime1.setShowDate(today.plusDays(2));
        showtime1.setStartTime(LocalTime.of(20, 0, 0));
        Hall hall = new Hall(6, List.of("A1", "A2", "A3", "A4", "A5", "A6"));
        hall.setId(1);
        showtime1.setHall(hall);
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(showtime1);
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of());
        SeatMapResponse response = apiMoviesShowtimesServiceImpl.getSeatMapForShowtime(1);
        verify(showtimeDAOMock).findShowtimeById(1);
        verify(reservationDAOMock).findReservedSeatsInShowtime(1);
        assertEquals(List.of(), response.reservedSeats());
        assertEquals(List.of("A1", "A2", "A3", "A4", "A5", "A6"), response.allTheSeats());
        assertEquals(List.of("A1", "A2", "A3", "A4", "A5", "A6"), response.freeSeats());
    }

    @Test
    public void getSeatMapForShowtime_validRequest_withReservedSeats() {
        Showtime showtime1 = new Showtime();
        LocalDate today = LocalDate.now();

        showtime1.setShowDate(today.plusDays(2));
        showtime1.setStartTime(LocalTime.of(20, 0, 0));
        Hall hall = new Hall(6, List.of("A1", "A2", "A3", "A4", "A5", "A6"));
        hall.setId(1);
        showtime1.setHall(hall);
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(showtime1);
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of("A1", "A2", "A3"));
        SeatMapResponse response = apiMoviesShowtimesServiceImpl.getSeatMapForShowtime(1);
        verify(showtimeDAOMock).findShowtimeById(1);
        verify(reservationDAOMock).findReservedSeatsInShowtime(1);
        assertEquals(List.of("A1", "A2", "A3"), response.reservedSeats());
        assertEquals(List.of("A1", "A2", "A3", "A4", "A5", "A6"), response.allTheSeats());
        assertEquals(List.of("A4", "A5", "A6"), response.freeSeats());
        assertEquals(
                List.of("A1", "A2", "A3", "A4", "A5", "A6"),
                hall.getSeats());
    }

    @Test
    public void getSeatMapForShowtime_noShowtime() {
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(null);
        assertThrows(ShowtimeNotFoundException.class, () -> apiMoviesShowtimesServiceImpl.getSeatMapForShowtime(1));
        verify(showtimeDAOMock).findShowtimeById(1);
    }

    @Test
    public void getAllMoviesAndShowtimesPaginated_validRequest() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        Pageable p = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(
                List.of(m1), p, 1);
        when(movieDAOMock.findAllMoviesPaginated(p)).thenReturn(moviePage);
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        LocalDate today = LocalDate.now();

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
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of(showtime1, showtime2));
        Page<CustomerMovieResponse> response = apiMoviesShowtimesServiceImpl.getAllMoviesAndShowtimesPaginated(p);
        verify(movieDAOMock).findAllMoviesPaginated(p);
        verify(showtimeDAOMock).getMovieShowtimes(1);
        assertEquals(0, response.getNumber());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        CustomerMovieResponse movieResponse = response.getContent().get(0);

        assertEquals("Inception", movieResponse.name());
        assertEquals("Fantasy", movieResponse.genre());
        assertEquals("cobb", movieResponse.description());
        assertEquals("i.png", movieResponse.posterURL());
        assertEquals(150, movieResponse.runtimeInMinutes());
        assertEquals(today.plusDays(2), movieResponse.showtimes().get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), movieResponse.showtimes().get(0).showTime());
        assertEquals(1, movieResponse.showtimes().get(0).hallNumber());
        assertEquals(BigDecimal.valueOf(200), movieResponse.showtimes().get(0).price());

        assertEquals(today.plusDays(5), movieResponse.showtimes().get(1).showDate());
        assertEquals(LocalTime.of(15, 0, 0), movieResponse.showtimes().get(1).showTime());
        assertEquals(1, movieResponse.showtimes().get(1).hallNumber());
        assertEquals(BigDecimal.valueOf(300), movieResponse.showtimes().get(1).price());
    }

    @Test
    public void getAllMoviesAndShowtimesPaginated_emptyPageNoMovies() {
        Pageable p = PageRequest.of(0, 10);
        Page<Movie> moviePage = Page.empty(p);

        when(movieDAOMock.findAllMoviesPaginated(p)).thenReturn(moviePage);

        Page<CustomerMovieResponse> response = apiMoviesShowtimesServiceImpl.getAllMoviesAndShowtimesPaginated(p);

        verify(movieDAOMock).findAllMoviesPaginated(p);
        verify(showtimeDAOMock, times(0)).getMovieShowtimes(1);

        assertTrue(response.isEmpty());
        assertEquals(0, response.getTotalElements());
    }

    @Test
    public void getAllMoviesAndShowtimesPaginated_noShowtimes() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        Pageable p = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(
                List.of(m1), p, 1);
        when(movieDAOMock.findAllMoviesPaginated(p)).thenReturn(moviePage);
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of());
        Page<CustomerMovieResponse> response = apiMoviesShowtimesServiceImpl.getAllMoviesAndShowtimesPaginated(p);
        verify(movieDAOMock).findAllMoviesPaginated(p);
        verify(showtimeDAOMock).getMovieShowtimes(1);
        assertEquals(0, response.getNumber());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        CustomerMovieResponse movieResponse = response.getContent().get(0);

        assertEquals("Inception", movieResponse.name());
        assertEquals("Fantasy", movieResponse.genre());
        assertEquals("cobb", movieResponse.description());
        assertEquals("i.png", movieResponse.posterURL());
        assertEquals(150, movieResponse.runtimeInMinutes());
        assertTrue(movieResponse.showtimes().isEmpty());
    }

    @Test
    public void getAllMovieUpcomingShowtimes_validRequest() {
        Showtime showtime1 = new Showtime();
        Showtime showtime2 = new Showtime();
        LocalDate today = LocalDate.now();

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
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of(showtime1, showtime2));
        List<CustomerShowtimeResponse> responses = apiMoviesShowtimesServiceImpl.getAllMovieUpcomingShowtimes(1);
        verify(showtimeDAOMock).getMovieShowtimes(1);

        assertEquals(today.plusDays(2), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(1, responses.get(0).hallNumber());
        assertEquals(BigDecimal.valueOf(200), responses.get(0).price());
        assertEquals(today.plusDays(5), responses.get(1).showDate());
        assertEquals(LocalTime.of(15, 0, 0), responses.get(1).showTime());
        assertEquals(1, responses.get(1).hallNumber());
        assertEquals(BigDecimal.valueOf(300), responses.get(1).price());

        assertEquals(2, responses.size());
    }

    @Test
    public void getAllMovieUpcomingShowtimes_noShowtimes() {
        when(showtimeDAOMock.getMovieShowtimes(1)).thenReturn(List.of());
        List<CustomerShowtimeResponse> responses = apiMoviesShowtimesServiceImpl.getAllMovieUpcomingShowtimes(1);
        verify(showtimeDAOMock).getMovieShowtimes(1);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void getAllMovieUpcomingShowtimes_filtersPastAndSortsFuture() {
        LocalDate today = LocalDate.now();

        Hall hall = new Hall(6, List.of("A1", "A2"));
        hall.setId(1);

        Showtime pastShowtime = new Showtime();
        pastShowtime.setShowDate(today.minusDays(1));
        pastShowtime.setStartTime(LocalTime.of(20, 0));
        pastShowtime.setPrice(BigDecimal.valueOf(100));

        Showtime laterShowtime = new Showtime();
        laterShowtime.setShowDate(today.plusDays(2));
        laterShowtime.setStartTime(LocalTime.of(20, 0));
        laterShowtime.setPrice(BigDecimal.valueOf(300));

        Showtime earlierShowtime = new Showtime();
        earlierShowtime.setShowDate(today.plusDays(1));
        earlierShowtime.setStartTime(LocalTime.of(15, 0));
        earlierShowtime.setPrice(BigDecimal.valueOf(200));

        pastShowtime.setHall(hall);
        laterShowtime.setHall(hall);
        earlierShowtime.setHall(hall);
        when(showtimeDAOMock.getMovieShowtimes(1))
                .thenReturn(List.of(pastShowtime, laterShowtime, earlierShowtime));

        List<CustomerShowtimeResponse> responses = apiMoviesShowtimesServiceImpl.getAllMovieUpcomingShowtimes(1);
        verify(showtimeDAOMock).getMovieShowtimes(1);
        assertEquals(2, responses.size());

        assertEquals(today.plusDays(1), responses.get(0).showDate());
        assertEquals(BigDecimal.valueOf(200), responses.get(0).price());
        assertEquals(1, responses.get(0).hallNumber());
        assertEquals(1, responses.get(1).hallNumber());
        assertEquals(today.plusDays(2), responses.get(1).showDate());
        assertEquals(BigDecimal.valueOf(300), responses.get(1).price());

    }
}
