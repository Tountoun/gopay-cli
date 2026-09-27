package com.gofar.gopay.infrastructure.persistence.customer;

import com.gofar.gopay.domain.customer.Customer;
import com.gofar.gopay.domain.customer.CustomerRepository;
import com.gofar.gopay.domain.customer.Status;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerEntityRepository repository;
    private final CustomerMapper mapper;

    public CustomerRepositoryAdapter(CustomerEntityRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Customer> find(Long id) {
        Optional<CustomerEntity> optionalCustomer = repository.findById(id);
        return optionalCustomer.map(mapper::toCustomer);
    }

    @Override
    public Customer save(Customer customerDto) {
        CustomerEntity customer = mapper.toEntity(customerDto);
        CustomerEntity saved = repository.save(customer);
        return mapper.toCustomer(saved);
    }

    @Override
    public List<Customer> list() {
        return mapper.toCustomers(repository.findAll());
    }

    @Override
    public List<Customer> listByStatus(Status status) {
        return mapper.toCustomers(repository.findByStatus(status));
    }
}
