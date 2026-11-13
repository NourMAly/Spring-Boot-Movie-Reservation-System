package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.User;

public interface UserDAO {
    public User findUserById(int id);

    public void addUser(User user);

    public void addRole(String username, String role);

    public void deleteUser(User user);

    public List<User> findAllUsers();

    public User findUserByUsername(String username);

    public boolean isUsernameUnique(String username);

    public boolean isEmailUnique(String email);
}
