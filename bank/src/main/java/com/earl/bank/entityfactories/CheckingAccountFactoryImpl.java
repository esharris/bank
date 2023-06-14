package com.earl.bank.entityfactories;

import java.math.BigDecimal;
import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.earl.bank.entities.CheckingAccount;
import com.earl.bank.random.RandomNumeralString;

@Component
public class CheckingAccountFactoryImpl implements CheckingAccountFactory {

	private static long index = 100000;

	private final RandomNumeralString randomNumeralString;

	public CheckingAccountFactoryImpl(RandomNumeralString randomNumeralString) {
		this.randomNumeralString = randomNumeralString;
	}

	@Override
	public CheckingAccount create(long accountNumber, BigDecimal initDeposit) {
		CheckingAccount result = new CheckingAccount(accountNumber, initDeposit, new HashSet<>());
		result.setRate(BaseRateSingleton.getInstance().getValue().multiply(new BigDecimal("1.5")));
		result.setDebitCardNumber(index++);
		result.setDebitCardPIN(randomNumeralString.nextNumeralString(4));
		return result;
	}
}
