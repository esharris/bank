package com.earl.bank.entities;

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
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;

	@Column(unique = true)
	private String socialSecurityNumber;

	private String firstName;
	private String lastName;

	@ManyToMany(targetEntity = Account.class
//			, fetch = FetchType.EAGER
	)
	@JsonIgnore
	private Set<Account> accountSet;

	public Customer() {

	}

	public Customer(String socialSecurityNumber, String firstName, String lastName, Set<Account> accountSet) {
		super();
		this.socialSecurityNumber = socialSecurityNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.accountSet = accountSet;
		for (Account account : accountSet) {
			account.getCustomerSet().add(this);
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Set<Account> getAccountSet() {
		return accountSet;
	}

	public void setAccountSet(Set<Account> accountSet) {
		this.accountSet = accountSet;
	}

	public void addAccount(Account account) {
		this.getAccountSet().add(account);
		account.getCustomerSet().add(this);
	}

	public void removeAccount(Account account) {
		this.getAccountSet().remove(account);
		account.getCustomerSet().remove(this);
	}

	public void removeAllAccounts(Function<Account, Account> accountFunction) {
		/**
		 * Remove the customer from every account that has the customer.
		 */
		for (Account account : this.getAccountSet()) {
			account.getCustomerSet().remove(this);
			accountFunction.apply(account);
		}
		this.getAccountSet().clear();
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", socialSecurityNumber=" + socialSecurityNumber + ", firstName=" + firstName
				+ ", lastName=" + lastName + "]";
	}
}
