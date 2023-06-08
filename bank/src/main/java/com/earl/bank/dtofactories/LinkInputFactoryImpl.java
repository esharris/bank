package com.earl.bank.dtofactories;

import org.springframework.stereotype.Component;

import com.earl.bank.dto.LinkInput;

@Component
public class LinkInputFactoryImpl implements LinkInputFactory {

	@Override
	public LinkInput create(String dataRow) {
		String[] dataRecords = dataRow.split(",");
		if (dataRecords.length == 2) {
			String socialSecurityNumber = dataRecords[0];
			String accountNumber = dataRecords[1];
			return new LinkInput(socialSecurityNumber, accountNumber);
		} else {
			throw new RuntimeException("Expected 2 link components, but saw " + dataRecords.length);
		}
	}
}
