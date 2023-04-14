package lithium.service.accounting.provider.internal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.data.repositories.AccountTypeRepository;

@RestController
@RequestMapping("/accounttype")
public class AccountTypeController {
	@Autowired AccountTypeRepository accountTypeRepository;
	
	@RequestMapping("/create")
	public ResponseEntity<AccountType> createAccountType(String code) {
		AccountType a = AccountType.builder()
				.code(code)
				.build();
		accountTypeRepository.save(a);
		return new ResponseEntity<AccountType>(a, HttpStatus.OK);
	}
}
