package com.earl.bank.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;

@Entity
public class SavingsAccount extends Account {

	private String safetyDepositBoxID;
	private String safetyDepositBoxKey;

	public SavingsAccount() {

	}

	public SavingsAccount(String accountNumber, BigDecimal balance) {
		super(accountNumber, balance);
	}

	public String getSafetyDepositBoxID() {
		return safetyDepositBoxID;
	}

	public void setSafetyDepositBoxID(String safetyDepositBoxID) {
		this.safetyDepositBoxID = safetyDepositBoxID;
	}

	public String getSafetyDepositBoxKey() {
		return safetyDepositBoxKey;
	}

	public void setSafetyDepositBoxKey(String safetyDepositBoxKey) {
		this.safetyDepositBoxKey = safetyDepositBoxKey;
	}

	@Override
	public String toString() {
		return "SavingsAccount [safetyDepositBoxID=" + safetyDepositBoxID + ", safetyDepositBoxKey="
				+ safetyDepositBoxKey + ", id=" + id + ", balance=" + balance + ", accountNumber=" + accountNumber
				+ ", rate=" + rate + "]";
	}

}
