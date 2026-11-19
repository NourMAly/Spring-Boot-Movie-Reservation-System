package com.nour.SpringBootMovieReservationSystem.apiservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nour.SpringBootMovieReservationSystem.CustomException.HallNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.HallDAO;
import com.nour.SpringBootMovieReservationSystem.dto.HallRequest;
import com.nour.SpringBootMovieReservationSystem.dto.HallResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;

@ExtendWith(MockitoExtension.class)
public class ApiHallServiceImplTest {

    @Mock
    private HallDAO hallDAOMock;

    @InjectMocks
    ApiHallServiceImpl apiHallServiceImpl;

    @Test
    public void addHall_validRequest() {
        HallResponse response = apiHallServiceImpl.addHall(new HallRequest(3, List.of("A1", "A2", "A3")));
        verify(hallDAOMock, times(1)).addHall(any(Hall.class));
        assertEquals(3, response.numberOfSeats());
        assertEquals(List.of("A1", "A2", "A3"), response.seats());
    }

    @Test
    public void findAllHalls_validRequest() {
        Hall h1 = new Hall(2, List.of("A1", "A2"));
        Hall h2 = new Hall(4, List.of("A1", "A2", "B1", "B2"));
        when(hallDAOMock.findAllHalls()).thenReturn(List.of(h1, h2));
        List<HallResponse> responses = apiHallServiceImpl.findAllHalls();

        assertEquals(2, responses.get(0).numberOfSeats());
        assertEquals(List.of("A1", "A2"), responses.get(0).seats());
        assertEquals(4, responses.get(1).numberOfSeats());
        assertEquals(List.of("A1", "A2", "B1", "B2"), responses.get(1).seats());
        assertEquals(2, responses.size());
    }

    @Test
    public void findAllHalls_emptyList() {
        when(hallDAOMock.findAllHalls()).thenReturn(List.of());
        List<HallResponse> responses = apiHallServiceImpl.findAllHalls();
        assertTrue(responses.isEmpty());
    }

    @Test
    public void findHallById_validRequest() {
        Hall hall = new Hall(2, List.of("A1", "A2"));
        hall.setId(1);
        when(hallDAOMock.findHallById(1)).thenReturn(hall);
        HallResponse response = apiHallServiceImpl.findHallById(1);
        assertEquals(2, response.numberOfSeats());
        assertEquals(List.of("A1", "A2"), response.seats());
        assertEquals(1, response.id());
    }

    @Test
    public void findHallById_noHall() {
        when(hallDAOMock.findHallById(1)).thenReturn(null);
        assertThrows(HallNotFoundException.class, () -> apiHallServiceImpl.findHallById(1));
    }

    @Test
    public void editHall_validRequest() {
        Hall hall = new Hall(2, List.of("A1", "A2"));
        hall.setId(1);
        when(hallDAOMock.findHallById(1)).thenReturn(hall);
        HallRequest r = new HallRequest(4, List.of("A1", "A2", "B1", "B2"));
        HallResponse response = apiHallServiceImpl.editHall(1, r);
        assertEquals(4, response.numberOfSeats());
        assertEquals(List.of("A1", "A2", "B1", "B2"), response.seats());
        assertEquals(4, hall.getNumberOfSeats());
        assertEquals(List.of("A1", "A2", "B1", "B2"), hall.getSeats());
        assertEquals(1, response.id());
    }

    @Test
    public void editHall_noHall() {
        HallRequest r = new HallRequest(4, List.of("A1", "A2", "B1", "B2"));
        when(hallDAOMock.findHallById(1)).thenReturn(null);
        assertThrows(HallNotFoundException.class, () -> apiHallServiceImpl.editHall(1, r));
    }

    @Test
    public void deleteHall_validRequest() {
        Hall hall = new Hall(2, List.of("A1", "A2"));
        hall.setId(1);
        when(hallDAOMock.findHallById(1)).thenReturn(hall);
        apiHallServiceImpl.deleteHall(1);
        verify(hallDAOMock, times(1)).deleteHall(hall);
    }

    @Test
    public void deleteHall_noHall() {
        when(hallDAOMock.findHallById(1)).thenReturn(null);
        assertThrows(HallNotFoundException.class, () -> apiHallServiceImpl.deleteHall(1));
    }
}
