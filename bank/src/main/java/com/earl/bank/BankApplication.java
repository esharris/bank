package com.earl.bank;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.earl.bank.controller.RelationshipManager;
import com.earl.bank.dto.BankAccountInput;
import com.earl.bank.dto.CustomerAccountPair;
import com.earl.bank.dto.CustomerInput;
import com.earl.bank.dto.LinkInput;
import com.earl.bank.dtofactories.BankAccountInputFactory;
import com.earl.bank.dtofactories.CustomerInputFactory;
import com.earl.bank.dtofactories.LinkInputFactory;
import com.earl.bank.entities.Account;
import com.earl.bank.entities.Customer;
import com.earl.bank.entityfactories.CheckingAccountFactory;
import com.earl.bank.entityfactories.CustomerFactory;
import com.earl.bank.entityfactories.SavingsAccountFactory;
import com.earl.bank.jpa.AccountNotFoundException;
import com.earl.bank.jpa.AccountRepository;
import com.earl.bank.jpa.AccountRepositoryHelper;
import com.earl.bank.jpa.CustomerNotFoundException;
import com.earl.bank.jpa.CustomerRepository;
import com.earl.bank.jpa.CustomerRepositoryHelper;

@SpringBootApplication
public class BankApplication {

	private static final Logger log = LoggerFactory.getLogger(BankApplication.class);

	private final BankAccountInputFactory bankAccountInputFactory;

	private final CustomerInputFactory customerInputFactory;

	private final LinkInputFactory linkInputFactory;

	private final CheckingAccountFactory checkingAccountFactory;

	private final SavingsAccountFactory savingsAccountFactory;

	private final CustomerFactory customerFactory;

	private final AccountRepository accountRepository;

	private final CustomerRepository customerRepository;

	private final RelationshipManager relationshipManager;

	@Autowired
	public BankApplication(BankAccountInputFactory bankAccountInputFactory, CustomerInputFactory customerInputFactory,
			LinkInputFactory linkInputFactory, CheckingAccountFactory checkingAccountFactory,
			SavingsAccountFactory savingsAccountFactory, CustomerFactory customerFactory,
			AccountRepository accountRepository, CustomerRepository customerRepository,
			RelationshipManager relationshipManager) {
		this.bankAccountInputFactory = bankAccountInputFactory;
		this.customerInputFactory = customerInputFactory;
		this.linkInputFactory = linkInputFactory;
		this.savingsAccountFactory = savingsAccountFactory;
		this.checkingAccountFactory = checkingAccountFactory;
		this.customerFactory = customerFactory;
		this.accountRepository = accountRepository;
		this.customerRepository = customerRepository;
		this.relationshipManager = relationshipManager;
	}

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

	@Bean
	CommandLineRunner demo() {
		return args -> {
			final String accountFileName = "data/NewBankAccounts.csv";
			final String customerFileName = "data/NewBankCustomers.csv";
			final String linksFileName = "data/Links.csv";
			/**
			 * The maps are a workaround for a problem with the find methods producing lazy
			 * sets that can't be initialized. Add fetch=fetchType=Eager to Customer and
			 * Account fixes the problem. But this makes things less efficient.
			 */
			Map<String, Account> accountNumberToAccount = new HashMap<>();
			try {
				try (BufferedReader br = new BufferedReader(new FileReader(accountFileName))) {
					String dataRow;
					while ((dataRow = br.readLine()) != null) {
						BankAccountInput bankAccountInput = bankAccountInputFactory.create(dataRow);
						String accountNumber = bankAccountInput.accountNumber();
						if ("Checking".equals(bankAccountInput.accountType())) {
							accountNumberToAccount.put(accountNumber, AccountRepositoryHelper.saveIfAccountNumberUnique(
									accountRepository,
									checkingAccountFactory.create(accountNumber, bankAccountInput.initDeposit())));
						} else if ("Savings".equals(bankAccountInput.accountType())) {
							accountNumberToAccount.put(accountNumber, AccountRepositoryHelper.saveIfAccountNumberUnique(
									accountRepository,
									savingsAccountFactory.create(accountNumber, bankAccountInput.initDeposit())));
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
			Map<String, Customer> socialSecurtyNumberToCustomer = new HashMap<>();
			try {
				try (BufferedReader br = new BufferedReader(new FileReader(customerFileName))) {
					String dataRow;
					while ((dataRow = br.readLine()) != null) {
						CustomerInput customerInput = customerInputFactory.create(dataRow);
						String socialSecurityNumber = customerInput.socialSecurityNumber();
						socialSecurtyNumberToCustomer.put(socialSecurityNumber,
								CustomerRepositoryHelper.saveIfSocialSecurityNumberUnique(customerRepository,
										customerFactory.create(socialSecurityNumber, customerInput.firstName(),
												customerInput.lastName())));
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
			try {
				try (BufferedReader br = new BufferedReader(new FileReader(linksFileName))) {
					String dataRow;
					while ((dataRow = br.readLine()) != null) {
						LinkInput linkInput = linkInputFactory.create(dataRow);
						String socialSecurityNumber = linkInput.socialSecurityNumber();
						Customer customer;
						if (socialSecurtyNumberToCustomer.containsKey(socialSecurityNumber)) {
							customer = socialSecurtyNumberToCustomer.get(socialSecurityNumber)
//								CustomerRepositoryHelper.getCustomer(customerRepository,
//								socialSecurityNumber)
							;
						} else {
							throw new CustomerNotFoundException(socialSecurityNumber);
						}

						String accountNumber = linkInput.accountNumber();
						Account account;
						if (accountNumberToAccount.containsKey(accountNumber)) {
							account = accountNumberToAccount.get(accountNumber)
//								AccountRepositoryHelper.getAccount(accountRepository,
//								accountNumber)
							;
						} else {
							throw new AccountNotFoundException(accountNumber);
						}
						log.info(customer.toString());
						log.info(account.toString());
						CustomerAccountPair customerAccountPair = relationshipManager.addAccount(customer, account);
						socialSecurtyNumberToCustomer.put(socialSecurityNumber, customerAccountPair.customer());
						accountNumberToAccount.put(accountNumber, customerAccountPair.account());
					}
				}
			} catch (FileNotFoundException e) {
				log.error("Couldn't find links file to open: " + linksFileName);
			} catch (IOException e) {
				log.error("Couldn't read customer file");
			}
		};
	}
}
