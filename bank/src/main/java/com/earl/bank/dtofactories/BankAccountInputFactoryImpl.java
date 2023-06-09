package com.earl.bank.dtofactories;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.earl.bank.dto.BankAccountInput;

@Component
public class BankAccountInputFactoryImpl implements BankAccountInputFactory {

	@Override
	public BankAccountInput create(String dataRow) {
		String[] dataRecords = dataRow.split(",");
		if (dataRecords.length == 3) {
			long accountNumber = Long.valueOf(dataRecords[0]);
			String accountType = dataRecords[1];
			BigDecimal initDeposit = new BigDecimal(dataRecords[2]);
			return new BankAccountInput(accountNumber, accountType, initDeposit);
		} else {
			throw new RuntimeException("Expected 3 bank account components, but saw " + dataRecords.length);
		}
	}
}
