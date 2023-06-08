package com.earl.bank.entities;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public abstract class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;

	@Column(unique = true)
	protected String accountNumber;

	protected BigDecimal balance;

	protected BigDecimal rate;

	@ManyToMany(targetEntity = Customer.class
//			, fetch = FetchType.EAGER
	)
	@JsonIgnore
	private Set<Customer> customerSet;

	public Account() {

	}

	public Account(String accountNumber, BigDecimal balance, Set<Customer> customerSet) {
		super();
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.customerSet = customerSet;
		for (Customer customer : customerSet) {
			customer.getAccountSet().add(this);
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Set<Customer> getCustomerSet() {
		return customerSet;
	}

	public void setCustomerSet(Set<Customer> customerSet) {
		this.customerSet = customerSet;
	}

	public void addCustomer(Customer customer) {
		this.getCustomerSet().add(customer);
		customer.getAccountSet().add(this);
	}

	public void removeCustomer(Customer customer) {
		this.getCustomerSet().remove(customer);
		customer.getAccountSet().remove(this);
	}

	public void removeAllCustomers(Function<Customer, Customer> customerFunction) {
		/**
		 * Remove the account from every customer that has the account.
		 */
		for (Customer customer : this.getCustomerSet()) {
			customer.getAccountSet().remove(this);
			customerFunction.apply(customer);
		}
		this.getCustomerSet().clear();
	}
}
