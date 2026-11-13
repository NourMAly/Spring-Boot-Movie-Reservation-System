package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.CustomException.PasswordMismatchException;
import com.nour.SpringBootMovieReservationSystem.CustomException.UserAlreadyExistsException;
import com.nour.SpringBootMovieReservationSystem.CustomException.UserNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.UserDAO;
import com.nour.SpringBootMovieReservationSystem.dto.RegisterRequest;
import com.nour.SpringBootMovieReservationSystem.dto.UserResponse;
import com.nour.SpringBootMovieReservationSystem.entity.User;

import jakarta.transaction.Transactional;

@Service
public class ApiUserServiceImpl implements ApiUserService {

    private UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    public ApiUserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (!registerRequest.password().equals(registerRequest.confirmPassword()))
            throw new PasswordMismatchException("Passwords do not match");

        String firstname = registerRequest.firstname().trim();
        String lastname = registerRequest.lastname().trim();
        String email = registerRequest.email().trim().toLowerCase();
        String username = registerRequest.username().trim();
        if (!userDAO.isUsernameUnique(registerRequest.username()))
            throw new UserAlreadyExistsException("Username already taken");
        if (!userDAO.isEmailUnique(email))
            throw new UserAlreadyExistsException("Email is already registered");
        String password = registerRequest.password();
        try {
            userDAO.addUser(new User(firstname, lastname, email, username, passwordEncoder.encode(password), true));
            userDAO.addRole(username, "ROLE_CUSTOMER");
        } catch (DataIntegrityViolationException e) {
            // simultaneous registrations with same username/email caught by db uniqness
            // check
            throw new UserAlreadyExistsException("Username or email already in use");
        }

    }

    @Override
    public UserResponse viewProfile(String username, List<String> roles) {
        User user = userDAO.findUserByUsername(username);
        if (user == null)
            throw new UserNotFoundException("User not found");
        return new UserResponse(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername(), roles);
    }

}
