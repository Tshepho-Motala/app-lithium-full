package lithium.service.accounting.provider.internal.services;

import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.BalanceLimit;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.repositories.BalanceLimitRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BalanceLimitService {
	@Autowired AccountService accountService;
	@Autowired TransactionTypeRepository transactionTypeRepository;
	@Autowired BalanceLimitRepository balanceLimitRepository;

	public BalanceLimit createOrUpdate(
		String domainName,
		String playerGuid,
		Long amountCents,
		String currencyCode,
		String accountCode,
		String accountTypeCode,
		String transactionTypeCode,
		String contraAccountCode,
		String contraAccountTypeCode
	) {
		log.debug("BalanceLimit createOrUpdate :: "+playerGuid+" "+amountCents+" "+currencyCode+" "+accountCode+" "+accountTypeCode+" "+transactionTypeCode+" "+contraAccountCode+" "+contraAccountTypeCode+" ");
		Account account = accountService.findOrCreate(accountCode, accountTypeCode, currencyCode, domainName, playerGuid);
		Account contraAccount = accountService.findOrCreate(contraAccountCode, contraAccountTypeCode, currencyCode, domainName, playerGuid);

		TransactionType transactionType = transactionTypeRepository.findByCode(transactionTypeCode);

		BalanceLimit limit = find(playerGuid, accountCode, accountTypeCode);
		if (limit == null) {
			limit = BalanceLimit.builder()
				.account(account)
				.contraAccount(contraAccount)
				.transactionTypeTo(transactionType)
				.balanceCents(amountCents)
				.build();
		} else {
			limit.setBalanceCents(amountCents);
		}
		limit = balanceLimitRepository.save(limit);
		return limit;
	}

	public BalanceLimit find(
		String playerGuid,
		String accountCode,
		String accountTypeCode
	) {
		log.debug("BalanceLimit find :: "+playerGuid+" "+accountCode+" "+accountTypeCode);
		BalanceLimit balanceLimit = balanceLimitRepository.findByAccountOwnerGuidAndAccountAccountCodeCodeAndAccountAccountTypeCode(playerGuid, accountCode, accountTypeCode);
		return balanceLimit;
	}
}
