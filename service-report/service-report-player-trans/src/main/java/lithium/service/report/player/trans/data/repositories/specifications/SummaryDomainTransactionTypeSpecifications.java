package lithium.service.report.player.trans.data.repositories.specifications;

public class SummaryDomainTransactionTypeSpecifications {

//	public static Specification<SummaryDomainTransactionType> find(
//		final String domainName,
//		final String currencyCode,
//		final String accountCode, 
//		final String transactionTypeCode,
//		final int granularity,
//		final Date dateStart,
//		final Date dateEnd
//	) {
//		return new Specification<SummaryDomainTransactionType>() {
//			@Override
//			public Predicate toPredicate(Root<SummaryDomainTransactionType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Join<SummaryDomainTransactionType, Period> joinPeriod = root.join(SummaryDomainTransactionType_.period, JoinType.INNER);
//				Join<Period, Domain> joinDomain = joinPeriod.join(Period_.domain, JoinType.INNER);
//				Join<SummaryDomainTransactionType, Currency> joinCurrency = root.join(SummaryDomainTransactionType_.currency, JoinType.INNER);
//				Join<SummaryDomainTransactionType, AccountCode> joinAccountCode = root.join(SummaryDomainTransactionType_.accountCode, JoinType.INNER);
//				Join<SummaryDomainTransactionType, TransactionType> joinTransactionType = root.join(SummaryDomainTransactionType_.transactionType, JoinType.INNER);
//				
//				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
//				p = cb.and(p, cb.equal(joinCurrency.get(Currency_.code), currencyCode));
//				p = cb.and(p, cb.equal(joinAccountCode.get(AccountCode_.code), accountCode));
//				p = cb.and(p, cb.equal(joinTransactionType.get(TransactionType_.code), transactionTypeCode));
//				p = cb.and(p, cb.equal(joinPeriod.get(Period_.granularity), granularity));
//				p = cb.and(p, cb.greaterThanOrEqualTo(joinPeriod.get(Period_.dateStart), dateStart));
//				p = cb.and(p, cb.lessThanOrEqualTo(joinPeriod.get(Period_.dateEnd), dateEnd));
//				
//				return p;
//			}
//		};
//	}
}