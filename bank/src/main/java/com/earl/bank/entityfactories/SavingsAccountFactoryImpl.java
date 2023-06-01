package com.earl.bank.entityfactories;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.earl.bank.entities.SavingsAccount;
import com.earl.bank.random.RandomAccountNumberGenerator;
import com.earl.bank.random.RandomNumeralString;

@Component
public class SavingsAccountFactoryImpl implements SavingsAccountFactory {

	private final RandomNumeralString randomNumeralString;

	private final RandomAccountNumberGenerator randomAccountNumberGenerator;

	public SavingsAccountFactoryImpl(RandomNumeralString randomNumeralString,
			RandomAccountNumberGenerator randomAccountNumberGenerator) {
		this.randomNumeralString = randomNumeralString;
		this.randomAccountNumberGenerator = randomAccountNumberGenerator;
	}

	@Override
	public SavingsAccount create(String firstName, String lastName, String socialSecurityNumber,
			BigDecimal initDeposit) {
		SavingsAccount result = new SavingsAccount(firstName, lastName, socialSecurityNumber, initDeposit,
				randomAccountNumberGenerator.nextAccountNumber(socialSecurityNumber));
		result.setRate(BaseRateSingleton.getInstance().getValue().subtract(new BigDecimal("0.25")));
		result.setAccountNumber("2" + result.getAccountNumber());
		result.setSafetyDepositBoxID(randomNumeralString.nextNumeralString(3));
		result.setSafetyDepositBoxKey(randomNumeralString.nextNumeralString(4));
		return result;
	}
}
