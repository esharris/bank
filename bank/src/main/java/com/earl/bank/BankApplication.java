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
import com.earl.bank.dtofactories.BankAccountInputFactory;
import com.earl.bank.entities.Account;
import com.earl.bank.entityfactories.CheckingAccountFactory;
import com.earl.bank.entityfactories.SavingsAccountFactory;
import com.earl.bank.jpa.AccountRepository;

@SpringBootApplication
public class BankApplication {

	private static final Logger log = LoggerFactory.getLogger(BankApplication.class);

	private final BankAccountInputFactory bankAccountInputFactory;

	private final CheckingAccountFactory checkingAccountFactory;

	private final SavingsAccountFactory savingsAccountFactory;

	private final AccountRepository accountRepository;

	@Autowired
	public BankApplication(BankAccountInputFactory bankAccountInputFactory,
			CheckingAccountFactory checkingAccountFactory, SavingsAccountFactory savingsAccountFactory,
			AccountRepository accountRepository) {
		this.bankAccountInputFactory = bankAccountInputFactory;
		this.savingsAccountFactory = savingsAccountFactory;
		this.checkingAccountFactory = checkingAccountFactory;
		this.accountRepository = accountRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

	@Bean
	CommandLineRunner demo() {
		return args -> {
			final String file = "data/NewBankAccounts.csv";
			String dataRow;
			try {
				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					while ((dataRow = br.readLine()) != null) {
						BankAccountInput bankAccountInput = bankAccountInputFactory.create(dataRow);
						if ("Checking".equals(bankAccountInput.accountType())) {
							accountRepository.save(checkingAccountFactory.create(bankAccountInput.firstName(),
									bankAccountInput.lastName(), bankAccountInput.socialSecurityNumber(),
									bankAccountInput.initDeposit()));
						} else if ("Savings".equals(bankAccountInput.accountType())) {
							accountRepository.save(savingsAccountFactory.create(bankAccountInput.firstName(),
									bankAccountInput.lastName(), bankAccountInput.socialSecurityNumber(),
									bankAccountInput.initDeposit()));
						} else {
							log.warn("Unknown account type; " + bankAccountInput.accountType());
						}
					}
				}
			} catch (FileNotFoundException e) {
				log.error("Couldn't find file to open: " + file);
			} catch (IOException e) {
				log.error("Couldn't read file");
			}
			final List<Account> data = accountRepository.findAll();
			for (Account r : data) {
				log.info(r.toString());
				log.info("");
			}
		};
	}
}
