package com.earl.bank.controller;

import com.earl.bank.dto.CustomerAccountPair;
import com.earl.bank.entities.Account;
import com.earl.bank.entities.Customer;

public interface RelationshipManager {

	CustomerAccountPair addAccount(Customer customer, Account account);

	CustomerAccountPair addCustomer(Account account, Customer customer);

	void removeAccount(Customer customer, Account account);

	void removeCustomer(Account account, Customer customer);

	void removeAccount(Account account);

	void removeCustomer(Customer customer);

}