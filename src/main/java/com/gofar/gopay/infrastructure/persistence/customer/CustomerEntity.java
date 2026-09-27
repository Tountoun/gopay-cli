package com.gofar.gopay.infrastructure.persistence.customer;


import com.gofar.gopay.domain.customer.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uc_customerentity_email", columnNames = {"email"})
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private Status status;

    public CustomerEntity(String name, String email, Status status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }
}

