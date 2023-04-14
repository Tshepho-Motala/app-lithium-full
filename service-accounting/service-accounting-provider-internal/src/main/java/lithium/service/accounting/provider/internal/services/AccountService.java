package lithium.service.accounting.provider.internal.services;

import lithium.leader.LeaderCandidate;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.UserRepository;
import lithium.service.user.client.objects.UserAttributesData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class AccountService {
	@Autowired AccountTypeService accountTypeService;
	@Autowired AccountRepository accountRepository;
	@Autowired DomainService domainService;
	@Autowired UserRepository userRepository;
	@Autowired AccountCodeService accountCodeService;
	@Autowired CurrencyService currencyService;
	@Autowired LeaderCandidate leaderCandidate;

	public User findOrCreateUser(String guid) {
		User owner = userRepository.findByGuid(guid);
		if (owner == null) {
			owner = userRepository.save(User.builder().guid(guid).build());
		}
		return owner;
	}

	@Retryable
	public Account findOrCreate(
		String accountCode,
		String accountTypeCode,
		String currencyCode,
		String domainName,
		String ownerGuid
	) {
		return findOrCreate(accountCode, accountTypeCode, currencyCode, domainName, ownerGuid, null);
	}

	@Retryable
	public Account findOrCreate(
			String accountCode,
			String accountTypeCode,
			String currencyCode,
			String domainName,
			String ownerGuid,
			TransactionType transactionType
	) {
		AccountCode code = accountCodeService.findOrCreate(accountCode);

		AccountType accountType = accountTypeService.findOrCreate(accountTypeCode, transactionType);

		User owner = findOrCreateUser(ownerGuid);
		Domain domain = domainService.findOrCreate(domainName);
		Currency currency = currencyService.findByCode(currencyCode);

		Account a = accountRepository.findByOwnerAndDomainAndCurrencyAndAccountCodeAndAccountType(owner, domain, currency, code, accountType);
		if (a == null) {
			a = accountRepository.save(
					Account.builder()
							.accountType(accountType)
							.accountCode(code)
							.currency(currency)
							.domain(domain)
							.owner(owner)
							.balanceCents(0L)
							.build()
			);
		} else {
			a.setBalanceCents(a.getBalanceCents());
			a = accountRepository.save(a);
		}

		return a;
	}

	/**
	 * Select the account for update, enforcing the PESIMISTIC lock.
	 *
	 * @param accountId - Account to be locked.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public Account lockingUpdate(Long accountId) {
		try {
			Account account = accountRepository.findForUpdate(accountId);
			log.trace("Acquired lock on accountId: {}, account: {}", accountId, account);
			return account;
		} catch (Exception e) {
			log.error("Failed to acquire lock on accountId: {} | {}", accountId, e.getMessage(), e);
			Account account = accountRepository.findOne(accountId);
			log.trace("Account from DB: {}", account);
			throw e;
		}
	}

	public Account find(
		String code,
		String accountTypeCode,
		String currencyCode,
		String domainName,
		String ownerGuid
	) {
		AccountCode accountCode = accountCodeService.findOrCreate(code);
		if (accountCode == null) return null;

		AccountType accountType = accountTypeService.findByCode(accountTypeCode);
		if (accountType == null) return null;

		User owner = userRepository.findByGuid(ownerGuid);
		if (owner == null) return null;

		Domain domain = domainService.findOrCreate(domainName);
		if (domain == null) return null;

		Currency currency = currencyService.findByCode(currencyCode);
		if (currency == null) return null;

		Account a = accountRepository.findByOwnerAndDomainAndCurrencyAndAccountCodeAndAccountType(owner, domain, currency, accountCode, accountType);
		if (a == null) return null;

		return a;
	}

	public void processUserAttributesData(UserAttributesData data) {
		User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
				.orElse(User.builder().guid(data.getGuid()).build());
		user.setTestAccount(data.isTestAccount());
		userRepository.save(user);
	}
}
