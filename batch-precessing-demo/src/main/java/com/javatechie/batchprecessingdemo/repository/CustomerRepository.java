package com.javatechie.batchprecessingdemo.repository;

import com.javatechie.batchprecessingdemo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
