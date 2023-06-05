package com.earl.bank;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.earl.bank.dto.BankAccountInput;
import com.earl.bank.dto.CustomerInput;
import com.earl.bank.dtofactories.BankAccountInputFactory;
import com.earl.bank.dtofactories.CustomerInputFactory;
import com.earl.bank.entities.Account;
import com.earl.bank.entities.Customer;
import com.earl.bank.entityfactories.CheckingAccountFactory;
import com.earl.bank.entityfactories.CustomerFactory;
import com.earl.bank.entityfactories.SavingsAccountFactory;
import com.earl.bank.jpa.AccountRepository;
import com.earl.bank.jpa.AccountRepositoryHelper;
import com.earl.bank.jpa.CustomerRepository;
import com.earl.bank.jpa.CustomerRepositoryHelper;

@SpringBootApplication
public class BankApplication {

	private static final Logger log = LoggerFactory.getLogger(BankApplication.class);

	private final BankAccountInputFactory bankAccountInputFactory;

	private final CustomerInputFactory customerInputFactory;

	private final CheckingAccountFactory checkingAccountFactory;

	private final SavingsAccountFactory savingsAccountFactory;

	private final CustomerFactory customerFactory;

	private final AccountRepository accountRepository;

	private final CustomerRepository customerRepository;

	@Autowired
	public BankApplication(BankAccountInputFactory bankAccountInputFactory, CustomerInputFactory customerInputFactory,
			CheckingAccountFactory checkingAccountFactory, SavingsAccountFactory savingsAccountFactory,
			CustomerFactory customerFactory, AccountRepository accountRepository,
			CustomerRepository customerRepository) {
		this.bankAccountInputFactory = bankAccountInputFactory;
		this.customerInputFactory = customerInputFactory;
		this.savingsAccountFactory = savingsAccountFactory;
		this.checkingAccountFactory = checkingAccountFactory;
		this.customerFactory = customerFactory;
		this.accountRepository = accountRepository;
		this.customerRepository = customerRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

	@Bean
	CommandLineRunner demo() {
		return args -> {
			final String accountFileName = "data/NewBankAccounts.csv";
			final String customerFileName = "data/NewBankCustomers.csv";
			String dataRow;
			try {
				try (BufferedReader br = new BufferedReader(new FileReader(accountFileName))) {
					while ((dataRow = br.readLine()) != null) {
						BankAccountInput bankAccountInput = bankAccountInputFactory.create(dataRow);
						if ("Checking".equals(bankAccountInput.accountType())) {
							AccountRepositoryHelper.saveIfAccountNumberUnique(accountRepository, checkingAccountFactory
									.create(bankAccountInput.accountNumber(), bankAccountInput.initDeposit()));
						} else if ("Savings".equals(bankAccountInput.accountType())) {
							AccountRepositoryHelper.saveIfAccountNumberUnique(accountRepository, savingsAccountFactory
									.create(bankAccountInput.accountNumber(), bankAccountInput.initDeposit()));
						} else {
							log.warn("Unknown account type; " + bankAccountInput.accountType());
						}
					}
				}
			} catch (FileNotFoundException e) {
				log.error("Couldn't find account file to open: " + accountFileName);
			} catch (IOException e) {
				log.error("Couldn't read account file");
			}
			final List<Account> accountList = accountRepository.findAll();
			for (Account account : accountList) {
				log.info(account.toString());
				log.info("");
			}
			try {
				try (BufferedReader br = new BufferedReader(new FileReader(customerFileName))) {
					while ((dataRow = br.readLine()) != null) {
						CustomerInput customerInput = customerInputFactory.create(dataRow);
						CustomerRepositoryHelper.saveIfSocialSecurityNumberUnique(customerRepository,
								customerFactory.create(customerInput.socialSecurityNumber(), customerInput.firstName(),
										customerInput.lastName()));
					}
				}
			} catch (FileNotFoundException e) {
				log.error("Couldn't find customer file to open: " + accountFileName);
			} catch (IOException e) {
				log.error("Couldn't read customer file");
			}
			final List<Customer> customerList = customerRepository.findAll();
			for (Customer customer : customerList) {
				log.info(customer.toString());
				log.info("");
			}
		};
	}
}
