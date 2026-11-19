package com.nour.SpringBootMovieReservationSystem.apiservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nour.SpringBootMovieReservationSystem.CustomException.InvalidSeatException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ReservationNotCancellableException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ReservationNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.SeatOccupiedException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ShowtimeNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.UserNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.ReservationDAO;
import com.nour.SpringBootMovieReservationSystem.dao.ShowtimeDAO;
import com.nour.SpringBootMovieReservationSystem.dao.UserDAO;
import com.nour.SpringBootMovieReservationSystem.dto.ReservationRequest;
import com.nour.SpringBootMovieReservationSystem.dto.ReservationResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.entity.Reservation;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;
import com.nour.SpringBootMovieReservationSystem.entity.User;

@ExtendWith(MockitoExtension.class)
public class ApiReservationServiceImplTest {

    @Mock
    private ReservationDAO reservationDAOMock;
    @Mock
    private ShowtimeDAO showtimeDAOMock;
    @Mock
    private UserDAO userDAOMock;

    @InjectMocks
    private ApiReservationServiceImpl apiReservationServiceImpl;

    @Test
    public void isReservationUnique_whenNoSeatsReserved_ReturnsTrue() {
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of());
        boolean result = apiReservationServiceImpl.isReservationUnique(1, List.of("A1", "A2"));
        assertTrue(result);
    }

    @Test
    public void isReservationUnique_whenSeatsAreAvailable_ReturnsTrue() {
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of("A1", "A2"));
        boolean result = apiReservationServiceImpl.isReservationUnique(1, List.of("A3", "A4"));
        assertTrue(result);
    }

    @Test
    public void isReservationUnique_whenRequestedSeatIsReserved_ReturnsFalse() {
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of("A1", "A2"));
        boolean result = apiReservationServiceImpl.isReservationUnique(1, List.of("A1", "A2"));
        assertFalse(result);
    }

    @Test
    public void isReservationUnique_reservedSeat() {
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of("A1", "A2"));
        boolean result = apiReservationServiceImpl.isReservationUnique(1, List.of("A2", "A3"));
        assertFalse(result);
    }

    @Test
    public void addReservation_findShowtimeById_returnsNull() {
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(null);
        assertThrows(ShowtimeNotFoundException.class,
                () -> apiReservationServiceImpl.addReservation(new ReservationRequest(1, List.of("A1", "A2")),
                        "loislane"));
    }

    @Test
    public void addReservation_showtimeDate_returnsOldDate() {
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2025, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(showtime);
        assertThrows(ShowtimeNotFoundException.class,
                () -> apiReservationServiceImpl.addReservation(new ReservationRequest(1, List.of("A1", "A2")),
                        "loislane"));
    }

    @Test
    public void addReservation_reservedSeatNotInHall() {
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        Hall hall = new Hall();
        showtime.setHall(hall);
        hall.setSeats(List.of("A1", "A2", "A3", "A4", "A5", "A6"));
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(showtime);
        assertThrows(InvalidSeatException.class,
                () -> apiReservationServiceImpl.addReservation(new ReservationRequest(1, List.of("A7", "A8")),
                        "loislane"));
        verify(reservationDAOMock, never()).addReservation(any(Reservation.class));
    }

    @Test
    public void addReservation_reservedSeatNotUnique() {
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        Hall hall = new Hall();
        showtime.setHall(hall);
        hall.setSeats(List.of("A1", "A2", "A3", "A4", "A5", "A6"));
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(showtime);
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of("A1", "A2"));
        assertThrows(SeatOccupiedException.class,
                () -> apiReservationServiceImpl.addReservation(new ReservationRequest(1, List.of("A1", "A2")),
                        "loislane"));
        verify(reservationDAOMock, never()).addReservation(any(Reservation.class));
    }

    @Test
    public void addReservation_findUserByUsername_returnsNull() {
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        Hall hall = new Hall();
        showtime.setHall(hall);
        hall.setSeats(List.of("A1", "A2", "A3", "A4", "A5", "A6"));
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(showtime);
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of());
        when(userDAOMock.findUserByUsername("loislane")).thenReturn(null);
        assertThrows(UserNotFoundException.class,
                () -> apiReservationServiceImpl.addReservation(new ReservationRequest(1, List.of("A1", "A2")),
                        "loislane"));
    }

    @Test
    public void addReservation_validRequest() {
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        showtime.setPrice(BigDecimal.valueOf(200));
        showtime.setMovie(movie);
        Hall hall = new Hall();
        showtime.setHall(hall);
        hall.setSeats(List.of("A1", "A2", "A3", "A4", "A5", "A6"));
        User user = new User();
        when(showtimeDAOMock.findShowtimeById(1)).thenReturn(showtime);
        when(reservationDAOMock.findReservedSeatsInShowtime(1)).thenReturn(List.of());
        when(userDAOMock.findUserByUsername("loislane")).thenReturn(user);
        ReservationResponse response = apiReservationServiceImpl
                .addReservation(new ReservationRequest(1, List.of("A1", "A2")), "loislane");
        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationDAOMock, times(2)).addReservation(captor.capture());
        List<Reservation> reservations = captor.getAllValues();

        assertEquals("A1", reservations.get(0).getSeatLabel());
        assertEquals("A2", reservations.get(1).getSeatLabel());

        assertEquals(
                reservations.get(0).getBookingRef(),
                reservations.get(1).getBookingRef());

        assertEquals("Inception", response.movieName());
        assertEquals(150, response.runTimeinMinutes());
        assertEquals(new BigDecimal("400"), response.totalPrice());
        assertEquals(List.of("A1", "A2"), response.seats());
    }

    @Test
    public void allMyReservations_findUserByUsername_returnsNull() {
        when(userDAOMock.findUserByUsername("loislane")).thenReturn(null);
        assertThrows(UserNotFoundException.class,
                () -> apiReservationServiceImpl.allMyreservations("loislane"));
    }

    @Test
    public void allMyreservations_noReservations() {
        User user = new User();
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        showtime.setPrice(BigDecimal.valueOf(200));
        showtime.setMovie(movie);

        when(userDAOMock.findUserByUsername("loislane")).thenReturn(user);
        when(reservationDAOMock.findAllByUsername("loislane")).thenReturn(List.of());

        List<ReservationResponse> responses = apiReservationServiceImpl.allMyreservations("loislane");

        assertTrue(responses.isEmpty());
    }

    @Test
    public void allMyreservations_validRequest() {
        User user = new User();
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        showtime.setPrice(BigDecimal.valueOf(200));
        showtime.setMovie(movie);
        String bRef = UUID.randomUUID().toString();
        Reservation r1 = new Reservation(user, showtime, "A1", bRef, BigDecimal.valueOf(200));
        Reservation r2 = new Reservation(user, showtime, "A2", bRef, BigDecimal.valueOf(200));

        when(userDAOMock.findUserByUsername("loislane")).thenReturn(user);
        when(reservationDAOMock.findAllByUsername("loislane")).thenReturn(List.of(r1, r2));

        List<ReservationResponse> responses = apiReservationServiceImpl.allMyreservations("loislane");

        assertEquals(bRef, responses.get(0).bookingReference());
        assertEquals("Inception", responses.get(0).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 14), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(150, responses.get(0).runTimeinMinutes());
        assertEquals(List.of("A1", "A2"), responses.get(0).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(0).totalPrice());
    }

    @Test
    public void allMyreservations_validRequest_multipleBookingsSameShowtime() {
        User user = new User();
        Showtime showtime1 = new Showtime();
        showtime1.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime1.setStartTime(LocalTime.of(20, 0, 0));

        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        showtime1.setPrice(BigDecimal.valueOf(200));
        showtime1.setMovie(movie);

        String bRef1 = UUID.randomUUID().toString();
        Reservation r1 = new Reservation(user, showtime1, "A1", bRef1, BigDecimal.valueOf(200));
        Reservation r2 = new Reservation(user, showtime1, "A2", bRef1, BigDecimal.valueOf(200));

        String bRef2 = UUID.randomUUID().toString();
        Reservation r3 = new Reservation(user, showtime1, "B1", bRef2, BigDecimal.valueOf(200));
        Reservation r4 = new Reservation(user, showtime1, "B2", bRef2, BigDecimal.valueOf(200));

        when(userDAOMock.findUserByUsername("loislane")).thenReturn(user);
        when(reservationDAOMock.findAllByUsername("loislane")).thenReturn(List.of(r1, r2, r3, r4));

        List<ReservationResponse> responses = apiReservationServiceImpl.allMyreservations("loislane");

        assertEquals(bRef1, responses.get(0).bookingReference());
        assertEquals("Inception", responses.get(0).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 14), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(150, responses.get(0).runTimeinMinutes());
        assertEquals(List.of("A1", "A2"), responses.get(0).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(0).totalPrice());

        assertEquals(bRef2, responses.get(1).bookingReference());
        assertEquals("Inception", responses.get(1).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 14), responses.get(1).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(1).showTime());
        assertEquals(150, responses.get(1).runTimeinMinutes());
        assertEquals(List.of("B1", "B2"), responses.get(1).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(1).totalPrice());
    }

    @Test
    public void allMyreservations_validRequest_multipleBookingsDiffShowtime() {
        User user = new User();
        Showtime showtime1 = new Showtime();
        showtime1.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime1.setStartTime(LocalTime.of(20, 0, 0));

        Showtime showtime2 = new Showtime();
        showtime2.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 20));
        showtime2.setStartTime(LocalTime.of(15, 0, 0));

        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        showtime1.setPrice(BigDecimal.valueOf(200));
        showtime1.setMovie(movie);
        showtime2.setPrice(BigDecimal.valueOf(200));
        showtime2.setMovie(movie);
        String bRef1 = UUID.randomUUID().toString();
        Reservation r1 = new Reservation(user, showtime1, "A1", bRef1, BigDecimal.valueOf(200));
        Reservation r2 = new Reservation(user, showtime1, "A2", bRef1, BigDecimal.valueOf(200));

        String bRef2 = UUID.randomUUID().toString();
        Reservation r3 = new Reservation(user, showtime2, "B1", bRef2, BigDecimal.valueOf(200));
        Reservation r4 = new Reservation(user, showtime2, "B2", bRef2, BigDecimal.valueOf(200));

        when(userDAOMock.findUserByUsername("loislane")).thenReturn(user);
        when(reservationDAOMock.findAllByUsername("loislane")).thenReturn(List.of(r1, r2, r3, r4));

        List<ReservationResponse> responses = apiReservationServiceImpl.allMyreservations("loislane");

        assertEquals(bRef1, responses.get(0).bookingReference());
        assertEquals("Inception", responses.get(0).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 14), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(150, responses.get(0).runTimeinMinutes());
        assertEquals(List.of("A1", "A2"), responses.get(0).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(0).totalPrice());

        assertEquals(bRef2, responses.get(1).bookingReference());
        assertEquals("Inception", responses.get(1).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 20), responses.get(1).showDate());
        assertEquals(LocalTime.of(15, 0, 0), responses.get(1).showTime());
        assertEquals(150, responses.get(1).runTimeinMinutes());
        assertEquals(List.of("B1", "B2"), responses.get(1).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(1).totalPrice());
    }

    @Test
    public void cancelMyReservation_findShowtimeOfBooking_returnsNull() {
        String bRef = UUID.randomUUID().toString();
        when(reservationDAOMock.findShowtimeOfBooking(bRef, "loislane")).thenReturn(Optional.empty());
        assertThrows(ReservationNotFoundException.class,
                () -> apiReservationServiceImpl.cancelMyReservation(bRef, "loislane"));
        verify(reservationDAOMock, never()).deleteByBookingRefAndUsername(bRef, "loislane");
    }

    @Test
    public void cancelMyReservation_showtimeIsOld() {
        String bRef = UUID.randomUUID().toString();
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2025, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        when(reservationDAOMock.findShowtimeOfBooking(bRef, "loislane")).thenReturn(Optional.of(showtime));
        assertThrows(ReservationNotCancellableException.class,
                () -> apiReservationServiceImpl.cancelMyReservation(bRef, "loislane"));
        verify(reservationDAOMock, never()).deleteByBookingRefAndUsername(bRef, "loislane");
    }

    @Test
    public void cancelMyReservation_noDeletion() {
        String bRef = UUID.randomUUID().toString();
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        when(reservationDAOMock.findShowtimeOfBooking(bRef, "loislane")).thenReturn(Optional.of(showtime));
        when(reservationDAOMock.deleteByBookingRefAndUsername(bRef, "loislane")).thenReturn(0);
        ArgumentCaptor<String> bookingRefCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        assertThrows(ReservationNotFoundException.class,
                () -> apiReservationServiceImpl.cancelMyReservation(bRef, "loislane"));
        verify(reservationDAOMock, times(1)).deleteByBookingRefAndUsername(bookingRefCaptor.capture(),
                usernameCaptor.capture());
        String bookingRef = bookingRefCaptor.getValue();
        String username = usernameCaptor.getValue();
        assertEquals(bRef, bookingRef);
        assertEquals("loislane", username);

    }

    @Test
    public void cancelMyReservation_validRequest() {
        String bRef = UUID.randomUUID().toString();
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        when(reservationDAOMock.findShowtimeOfBooking(bRef, "loislane")).thenReturn(Optional.of(showtime));
        when(reservationDAOMock.deleteByBookingRefAndUsername(bRef, "loislane")).thenReturn(1);
        ArgumentCaptor<String> bookingRefCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        apiReservationServiceImpl.cancelMyReservation(bRef, "loislane");
        verify(reservationDAOMock, times(1)).deleteByBookingRefAndUsername(bookingRefCaptor.capture(),
                usernameCaptor.capture());
        String bookingRef = bookingRefCaptor.getValue();
        String username = usernameCaptor.getValue();
        assertEquals(bRef, bookingRef);
        assertEquals("loislane", username);
    }

    @Test
    public void deleteReservationAsEmployee_noReservation() {
        String bRef = UUID.randomUUID().toString();
        when(reservationDAOMock.deleteByBookingRef(bRef)).thenReturn(0);
        ArgumentCaptor<String> bookingRefCaptor = ArgumentCaptor.forClass(String.class);
        assertThrows(ReservationNotFoundException.class,
                () -> apiReservationServiceImpl.deleteReservationAsEmployee(bRef));
        verify(reservationDAOMock, times(1)).deleteByBookingRef(bookingRefCaptor.capture());
        String bookingRef = bookingRefCaptor.getValue();
        assertEquals(bRef, bookingRef);
    }

    @Test
    public void deleteReservationAsEmployee_validRequest() {
        String bRef = UUID.randomUUID().toString();
        when(reservationDAOMock.deleteByBookingRef(bRef)).thenReturn(1);
        ArgumentCaptor<String> bookingRefCaptor = ArgumentCaptor.forClass(String.class);
        apiReservationServiceImpl.deleteReservationAsEmployee(bRef);
        verify(reservationDAOMock, times(1)).deleteByBookingRef(bookingRefCaptor.capture());
        String bookingRef = bookingRefCaptor.getValue();
        assertEquals(bRef, bookingRef);
    }

    @Test
    public void findAllReservationsEmployee_validrequest() {
        User user = new User();
        Showtime showtime = new Showtime();
        showtime.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime.setStartTime(LocalTime.of(20, 0, 0));
        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        showtime.setPrice(BigDecimal.valueOf(200));
        showtime.setMovie(movie);
        String bRef = UUID.randomUUID().toString();
        Reservation r1 = new Reservation(user, showtime, "A1", bRef, BigDecimal.valueOf(200));
        Reservation r2 = new Reservation(user, showtime, "A2", bRef, BigDecimal.valueOf(200));

        when(reservationDAOMock.findAllReservationsEmployee()).thenReturn(List.of(r1, r2));
        List<ReservationResponse> responses = apiReservationServiceImpl.findAllReservationsEmployee();

        assertEquals(bRef, responses.get(0).bookingReference());
        assertEquals("Inception", responses.get(0).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 14), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(150, responses.get(0).runTimeinMinutes());
        assertEquals(List.of("A1", "A2"), responses.get(0).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(0).totalPrice());
    }

    @Test
    public void findAllReservationsEmployee_noReservations() {
        when(reservationDAOMock.findAllReservationsEmployee()).thenReturn(List.of());
        List<ReservationResponse> responses = apiReservationServiceImpl.findAllReservationsEmployee();

        assertTrue(responses.isEmpty());
    }

    @Test
    public void findAllReservationsEmployee_validRequest_multipleBookingsSameShowtime() {
        User user = new User();
        Showtime showtime1 = new Showtime();
        showtime1.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime1.setStartTime(LocalTime.of(20, 0, 0));

        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        showtime1.setPrice(BigDecimal.valueOf(200));
        showtime1.setMovie(movie);

        String bRef1 = UUID.randomUUID().toString();
        Reservation r1 = new Reservation(user, showtime1, "A1", bRef1, BigDecimal.valueOf(200));
        Reservation r2 = new Reservation(user, showtime1, "A2", bRef1, BigDecimal.valueOf(200));

        String bRef2 = UUID.randomUUID().toString();
        Reservation r3 = new Reservation(user, showtime1, "B1", bRef2, BigDecimal.valueOf(200));
        Reservation r4 = new Reservation(user, showtime1, "B2", bRef2, BigDecimal.valueOf(200));

        when(reservationDAOMock.findAllReservationsEmployee()).thenReturn(List.of(r1, r2, r3, r4));

        List<ReservationResponse> responses = apiReservationServiceImpl.findAllReservationsEmployee();

        assertEquals(bRef1, responses.get(0).bookingReference());
        assertEquals("Inception", responses.get(0).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 14), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(150, responses.get(0).runTimeinMinutes());
        assertEquals(List.of("A1", "A2"), responses.get(0).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(0).totalPrice());

        assertEquals(bRef2, responses.get(1).bookingReference());
        assertEquals("Inception", responses.get(1).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 14), responses.get(1).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(1).showTime());
        assertEquals(150, responses.get(1).runTimeinMinutes());
        assertEquals(List.of("B1", "B2"), responses.get(1).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(1).totalPrice());
    }

    @Test
    public void findAllReservationsEmployee_validRequest_multipleBookingsDiffShowtime() {
        User user = new User();
        Showtime showtime1 = new Showtime();
        showtime1.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 14));
        showtime1.setStartTime(LocalTime.of(20, 0, 0));

        Showtime showtime2 = new Showtime();
        showtime2.setShowDate(LocalDate.of(2028, Month.NOVEMBER, 20));
        showtime2.setStartTime(LocalTime.of(15, 0, 0));

        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setRunTimeInMinutes(150);
        showtime1.setPrice(BigDecimal.valueOf(200));
        showtime1.setMovie(movie);
        showtime2.setPrice(BigDecimal.valueOf(200));
        showtime2.setMovie(movie);
        String bRef1 = UUID.randomUUID().toString();
        Reservation r1 = new Reservation(user, showtime1, "A1", bRef1, BigDecimal.valueOf(200));
        Reservation r2 = new Reservation(user, showtime1, "A2", bRef1, BigDecimal.valueOf(200));

        String bRef2 = UUID.randomUUID().toString();
        Reservation r3 = new Reservation(user, showtime2, "B1", bRef2, BigDecimal.valueOf(200));
        Reservation r4 = new Reservation(user, showtime2, "B2", bRef2, BigDecimal.valueOf(200));

        when(reservationDAOMock.findAllReservationsEmployee()).thenReturn(List.of(r1, r2, r3, r4));

        List<ReservationResponse> responses = apiReservationServiceImpl.findAllReservationsEmployee();

        assertEquals(bRef1, responses.get(0).bookingReference());
        assertEquals("Inception", responses.get(0).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 14), responses.get(0).showDate());
        assertEquals(LocalTime.of(20, 0, 0), responses.get(0).showTime());
        assertEquals(150, responses.get(0).runTimeinMinutes());
        assertEquals(List.of("A1", "A2"), responses.get(0).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(0).totalPrice());

        assertEquals(bRef2, responses.get(1).bookingReference());
        assertEquals("Inception", responses.get(1).movieName());
        assertEquals(LocalDate.of(2028, Month.NOVEMBER, 20), responses.get(1).showDate());
        assertEquals(LocalTime.of(15, 0, 0), responses.get(1).showTime());
        assertEquals(150, responses.get(1).runTimeinMinutes());
        assertEquals(List.of("B1", "B2"), responses.get(1).seats());
        assertEquals(BigDecimal.valueOf(400), responses.get(1).totalPrice());
    }
}
