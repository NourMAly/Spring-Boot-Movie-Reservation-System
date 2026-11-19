package com.nour.SpringBootMovieReservationSystem.apiservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nour.SpringBootMovieReservationSystem.CustomException.PasswordMismatchException;
import com.nour.SpringBootMovieReservationSystem.CustomException.UserAlreadyExistsException;
import com.nour.SpringBootMovieReservationSystem.CustomException.UserNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.UserDAO;
import com.nour.SpringBootMovieReservationSystem.dto.RegisterRequest;
import com.nour.SpringBootMovieReservationSystem.dto.UserResponse;
import com.nour.SpringBootMovieReservationSystem.entity.User;

@ExtendWith(MockitoExtension.class)
public class ApiUserServiceImplTest {

    @Mock
    private UserDAO userDAO;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ApiUserServiceImpl apiUserServiceImpl;

    @Test
    public void registerUser_validRequest() {
        RegisterRequest request = new RegisterRequest("nour", "moh", "nm@g.com", "nm", "fun12345", "fun12345");
        when(userDAO.isUsernameUnique("nm")).thenReturn(true);
        when(userDAO.isEmailUnique("nm@g.com")).thenReturn(true);
        when(passwordEncoder.encode("fun12345")).thenReturn("encodedPassword");

        apiUserServiceImpl.registerUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).addUser(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("nour", savedUser.getFirstName());
        assertEquals("moh", savedUser.getLastName());
        assertEquals("nm@g.com", savedUser.getEmail());
        assertEquals("nm", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPw());
        assertEquals(true, savedUser.isActive());

        verify(userDAO).addRole("nm", "ROLE_CUSTOMER");
        verify(userDAO).isEmailUnique("nm@g.com");
        verify(userDAO).isUsernameUnique("nm");
        verify(userDAO).addUser(any(User.class));
        verify(userDAO).addRole("nm", "ROLE_CUSTOMER");
    }

    @Test
    public void registerUser_passwordMismatch() {
        RegisterRequest request = new RegisterRequest("nour", "moh", "nm@g.com", "nm", "fun54321", "fun12345");
        assertThrows(PasswordMismatchException.class, () -> apiUserServiceImpl.registerUser(request));
        verifyNoInteractions(userDAO, passwordEncoder);
    }

    @Test
    public void registerUser_duplicateUsername() {
        RegisterRequest request = new RegisterRequest("nour", "moh", "nm@g.com", "nm", "fun12345", "fun12345");
        when(userDAO.isUsernameUnique("nm")).thenReturn(false);
        assertThrows(UserAlreadyExistsException.class, () -> apiUserServiceImpl.registerUser(request));
        verify(userDAO).isUsernameUnique("nm");

    }

    @Test
    public void registerUser_duplicateEmail() {
        RegisterRequest request = new RegisterRequest("nour", "moh", "nm@g.com", "nm", "fun12345", "fun12345");
        when(userDAO.isUsernameUnique("nm")).thenReturn(true);
        when(userDAO.isEmailUnique("nm@g.com")).thenReturn(false);
        assertThrows(UserAlreadyExistsException.class, () -> apiUserServiceImpl.registerUser(request));
        verify(userDAO).isEmailUnique("nm@g.com");
        verify(userDAO).isUsernameUnique("nm");
    }

    @Test
    public void registerUser_databaseConstraintViolation() {
        RegisterRequest request = new RegisterRequest(
                "nour", "moh", "nm@g.com", "nm", "fun12345", "fun12345");

        when(userDAO.isUsernameUnique("nm")).thenReturn(true);
        when(userDAO.isEmailUnique("nm@g.com")).thenReturn(true);
        when(passwordEncoder.encode("fun12345")).thenReturn("encodedPassword");

        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("Duplicate entry"))
                .when(userDAO).addUser(any(User.class));

        assertThrows(UserAlreadyExistsException.class,
                () -> apiUserServiceImpl.registerUser(request));

        verify(userDAO).addUser(any(User.class));
    }

    @Test
    public void viewProfile_validRequest() {
        User user = new User("nour", "moh", "nm@g.com", "nm", "fun12345", true);
        when(userDAO.findUserByUsername("nm")).thenReturn(user);
        UserResponse response = apiUserServiceImpl.viewProfile("nm", List.of("ROLE_CUSTOMER"));
        verify(userDAO).findUserByUsername("nm");
        assertEquals("nour", response.firstName());
        assertEquals("moh", response.lastname());
        assertEquals("nm@g.com", response.email());
        assertEquals("nm", response.username());
        assertEquals(List.of("ROLE_CUSTOMER"), response.roles());
    }

    @Test
    public void viewProfile_noUser() {
        when(userDAO.findUserByUsername("nm")).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> apiUserServiceImpl.viewProfile("nm", null));
        verify(userDAO).findUserByUsername("nm");
    }
}
