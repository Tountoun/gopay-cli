package com.gofar.gopay.domain.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> find(Long id);
    Customer save(Customer customer);
    List<Customer> list();
    List<Customer> listByStatus(Status status);
}
