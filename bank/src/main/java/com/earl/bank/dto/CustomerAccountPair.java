package com.earl.bank.dto;

import com.earl.bank.entities.Account;
import com.earl.bank.entities.Customer;

public record CustomerAccountPair(Customer customer, Account account) {

}
