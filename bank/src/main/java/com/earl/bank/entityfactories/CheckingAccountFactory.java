package com.earl.bank.entityfactories;

import java.math.BigDecimal;

import com.earl.bank.entities.CheckingAccount;

public interface CheckingAccountFactory {

	CheckingAccount create(String accountNumber, BigDecimal initDeposit);

}