package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nour.SpringBootMovieReservationSystem.entity.Customer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class CustomerDAOImpl implements CustomerDAO {
    private EntityManager entityManager;

    public CustomerDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void addCustomer(Customer customer) {
        entityManager.persist(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Customer customer) {
        entityManager.remove(customer);
    }

    @Override
    public List<Customer> findAllCustomers() {
        TypedQuery<Customer> query = entityManager.createQuery("FROM Customer", Customer.class);
        return query.getResultList();
    }

    @Override
    public Customer findCustomerById(int id) {
        return entityManager.find(Customer.class, id);
    }

    @Override
    public Customer findCustomerByUsername(String username) {
        TypedQuery<Customer> query = entityManager.createQuery("FROM Customer WHERE username=:username",
                Customer.class);
        return query.setParameter("username", username).getSingleResult();
    }

}
