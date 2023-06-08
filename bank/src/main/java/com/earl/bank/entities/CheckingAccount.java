package com.earl.bank.entities;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.Entity;

@Entity
public class CheckingAccount extends Account {

	private long debitCardNumber;
	private String debitCardPIN;

	public CheckingAccount() {

	}

	public CheckingAccount(String accountNumber, BigDecimal balance, Set<Customer> customerSet) {
		super(accountNumber, balance, customerSet);
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
				+ ", balance=" + balance + ", accountNumber=" + accountNumber + ", rate=" + rate + "]";
	}

}
