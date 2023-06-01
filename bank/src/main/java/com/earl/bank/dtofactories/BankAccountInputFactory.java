package com.earl.bank.dtofactories;

import com.earl.bank.dto.BankAccountInput;

public interface BankAccountInputFactory {

	BankAccountInput create(String dataRow);

}