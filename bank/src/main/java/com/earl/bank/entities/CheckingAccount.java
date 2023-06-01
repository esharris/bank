package com.earl.bank.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;

@Entity
public class CheckingAccount extends Account {

	private long debitCardNumber;
	private String debitCardPIN;

	public CheckingAccount() {

	}

	public CheckingAccount(String firstName, String lastName, String socialSecurityNumber, BigDecimal balance,
			String accountNumber) {
		super(firstName, lastName, socialSecurityNumber, balance, accountNumber);
	}

	public long getDebitCardNumber() {
		return debitCardNumber;
	}

	public void setDebitCardNumber(long debitCardNumber) {
		this.debitCardNumber = debitCardNumber;
	}

	public String getDebitCardPIN() {
		return debitCardPIN;
	}

	public void setDebitCardPIN(String debitCardPIN) {
		this.debitCardPIN = debitCardPIN;
	}

	@Override
	public String toString() {
		return "CheckingAccount [debitCardNumber=" + debitCardNumber + ", debitCardPIN=" + debitCardPIN + ", id=" + id
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", socialSecurityNumber="
				+ socialSecurityNumber + ", balance=" + balance + ", accountNumber=" + accountNumber + ", rate=" + rate
				+ "]";
	}

}
