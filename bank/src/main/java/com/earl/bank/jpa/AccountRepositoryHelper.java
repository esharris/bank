package com.earl.bank.jpa;

import com.earl.bank.entities.Account;

public class AccountRepositoryHelper {

	public static Account saveIfAccountNumberUnique(AccountRepository accountRepository, Account account) {
		if (!accountRepository.existsByAccountNumber(account.getAccountNumber())) {
			return accountRepository.save(account);
		} else {
			throw new DuplicateAccountNumberException(account.getAccountNumber());
		}
	}
}
