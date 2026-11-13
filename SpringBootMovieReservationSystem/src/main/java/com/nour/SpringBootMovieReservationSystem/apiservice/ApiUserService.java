package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.dto.RegisterRequest;
import com.nour.SpringBootMovieReservationSystem.dto.UserResponse;

public interface ApiUserService {

    public void registerUser(RegisterRequest registerRequest);

    public UserResponse viewProfile(String username, List<String> roles);
}
