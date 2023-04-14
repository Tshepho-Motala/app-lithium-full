package lithium.service.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.data.entities.TransactionTypeAccount;
import lithium.service.user.data.repositories.TransactionTypeAccountRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{domainName}/tranta")
public class TransactionTypeAccountController {
	@Autowired
	private TransactionTypeAccountRepository transactionTypeAccountRepository;
	
	@GetMapping("/all")
	public Response<Iterable<TransactionTypeAccount>> list() {
		log.debug("Listing all TransactionTypeAccounts");
		Iterable<TransactionTypeAccount> all = transactionTypeAccountRepository.findAll(Sort.by(new Sort.Order(Direction.ASC, "accountTypeCode"))); 
		return Response.<Iterable<TransactionTypeAccount>>builder().data(all).status(Status.OK).build();
	}
	
	@PostMapping("/addtranta/{accountTypeCode}/{debit}/{credit}")
	public Response<TransactionTypeAccount> addTransactionTypeAccount(
		@PathVariable("accountTypeCode") String accountTypeCode,
		@PathVariable("debit") Boolean debit,
		@PathVariable("credit") Boolean credit
	) throws InterruptedException {
		TransactionTypeAccount transactionTypeAccount = transactionTypeAccountRepository.findByAccountTypeCode(accountTypeCode);
		log.info("transactionTypeAccount : "+transactionTypeAccount);
		if (transactionTypeAccount == null) {
			log.info("Creating new TransactionTypeAccount : "+transactionTypeAccount);
			transactionTypeAccount = transactionTypeAccountRepository.save(
				TransactionTypeAccount.builder().accountTypeCode(accountTypeCode).debit(debit).credit(credit).build()
			);
		}
		return Response.<TransactionTypeAccount>builder().data(transactionTypeAccount).status(Status.OK).build();
	}
}
