package com.earl.bank.entityfactories;

import java.math.BigDecimal;

import com.earl.bank.entities.SavingsAccount;

public interface SavingsAccountFactory {

	SavingsAccount create(long accountNumber, BigDecimal initDeposit);

}