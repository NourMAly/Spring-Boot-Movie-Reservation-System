package com.nour.SpringBootMovieReservationSystem.service;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.User;

public interface UserService {

    public User findUserById(int id);

    public void addUser(User customer);

    public void deleteUser(User customer);

    public List<User> findAllUsers();

    public User findUserByUsername(String username);
}
