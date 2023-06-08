package com.earl.bank.jpa;

import java.util.Optional;

import com.earl.bank.entities.Account;
import com.earl.bank.jpa.exceptions.AccountNotFoundException;
import com.earl.bank.jpa.exceptions.DuplicateAccountNumberException;

public class AccountRepositoryHelper {

	public static Account saveIfAccountNumberUnique(AccountRepository accountRepository, Account account) {
		if (!accountRepository.existsByAccountNumber(account.getAccountNumber())) {
			return accountRepository.save(account);
		} else {
			throw new DuplicateAccountNumberException(account.getAccountNumber());
		}
	}

	public static Account getAccount(AccountRepository accountRepository, String accountNumber) {
		Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
		return optionalAccount.orElseThrow(() -> new AccountNotFoundException(accountNumber));
	}
}
