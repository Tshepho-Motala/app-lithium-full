package lithium.service.accounting.provider.internal.data.repositories.specifications;

import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.AccountCode_;
import lithium.service.accounting.provider.internal.data.entities.Account_;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Currency_;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Domain_;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.Period_;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccount;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccount_;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class SummaryAccountSpecifications {
	public static Specification<SummaryAccount> find(
		final String domainName,
		final String currencyCode,
		final String accountCode, 
		final int granularity,
		final Date dateStart,
		final Date dateEnd,
		final String ownerGuid
	) {
		return new Specification<SummaryAccount>() {
			@Override
			public Predicate toPredicate(Root<SummaryAccount> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				String[] accountCodes = accountCode.split(",");

				Join<SummaryAccount, Period> joinPeriod = root.join(SummaryAccount_.period, JoinType.INNER);
				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
				Join<SummaryAccount, Account> joinAccount = root.join(SummaryAccount_.account, JoinType.INNER);
				Join<Account, User> joinUser = joinAccount.join(Account_.owner, JoinType.INNER);
				Join<Account, Currency> joinCurrency = joinAccount.join(Account_.currency, JoinType.INNER);
				Join<Account, AccountCode> joinAccountCode = joinAccount.join(Account_.accountCode, JoinType.INNER);
				
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinUser.get(User_.guid), ownerGuid));
				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
				p = cb.and(p, joinAccountCode.get(AccountCode_.code).in(accountCodes));
				p = cb.and(p, cb.equal(joinPeriod.get(Period_.granularity), granularity));
				p = cb.and(p, cb.greaterThanOrEqualTo(joinPeriod.get(Period_.dateStart), dateStart));
				p = cb.and(p, cb.lessThanOrEqualTo(joinPeriod.get(Period_.dateEnd), dateEnd));
				
				return p;
			}
		};
	}
}