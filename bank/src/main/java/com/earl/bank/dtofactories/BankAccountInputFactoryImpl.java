package com.earl.bank.dtofactories;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.earl.bank.dto.BankAccountInput;

@Component
public class BankAccountInputFactoryImpl implements BankAccountInputFactory {

	@Override
	public BankAccountInput create(String dataRow) {
		String[] dataRecords = dataRow.split(",");
		if (dataRecords.length == 5) {
			String firstName = dataRecords[0];
			String lastName = dataRecords[1];
			String sSN = dataRecords[2];
			String accountType = dataRecords[3];
			BigDecimal initDeposit = new BigDecimal(dataRecords[4]);
			return new BankAccountInput(firstName, lastName, sSN, accountType, initDeposit);
		} else {
			throw new RuntimeException("Expected 4 components, but saw " + dataRecords.length);
		}
	}
}
