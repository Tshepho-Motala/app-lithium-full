package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.ArrayList;
import java.util.List;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

	List<Account> findByOwner(User owner);
	ArrayList<Account> findByOwnerAndDomainAndCurrencyAndAccountType(User owner, Domain domain, Currency currency, AccountType accountType);
	Account findByOwnerAndDomainAndCurrencyAndAccountCodeAndAccountType(User owner, Domain domain, Currency currency, AccountCode code, AccountType accountType);

	List<Account> findByOwnerGuidAndDomainNameAndCurrencyCodeAndAccountCodeCodeInAndAccountTypeCode(String ownerGuid, String domainName, String currencyCode, String[] accountCodes, String accountTypeCode);
	List<Account> findByDomainAndAccountCodeAndCurrency(Domain domainName, AccountCode accountCode, Currency currency);
	ArrayList<Account> findByOwnerAndDomainAndCurrencyAndAccountCode(User owner, Domain domain, Currency currency, AccountCode accountCode);
	/**
	 * This needs to be a PESSIMISTIC lock to ensure that same accounts for the same player will be locked,
	 * thus updating on concurrent requests will wait (for the same account for the same player)
	 *
	 * @param id - the account that needs to be locked.
	 * @return Account
	 */
	@Query("select o from #{#entityName} o where o.id = :id")
	@Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
	Account findForUpdate(@Param("id") Long id);

	default Account findOne(Long id) {
		return findById(id).orElse(null);
	}
}
