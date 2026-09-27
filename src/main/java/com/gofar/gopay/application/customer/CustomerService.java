package com.gofar.gopay.application.customer;

import com.gofar.gopay.domain.customer.Customer;
import com.gofar.gopay.domain.customer.Status;
import com.gofar.gopay.domain.customer.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Optional<Customer> find(Long id) {
        return repository.find(id);
    }

    public Customer create(String name, String email, Status status) {
        Customer customer = new Customer(null, name, email, status);
        return repository.save(customer);
    }

    public List<Customer> list() {
        return repository.list();
    }

    public List<Customer> byStatus(Status status) {
        return repository.listByStatus(status);
    }
}
