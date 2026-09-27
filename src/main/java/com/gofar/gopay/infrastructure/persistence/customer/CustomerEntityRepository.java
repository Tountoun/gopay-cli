package com.gofar.gopay.infrastructure.persistence.customer;

import com.gofar.gopay.domain.customer.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, Long> {

    List<CustomerEntity> findByStatus(Status status);
}
