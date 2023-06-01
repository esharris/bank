package com.earl.bank.entityfactories;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.earl.bank.entities.CheckingAccount;
import com.earl.bank.random.RandomAccountNumberGenerator;
import com.earl.bank.random.RandomNumeralString;

@Component
public class CheckingAccountFactoryImpl implements CheckingAccountFactory {

	private static long index = 100000;

	private final RandomNumeralString randomNumeralString;

	private final RandomAccountNumberGenerator randomAccountNumberGenerator;

	public CheckingAccountFactoryImpl(RandomNumeralString randomNumeralString,
			RandomAccountNumberGenerator randomAccountNumberGenerator) {
		this.randomNumeralString = randomNumeralString;
		this.randomAccountNumberGenerator = randomAccountNumberGenerator;
	}

	@Override
	public CheckingAccount create(String firstName, String lastName, String socialSecurityNumber,
			BigDecimal initDeposit) {
		CheckingAccount result = new CheckingAccount(firstName, lastName, socialSecurityNumber, initDeposit,
				randomAccountNumberGenerator.nextAccountNumber(socialSecurityNumber));
		result.setRate(BaseRateSingleton.getInstance().getValue().multiply(new BigDecimal("1.5")));
		result.setAccountNumber("2" + result.getAccountNumber());
		result.setDebitCardNumber(index++);
		result.setDebitCardPIN(randomNumeralString.nextNumeralString(4));
		return result;
	}
}
