package com.nour.SpringBootMovieReservationSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.dao.UserDAO;
import com.nour.SpringBootMovieReservationSystem.entity.User;

@Service
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void addUser(User user) {
        userDAO.addUser(user);
    }

    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userDAO.findAllUsers();
    }

    @Override
    public User findUserById(int id) {
        return userDAO.findUserById(id);
    }

    @Override
    public User findUserByUsername(String username) {
        return userDAO.findUserByUsername(username);
    }

}
