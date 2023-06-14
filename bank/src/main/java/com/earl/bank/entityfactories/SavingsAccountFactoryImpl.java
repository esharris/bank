package com.earl.bank.entityfactories;

import java.math.BigDecimal;
import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.earl.bank.entities.SavingsAccount;
import com.earl.bank.random.RandomNumeralString;

@Component
public class SavingsAccountFactoryImpl implements SavingsAccountFactory {

	private final RandomNumeralString randomNumeralString;

	public SavingsAccountFactoryImpl(RandomNumeralString randomNumeralString) {
		this.randomNumeralString = randomNumeralString;
	}

	@Override
	public SavingsAccount create(long accountNumber, BigDecimal initDeposit) {
		SavingsAccount result = new SavingsAccount(accountNumber, initDeposit, new HashSet<>());
		result.setRate(BaseRateSingleton.getInstance().getValue().subtract(new BigDecimal("0.25")));
		result.setSafetyDepositBoxID(randomNumeralString.nextNumeralString(3));
		result.setSafetyDepositBoxKey(randomNumeralString.nextNumeralString(4));
		return result;
	}
}
