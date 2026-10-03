package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.Customer;

public interface CustomerDAO {
    public Customer findCustomerById(int id);

    public void addCustomer(Customer customer);

    public void deleteCustomer(Customer customer);

    public List<Customer> findAllCustomers();

    public Customer findCustomerByUsername(String username);
}
