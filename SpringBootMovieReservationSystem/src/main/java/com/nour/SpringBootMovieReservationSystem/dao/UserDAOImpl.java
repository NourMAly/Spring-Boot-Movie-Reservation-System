package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class UserDAOImpl implements UserDAO {
    private EntityManager entityManager;

    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void addUser(User user) {
        entityManager.persist(user);
        entityManager.flush(); // insert now to catch duplicates
    }

    @Override
    public void addRole(String username, String role) {
        entityManager.createNativeQuery("INSERT INTO roles(username,role) values(:username,:role)")
                .setParameter("username", username)
                .setParameter("role", role).executeUpdate();
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        entityManager.remove(user);
    }

    @Override
    public List<User> findAllUsers() {
        TypedQuery<User> query = entityManager.createQuery("FROM User", User.class);
        return query.getResultList();
    }

    @Override
    public User findUserById(int id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findUserByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery("FROM User WHERE username=:username",
                User.class);
        return query.setParameter("username", username).getSingleResultOrNull();
    }

    @Override
    public boolean isUsernameUnique(String username) {
        TypedQuery<User> query = entityManager.createQuery("FROM User WHERE username=:username", User.class);
        query.setParameter("username", username);
        return query.getSingleResultOrNull() == null;
    }

    @Override
    public boolean isEmailUnique(String email) {
        TypedQuery<User> query = entityManager.createQuery("FROM User WHERE email=:email", User.class);
        query.setParameter("email", email);
        return query.getSingleResultOrNull() == null;
    }

}
