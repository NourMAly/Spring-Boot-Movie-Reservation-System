package com.nour.SpringBootMovieReservationSystem.apiservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nour.SpringBootMovieReservationSystem.CustomException.MovieNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.MovieDAO;
import com.nour.SpringBootMovieReservationSystem.dto.MovieRequest;
import com.nour.SpringBootMovieReservationSystem.dto.MovieResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;

@ExtendWith(MockitoExtension.class)
public class ApiMovieServiceImplTest {

    @Mock
    private MovieDAO movieDAOMock;

    @InjectMocks
    private ApiMovieServiceImpl apiMovieServiceImpl;

    @Test
    public void findAllMovies_validRequest() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        Movie m2 = new Movie("Moana", "Animation", "m.png", "moana live action", 90);
        when(movieDAOMock.findAllMovies()).thenReturn(List.of(m1, m2));
        List<MovieResponse> responses = apiMovieServiceImpl.findAllMovies();
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

        assertEquals(2, responses.size());
    }

    @Test
    public void findAllMovies_noMovies() {
        when(movieDAOMock.findAllMovies()).thenReturn(List.of());
        List<MovieResponse> responses = apiMovieServiceImpl.findAllMovies();
        assertTrue(responses.isEmpty());
    }

    @Test
    public void findMovieById_validRequest() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        when(movieDAOMock.findMovieById(1)).thenReturn(m1);

        MovieResponse response = apiMovieServiceImpl.findMovieById(1);
        assertEquals(1, response.id());
        assertEquals("Inception", response.name());
        assertEquals("Fantasy", response.genre());
        assertEquals("cobb", response.description());
        assertEquals("i.png", response.posterURL());
        assertEquals(150, response.runtimeInMinutes());
    }

    @Test
    public void findMovieById_noMovie() {
        when(movieDAOMock.findMovieById(1)).thenReturn(null);
        assertThrows(MovieNotFoundException.class, () -> apiMovieServiceImpl.findMovieById(1));
    }

    @Test
    public void getMovieByName_validRequest() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        m1.setId(1);
        when(movieDAOMock.findMoviesByName("Inception")).thenReturn(List.of(m1));
        List<MovieResponse> responses = apiMovieServiceImpl.getMovieByName("Inception");
        assertEquals(1, responses.get(0).id());
        assertEquals("Inception", responses.get(0).name());
        assertEquals("Fantasy", responses.get(0).genre());
        assertEquals("cobb", responses.get(0).description());
        assertEquals("i.png", responses.get(0).posterURL());
        assertEquals(150, responses.get(0).runtimeInMinutes());

        assertEquals(1, responses.size());
    }

    @Test
    public void getMovieByName_noMovies() {
        when(movieDAOMock.findMoviesByName("Inception")).thenReturn(List.of());
        List<MovieResponse> responses = apiMovieServiceImpl.getMovieByName("Inception");
        assertTrue(responses.isEmpty());
    }

    @Test
    public void addMovie_validRequest() {
        MovieRequest r = new MovieRequest("Inception", "Fantasy", "i.png", "cobb", 150);
        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);
        MovieResponse response = apiMovieServiceImpl.addMovie(r);
        verify(movieDAOMock).addMovie(captor.capture());
        Movie m1 = captor.getValue();
        assertEquals("Inception", m1.getMovieName());
        assertEquals("Fantasy", m1.getGenre());
        assertEquals("cobb", m1.getDescription());
        assertEquals("i.png", m1.getPosterURL());
        assertEquals(150, m1.getRunTimeInMinutes());

        assertEquals("Inception", response.name());
        assertEquals("Fantasy", response.genre());
        assertEquals("cobb", response.description());
        assertEquals("i.png", response.posterURL());
        assertEquals(150, response.runtimeInMinutes());
    }

    @Test
    public void editMovie_validRequest() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        when(movieDAOMock.findMovieById(1)).thenReturn(m1);
        MovieRequest r = new MovieRequest("Moana", "Animation", "m.png", "moana live action", 90);
        MovieResponse response = apiMovieServiceImpl.editMovie(1, r);
        assertEquals("Moana", response.name());
        assertEquals("Animation", response.genre());
        assertEquals("moana live action", response.description());
        assertEquals("m.png", response.posterURL());
        assertEquals(90, response.runtimeInMinutes());

        assertEquals("Moana", m1.getMovieName());
        assertEquals("Animation", m1.getGenre());
        assertEquals("moana live action", m1.getDescription());
        assertEquals("m.png", m1.getPosterURL());
        assertEquals(90, m1.getRunTimeInMinutes());
    }

    @Test
    public void editMovie_noMovie() {
        when(movieDAOMock.findMovieById(1)).thenReturn(null);
        assertThrows(MovieNotFoundException.class, () -> apiMovieServiceImpl.editMovie(1,
                new MovieRequest("Moana", "Animation", "m.png", "moana live action", 90)));
    }

    @Test
    public void deleteMovie_validRequest() {
        Movie m1 = new Movie("Inception", "Fantasy", "i.png", "cobb", 150);
        when(movieDAOMock.findMovieById(1)).thenReturn(m1);
        apiMovieServiceImpl.deleteMovie(1);
        verify(movieDAOMock).deleteMovie(m1);
    }

    @Test
    public void deleteMovie_noMovie() {
        when(movieDAOMock.findMovieById(1)).thenReturn(null);
        assertThrows(MovieNotFoundException.class, () -> apiMovieServiceImpl.deleteMovie(1));
    }
}
