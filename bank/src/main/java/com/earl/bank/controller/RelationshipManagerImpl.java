package com.earl.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.earl.bank.dto.CustomerAccountPair;
import com.earl.bank.entities.Account;
import com.earl.bank.entities.Customer;
import com.earl.bank.jpa.AccountRepository;
import com.earl.bank.jpa.CustomerRepository;
import com.earl.bank.jpa.LinkAlreadyThereException;
import com.earl.bank.jpa.LinkNonexistentException;

@Component
public class RelationshipManagerImpl implements RelationshipManager {

	private final CustomerRepository customerRepository;

	private final AccountRepository accountRepository;

	@Autowired
	public RelationshipManagerImpl(CustomerRepository customerRepository, AccountRepository accountRepository) {
		this.customerRepository = customerRepository;
		this.accountRepository = accountRepository;
	}

	@Override
	public CustomerAccountPair addAccount(Customer customer, Account account) {
		if (!customer.getAccountSet().contains(account)) {
			customer.addAccount(account);
			return new CustomerAccountPair(customerRepository.save(customer), accountRepository.save(account));
		} else {
			throw new LinkAlreadyThereException(customer.getSocialSecurityNumber(), account.getAccountNumber());
		}
	}

	@Override
	public CustomerAccountPair addCustomer(Account account, Customer customer) {
		if (!account.getCustomerSet().contains(customer)) {
			account.addCustomer(customer);
			return new CustomerAccountPair(customerRepository.save(customer), accountRepository.save(account));
		} else {
			throw new LinkAlreadyThereException(customer.getSocialSecurityNumber(), account.getAccountNumber());
		}
	}

	@Override
	public void removeAccount(Customer customer, Account account) {
		if (customer.getAccountSet().contains(account)) {
			customer.removeAccount(account);
			customerRepository.save(customer);
			accountRepository.save(account);
		} else {
			throw new LinkNonexistentException(customer.getSocialSecurityNumber(), account.getAccountNumber());
		}
	}

	@Override
	public void removeCustomer(Account account, Customer customer) {
		if (account.getCustomerSet().contains(customer)) {
			account.removeCustomer(customer);
			accountRepository.save(account);
			customerRepository.save(customer);
		} else {
			throw new LinkNonexistentException(customer.getSocialSecurityNumber(), account.getAccountNumber());
		}
	}

	@Override
	public void removeAccount(Account account) {
		account.removeAllCustomers((customer) -> customerRepository.save(customer));
		accountRepository.delete(account);
	}

	@Override
	public void removeCustomer(Customer customer) {
		customer.removeAllAccounts((account) -> accountRepository.save(account));
		customerRepository.delete(customer);
	}
}
