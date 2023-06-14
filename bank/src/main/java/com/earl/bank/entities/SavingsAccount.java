package com.earl.bank.entities;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.Entity;

@Entity
public class SavingsAccount extends Account {

	private String safetyDepositBoxId;
	private String safetyDepositBoxKey;

	public SavingsAccount() {

	}

	public SavingsAccount(long accountNumber, BigDecimal balance, Set<Customer> customerSet) {
		super(accountNumber, balance, customerSet);
	}

	public String getSafetyDepositBoxId() {
		return safetyDepositBoxId;
	}

	public void setSafetyDepositBoxId(String safetyDepositBoxID) {
		this.safetyDepositBoxId = safetyDepositBoxID;
	}

	public String getSafetyDepositBoxKey() {
		return safetyDepositBoxKey;
	}

	public void setSafetyDepositBoxKey(String safetyDepositBoxKey) {
		this.safetyDepositBoxKey = safetyDepositBoxKey;
	}

	@Override
	public String toString() {
		return "SavingsAccount [safetyDepositBoxID=" + safetyDepositBoxId + ", safetyDepositBoxKey="
				+ safetyDepositBoxKey + ", id=" + id + ", balance=" + balance + ", accountNumber=" + accountNumber
				+ ", rate=" + rate + "]";
	}

}
