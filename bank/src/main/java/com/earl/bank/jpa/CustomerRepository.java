package com.earl.bank.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.earl.bank.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findBySocialSecurityNumber(String socialSecurityNumber);

	boolean existsBySocialSecurityNumber(String socialSecurityNumber);
}
