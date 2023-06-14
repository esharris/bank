package com.earl.bank.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.earl.bank.dto.BankAccountInput;
import com.earl.bank.dto.CheckingAccountUpdateInput;
import com.earl.bank.dto.CustomerInput;
import com.earl.bank.dto.CustomerUpdateInput;
import com.earl.bank.dto.SavingsAccountUpdateInput;
import com.earl.bank.entities.Account;
import com.earl.bank.entities.CheckingAccount;
import com.earl.bank.entities.Customer;
import com.earl.bank.entities.SavingsAccount;
import com.earl.bank.entityfactories.CheckingAccountFactory;
import com.earl.bank.entityfactories.CustomerFactory;
import com.earl.bank.entityfactories.SavingsAccountFactory;
import com.earl.bank.jpa.AccountRepository;
import com.earl.bank.jpa.AccountRepositoryHelper;
import com.earl.bank.jpa.CustomerRepository;
import com.earl.bank.jpa.CustomerRepositoryHelper;

@RestController
public class BankController {

	private final AccountRepository accountRepository;

	private final RepresentationModelAssembler<Account, EntityModel<Account>> accountModelAssembler;

	private final CustomerRepository customerRepository;

	private final RepresentationModelAssembler<Customer, EntityModel<Customer>> customerModelAssembler;

	private final RelationshipManager relationshipManager;

	private final CheckingAccountFactory checkingAccountFactory;

	private final SavingsAccountFactory savingsAccountFactory;

	private final CustomerFactory customerFactory;

	@Autowired
	public BankController(AccountRepository accountRepository,
			RepresentationModelAssembler<Account, EntityModel<Account>> accountModelAssembler,
			CustomerRepository customerRepository,
			RepresentationModelAssembler<Customer, EntityModel<Customer>> customerModelAssembler,
			RelationshipManager relationshipManager, CheckingAccountFactory checkingAccountFactory,
			SavingsAccountFactory savingsAccountFactory, CustomerFactory customerFactory) {
		this.accountRepository = accountRepository;
		this.accountModelAssembler = accountModelAssembler;
		this.customerRepository = customerRepository;
		this.customerModelAssembler = customerModelAssembler;
		this.relationshipManager = relationshipManager;
		this.savingsAccountFactory = savingsAccountFactory;
		this.checkingAccountFactory = checkingAccountFactory;
		this.customerFactory = customerFactory;
	}

	@GetMapping("/accounts")
	public CollectionModel<EntityModel<Account>> retrieveAllAccounts() {
		List<EntityModel<Account>> accountList = accountRepository.findAll().stream()
				.map(accountModelAssembler::toModel).toList();
		return CollectionModel.of(accountList,
				linkTo(methodOn(BankController.class).retrieveAllAccounts()).withSelfRel());
	}

	@GetMapping("/customers")
	public CollectionModel<EntityModel<Customer>> retrieveAllCustomers() {
		List<EntityModel<Customer>> customerList = customerRepository.findAll().stream()
				.map(customerModelAssembler::toModel).toList();
		return CollectionModel.of(customerList,
				linkTo(methodOn(BankController.class).retrieveAllAccounts()).withSelfRel());
	}

	@GetMapping("/accounts/{accountNumber}")
	public EntityModel<Account> retrieveAccount(@PathVariable long accountNumber) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		return accountModelAssembler.toModel(account);
	}

	@GetMapping("/customers/{socialSecurityNumber}")
	public EntityModel<Customer> retrieveCustomer(@PathVariable String socialSecurityNumber) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		return customerModelAssembler.toModel(customer);
	}

	@GetMapping("/accounts/{accountNumber}/customers")
	public CollectionModel<EntityModel<Customer>> retrieveCustomersForAccount(@PathVariable long accountNumber) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		List<EntityModel<Customer>> customerList = account.getCustomerSet().stream()
				.map(customerModelAssembler::toModel).toList();
		return CollectionModel.of(customerList,
				linkTo(methodOn(BankController.class).retrieveCustomersForAccount(accountNumber)).withSelfRel());
	}

	@GetMapping("/customers/{socialSecurityNumber}/accounts")
	public CollectionModel<EntityModel<Account>> retrieveAccountsForCustomer(
			@PathVariable String socialSecurityNumber) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		List<EntityModel<Account>> accountList = customer.getAccountSet().stream().map(accountModelAssembler::toModel)
				.toList();
		return CollectionModel.of(accountList,
				linkTo(methodOn(BankController.class).retrieveAccountsForCustomer(socialSecurityNumber)).withSelfRel());
	}

	@PostMapping("/accounts")
	ResponseEntity<?> newAccount(@RequestBody BankAccountInput bankAccountInput) {
		Account savedAccount;
		if ("Checking".equals(bankAccountInput.accountType())) {
			savedAccount = AccountRepositoryHelper.saveIfAccountNumberUnique(accountRepository,
					checkingAccountFactory.create(bankAccountInput.accountNumber(), bankAccountInput.initDeposit()));
		} else if ("Savings".equals(bankAccountInput.accountType())) {
			savedAccount = AccountRepositoryHelper.saveIfAccountNumberUnique(accountRepository,
					savingsAccountFactory.create(bankAccountInput.accountNumber(), bankAccountInput.initDeposit()));
		} else {
			throw new UnknownAccountTypeException(bankAccountInput.accountType());
		}
		EntityModel<?> entityModel = accountModelAssembler.toModel(savedAccount);
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}

	@PostMapping("/customers")
	ResponseEntity<?> newCustomer(@RequestBody CustomerInput customerInput) {
		Customer savedCustomer = CustomerRepositoryHelper.saveIfSocialSecurityNumberUnique(customerRepository,
				customerFactory.create(customerInput.socialSecurityNumber(), customerInput.firstName(),
						customerInput.lastName()));
		EntityModel<?> entityModel = customerModelAssembler.toModel(savedCustomer);
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}

	@PostMapping("accounts/{accountNumber}/customers/{socialSecurityNumber}")
	public ResponseEntity<Account> addStudent(@PathVariable long accountNumber,
			@PathVariable String socialSecurityNumber) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		relationshipManager.addCustomer(account, customer);
		String locationString = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUriString();
		int i = locationString.lastIndexOf("/");
		URI location = URI.create(locationString.substring(0, i));
		return ResponseEntity.created(location).build();
	}

	@PostMapping("/customers/{socialSecurityNumber}/accounts/{accountNumber}")
	public ResponseEntity<Customer> addAccount(@PathVariable String socialSecurityNumber,
			@PathVariable long accountNumber) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		relationshipManager.addAccount(customer, account);
		String locationString = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUriString();
		int i = locationString.lastIndexOf("/");
		URI location = URI.create(locationString.substring(0, i));
		return ResponseEntity.created(location).build();
	}

	/**
	 * The user can’t modify the index or the account number, because they uniquely
	 * identity the account.
	 * 
	 * Why not let this method update the debitCardNumber? Because, this component
	 * has to sync with the CheckingAccountFactory counter to be unique.
	 * 
	 * @param accountNumber
	 * @param checkingAccountUpdateInput
	 * @return
	 */
	@PutMapping("accounts/{accountNumber}/checking")
	public ResponseEntity<Account> replaceCheckingAccount(@PathVariable long accountNumber,
			@RequestBody CheckingAccountUpdateInput checkingAccountUpdateInput) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		if (account instanceof CheckingAccount) {
			CheckingAccount checkingAccount = (CheckingAccount) account;
			checkingAccount.setBalance(checkingAccountUpdateInput.balance());
			checkingAccount.setRate(checkingAccountUpdateInput.rate());
			checkingAccount.setDebitCardPIN(checkingAccountUpdateInput.debitCardPIN());
			accountRepository.save(checkingAccount);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
			return ResponseEntity.created(location).build();
		} else {
			throw new AccountMismatchException("checking", account.getAccountNumber());
		}
	}

	/**
	 * The user can’t modify the index or the account number, because they uniquely
	 * identity the account.
	 * 
	 * @param accountNumber
	 * @param savingsAccountUpdateInput
	 * @return
	 */
	@PutMapping("accounts/{accountNumber}/savings")
	public ResponseEntity<Account> replaceSavingsAccount(@PathVariable long accountNumber,
			@RequestBody SavingsAccountUpdateInput savingsAccountUpdateInput) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		if (account instanceof SavingsAccount) {
			SavingsAccount savingsAccount = (SavingsAccount) account;
			savingsAccount.setBalance(savingsAccountUpdateInput.balance());
			savingsAccount.setRate(savingsAccountUpdateInput.rate());
			savingsAccount.setSafetyDepositBoxId(savingsAccountUpdateInput.safetyDepositBoxId());
			savingsAccount.setSafetyDepositBoxKey(savingsAccountUpdateInput.safetyDepositBoxKey());
			accountRepository.save(savingsAccount);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
			return ResponseEntity.created(location).build();
		} else {
			throw new AccountMismatchException("savings", account.getAccountNumber());
		}
	}

	/**
	 * The user can’t modify the index or the social security number, because they
	 * uniquely identify the Customer.
	 * 
	 * @param socialSecurityNumber
	 * @param customerUpdateInput
	 * @return
	 */
	@PutMapping("/customers/{socialSecurityNumber}")
	public ResponseEntity<Customer> replaceCustomer(@PathVariable String socialSecurityNumber,
			@RequestBody CustomerUpdateInput customerUpdateInput) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		customer.setFirstName(customerUpdateInput.firstName());
		customer.setLastName(customerUpdateInput.lastName());
		customerRepository.save(customer);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
		return ResponseEntity.created(location).build();
	}

	@PatchMapping("accounts/{accountNumber}/checking/debitcardnumber")
	public ResponseEntity<Account> replaceCheckingAccountDebitCardNumber(@PathVariable long accountNumber) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		if (account instanceof CheckingAccount) {
			CheckingAccount checkingAccount = (CheckingAccount) account;
			checkingAccount.setDebitCardNumber(checkingAccountFactory.generateUniqueDebitCardNumber());
			accountRepository.save(checkingAccount);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
			return ResponseEntity.created(location).build();
		} else {
			throw new AccountMismatchException("checking", account.getAccountNumber());
		}
	}

	/**
	 * The user can’t modify the index or the account number, because they uniquely
	 * identity the account.
	 * 
	 * @param accountNumber
	 * @param savingsAccountUpdateInput
	 * @return
	 */
	@PatchMapping("accounts/{accountNumber}/savings")
	public ResponseEntity<Account> partiallyReplaceSavingsAccount(@PathVariable long accountNumber,
			@RequestBody SavingsAccountUpdateInput savingsAccountUpdateInput) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		if (account instanceof SavingsAccount) {
			SavingsAccount savingsAccount = (SavingsAccount) account;
			if (savingsAccountUpdateInput.balance() != null) {
				savingsAccount.setBalance(savingsAccountUpdateInput.balance());
			}
			if (savingsAccountUpdateInput.rate() != null) {
				savingsAccount.setRate(savingsAccountUpdateInput.rate());
			}
			if (savingsAccountUpdateInput.safetyDepositBoxId() != null) {
				savingsAccount.setSafetyDepositBoxId(savingsAccountUpdateInput.safetyDepositBoxId());
			}
			if (savingsAccountUpdateInput.safetyDepositBoxKey() != null) {
				savingsAccount.setSafetyDepositBoxKey(savingsAccountUpdateInput.safetyDepositBoxKey());
			}
			accountRepository.save(savingsAccount);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
			return ResponseEntity.created(location).build();
		} else {
			throw new AccountMismatchException("savings", account.getAccountNumber());
		}
	}

	/**
	 * The user can’t modify the index or the account number, because they uniquely
	 * identity the account.
	 * 
	 * Why not let this method update the debitCardNumber? Because, this component
	 * has to sync with the CheckingAccountFactory counter to be unique.
	 * 
	 * @param accountNumber
	 * @param checkingAccountUpdateInput
	 * @return
	 */
	@PatchMapping("accounts/{accountNumber}/checking")
	public ResponseEntity<Account> partiallyReplaceCheckingAccount(@PathVariable long accountNumber,
			@RequestBody CheckingAccountUpdateInput checkingAccountUpdateInput) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		if (account instanceof CheckingAccount) {
			CheckingAccount checkingAccount = (CheckingAccount) account;
			if (checkingAccountUpdateInput.balance() != null) {
				checkingAccount.setBalance(checkingAccountUpdateInput.balance());
			}
			if (checkingAccountUpdateInput.rate() != null) {
				checkingAccount.setRate(checkingAccountUpdateInput.rate());
			}
			if (checkingAccountUpdateInput.debitCardPIN() != null) {
				checkingAccount.setDebitCardPIN(checkingAccountUpdateInput.debitCardPIN());
			}
			accountRepository.save(checkingAccount);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
			return ResponseEntity.created(location).build();
		} else {
			throw new AccountMismatchException("checking", account.getAccountNumber());
		}
	}

	/**
	 * The user can’t modify the index or the social security number, because they
	 * uniquely identify the Customer.
	 * 
	 * @param socialSecurityNumber
	 * @param customerUpdateInput
	 * @return
	 */
	@PatchMapping("/customers/{socialSecurityNumber}")
	public ResponseEntity<Customer> partiallyReplaceCustomer(@PathVariable String socialSecurityNumber,
			@RequestBody CustomerUpdateInput customerUpdateInput) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		if (customerUpdateInput.firstName() != null) {
			customer.setFirstName(customerUpdateInput.firstName());
		}
		if (customerUpdateInput.lastName() != null) {
			customer.setLastName(customerUpdateInput.lastName());
		}
		customerRepository.save(customer);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/accounts/{accountNumber}")
	public ResponseEntity<Account> deleteAccoount(@PathVariable long accountNumber) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		relationshipManager.removeAccount(account);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/customers/{socialSecurityNumber}")
	public ResponseEntity<Customer> deleteCustomer(@PathVariable String socialSecurityNumber) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		relationshipManager.removeCustomer(customer);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/customers/{socialSecurityNumber}/accounts/{accountNumber}")
	public ResponseEntity<Customer> removeAccount(@PathVariable String socialSecurityNumber,
			@PathVariable long accountNumber) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);

		relationshipManager.removeAccount(customer, account);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("accounts/{accountNumber}/customers/{socialSecurityNumber}")
	public ResponseEntity<Account> removeCustomer(@PathVariable long accountNumber,
			@PathVariable String socialSecurityNumber) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);

		relationshipManager.removeCustomer(account, customer);
		return ResponseEntity.noContent().build();
	}

}
