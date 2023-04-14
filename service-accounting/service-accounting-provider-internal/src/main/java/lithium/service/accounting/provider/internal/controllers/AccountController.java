package lithium.service.accounting.provider.internal.controllers;

import lithium.service.accounting.provider.internal.services.AccountCodeService;
import lithium.service.accounting.provider.internal.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.AccountTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.CurrencyRepository;
import lithium.service.accounting.provider.internal.data.repositories.DomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.UserRepository;

@RestController
@RequestMapping("/account")
public class AccountController {
	@Autowired AccountTypeRepository accountTypeRepository;
	@Autowired AccountService accountService;
	@Autowired DomainRepository domainRepository;
	@Autowired UserRepository userRepository;
	@Autowired CurrencyRepository currencyRepository;
	@Autowired AccountCodeService accountCodeService;
	
	@Retryable
	@RequestMapping("/findorcreate")
	public ResponseEntity<Account> findOrCreate(
		String accountCode,
		String accountTypeCode,
		String currencyCode,
		String domainName,
		String ownerGuid
	) {
		Account account = accountService.findOrCreate(
			accountCode,
			accountTypeCode,
			currencyCode,
			domainName,
			ownerGuid
		);
		return new ResponseEntity<>(account, HttpStatus.OK);
	}
	
	@RequestMapping("/find")
	public ResponseEntity<Account> find(
		@RequestParam String code,
		@RequestParam String accountTypeCode,
		@RequestParam String currencyCode,
		@RequestParam String domainName,
		@RequestParam String ownerGuid
	) {
		Account a = accountService.find(code, accountTypeCode, currencyCode, domainName, ownerGuid);
		if (a == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<>(a, HttpStatus.OK);
	}

}
