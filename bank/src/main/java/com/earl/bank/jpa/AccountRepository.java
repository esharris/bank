package com.earl.bank.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.earl.bank.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByAccountNumber(String accountNumber);

	boolean existsByAccountNumber(String accountNumber);
}
