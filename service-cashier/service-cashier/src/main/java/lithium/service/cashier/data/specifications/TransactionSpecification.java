package lithium.service.cashier.data.specifications;

import lithium.jpa.util.QueryWithLogicalOperatorsUtil;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor_;
import lithium.service.cashier.data.entities.DomainMethod_;
import lithium.service.cashier.data.entities.ProcessorAccountStatus;
import lithium.service.cashier.data.entities.ProcessorAccountStatus_;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.ProcessorUserCard_;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionPaymentType;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.TransactionStatus_;
import lithium.service.cashier.data.entities.TransactionTag;
import lithium.service.cashier.data.entities.TransactionTag_;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory_;
import lithium.service.cashier.data.entities.Transaction_;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.entities.UserCategory;
import lithium.service.cashier.data.entities.UserCategory_;
import lithium.service.cashier.data.entities.User_;
import lithium.specification.JoinableSpecification;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.List;
import java.util.Set;

import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getFormattedQueryArray;
import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getAmountInCentsValue;
import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getQueryConditionValue;


@Slf4j
public class TransactionSpecification {

    private final static long DAY_IN_SECONDS = 86400;

	public static Specification<Transaction> userFrontendTable(final User user, final Domain domain) {
		return new JoinableSpecification<Transaction>() {
			@Override
			public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(
					cb.equal(root.get(Transaction_.user), user),
					cb.isTrue(this.joinList(root, Transaction_.domainMethod, JoinType.LEFT).get(DomainMethod_.domain).in(domain))
				);
			}
		};
	}

	public static Specification<Transaction> userFrontendTableByType(
		final User user,
		final Domain domain,
		final TransactionType transactionType,
		final TransactionStatus transactionStatus,
		final DateTime startDate,
		final DateTime endDate
	) {
		return new JoinableSpecification<Transaction>() {
			@Override
			public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.conjunction();

				p = cb.and(p,
					cb.equal(root.get(Transaction_.user), user),
					cb.isTrue(this.joinList(root, Transaction_.domainMethod, JoinType.LEFT).get(DomainMethod_.domain).in(domain))
				);

				if (transactionType!=null) p = cb.and(p, cb.equal(root.get(Transaction_.transactionType), transactionType));
				if (transactionStatus != null) {
					p = cb.and(p, cb.equal(root.get(Transaction_.status), transactionStatus));
				}
				if ((startDate != null) && (endDate != null)) {
					log.trace("Date Range: "+startDate.toDate()+" - "+endDate.toDate());
					p = cb.and(p, cb.between(root.get(Transaction_.createdOn), startDate.toDate(), endDate.toDate()));
				}
				return p;
			}
		};
	}
	
	public static Specification<Transaction> table(
			final List<DomainMethod> domainMethods,
			final User user,
			final List<TransactionStatus> transactionStatuses,
			final String search,
			final DateTime createdStartDate,
			final DateTime createdEndDate,
			final DateTime updatedStartDate,
			final DateTime updatedEndDate,
			final DateTime registrationStartDate,
			final DateTime registrationEndDate,
			final String processorReference,
			final String additionalReference,
			final TransactionPaymentType transactionPaymentType,
			final String declineReason,
			final String lastFourDigits,
			final String transactionId,
			final Set<TransactionTagType> includedTagTypes,
			final Set<TransactionTagType> excludedTagTypes,
			final Boolean isTestAccount,
			final String transactionRuntimeQuery,
            final String depositCountQuery,
            final String daysSinceFirstDepositQuery,
            final String transactionAmount,
            final String activePaymentMethodCount,
            final List<Long> userStatusIds,
            final List<Long> userTagIds
            ) {
		return new JoinableSpecification<Transaction>() {
			@Override
			public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				log.debug("domainMethods:"+domainMethods+" user:"+user+" transactionStatuses:"+transactionStatuses+" processorReference:"+processorReference);
				Predicate p = cb.conjunction();
				if (!domainMethods.isEmpty()) p = cb.and(p, cb.isTrue(root.get(Transaction_.domainMethod).in(domainMethods)));
				if (user != null) p = cb.and(p, cb.equal(root.get(Transaction_.user), user));
				if (transactionStatuses != null && !transactionStatuses.isEmpty())
					p = cb.and(p, cb.isTrue(root.get(Transaction_.status).in(transactionStatuses)));
				if (processorReference != null)
					p = cb.and(p, cb.equal(cb.lower(root.get(Transaction_.processorReference)), processorReference.toLowerCase()));
				if (additionalReference != null)
					p = cb.and(p, cb.equal(cb.lower(root.get(Transaction_.additionalReference)), additionalReference.toLowerCase()));
				if (transactionPaymentType != null)
					p = cb.and(p, cb.equal(root.get(Transaction_.transactionPaymentType), transactionPaymentType));
				if (declineReason != null) p = cb.and(p, cb.equal(root.get(Transaction_.declineReason), declineReason));
				if (lastFourDigits != null)
					p = cb.and(p, cb.equal(root.join(Transaction_.paymentMethod, JoinType.INNER).get(ProcessorUserCard_.lastFourDigits), lastFourDigits));
				if (transactionId != null)
					p = cb.and(p, cb.like(root.get(Transaction_.id).as(String.class), transactionId +"%"));
				if ((search != null) && (!search.isEmpty())) {
					p = cb.and(p,
							cb.or(
									cb.like(cb.lower(root.get(Transaction_.accountInfo)), search.toLowerCase() + "%"),
									cb.like(cb.lower(root.get(Transaction_.bonusCode)), search.toLowerCase() + "%"),
									cb.like(cb.lower(root.get(Transaction_.additionalReference)), search.toLowerCase() + "%"),
									cb.like(cb.lower(this.joinList(root, Transaction_.user, JoinType.LEFT).get(User_.guid)), "%" + search.toLowerCase() + "%"),
							cb.like(cb.lower(this.joinList(root, Transaction_.domainMethod, JoinType.LEFT).get(DomainMethod_.name)), "%"+search.toLowerCase()+"%"),
							cb.like(cb.lower(this.joinList(root, Transaction_.current, JoinType.LEFT).get(TransactionWorkflowHistory_.processor).get(DomainMethodProcessor_.description)), "%"+search.toLowerCase()+"%"),
							cb.like(cb.lower(this.joinList(root, Transaction_.current, JoinType.LEFT).get(TransactionWorkflowHistory_.status).get(TransactionStatus_.code)), search.toLowerCase()+"%"),
							cb.like(root.get(Transaction_.id).as(String.class), search.toLowerCase()+"%"),
							cb.like(root.get(Transaction_.processorReference).as(String.class), search.toLowerCase()+"%"),
							cb.like(root.join(Transaction_.paymentMethod, JoinType.INNER).get(ProcessorUserCard_.lastFourDigits).as(String.class), search.toLowerCase()+"%")
						)
					);
				}
				if(createdStartDate != null) {
					p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Transaction_.createdOn), createdStartDate.toDate()));
				}
				if(createdEndDate != null) {
					p = cb.and(p, cb.lessThan(root.get(Transaction_.createdOn), createdEndDate.toDate()));
				}
				if (updatedStartDate != null) {
					p = cb.and(p, cb.greaterThanOrEqualTo(this.joinList(root, Transaction_.current, JoinType.INNER)
							.get(TransactionWorkflowHistory_.timestamp), updatedStartDate.toDate()));
				}
				if (updatedEndDate != null) {
					p = cb.and(p, cb.lessThan(this.joinList(root, Transaction_.current, JoinType.INNER)
							.get(TransactionWorkflowHistory_.timestamp), updatedEndDate.toDate()));
				}
				if (registrationStartDate != null) {
					p = cb.and(p, cb.greaterThanOrEqualTo(this.joinList(root, Transaction_.user, JoinType.INNER)
							.get(User_.createdDate), registrationStartDate.toDate()));
				}
				if (registrationEndDate != null) {
					p = cb.and(p, cb.lessThan(this.joinList(root, Transaction_.user, JoinType.INNER)
							.get(User_.createdDate), registrationEndDate.toDate()));
				}
				if (isTestAccount != null) {
					Join<Transaction, User> userJoin = root.join(Transaction_.user, JoinType.INNER);
					p = cb.and(p, cb.equal(userJoin.get(User_.testAccount), isTestAccount));
				}
				if (includedTagTypes != null && includedTagTypes.size() > 0) {
					for (TransactionTagType tagType : includedTagTypes) {
						p = addIncludedTransactionTag(root, query, cb, p, tagType);
					}
					query.distinct(true);
				}
				if (excludedTagTypes != null && excludedTagTypes.size() > 0) {
					for (TransactionTagType tagType : excludedTagTypes) {
						p = addExcludedTransactionTag(root, query, cb, p, tagType);
					}
				}
				if (transactionRuntimeQuery != null) {
					Expression<Long> runtime = cb.function("round", Long.class, cb.diff(cb.function("unix_timestamp", Long.class, root.join(Transaction_.current).get(TransactionWorkflowHistory_.timestamp)), cb.function("unix_timestamp", Long.class, root.get(Transaction_.createdOn))));
					p = cb.and(p, QueryWithLogicalOperatorsUtil.resolveConditionalExpressionLong(cb, runtime, transactionRuntimeQuery));
				}

                if (depositCountQuery != null) {
                      p = addDepositCountLimitPredicate(root, query, cb, p, depositCountQuery);
                }

                if (daysSinceFirstDepositQuery != null) {
                    p = addDaysSinceFirstDepositLimitPredicate(root, query, cb, p, daysSinceFirstDepositQuery);
                }

                if (transactionAmount != null) {
                    p = addTransactionAmountPredicate(root, query, cb, p, transactionAmount);
                }

                if (activePaymentMethodCount != null) {
                    p = addActivePaymentMethodCountPredicate(root, query, cb, p, activePaymentMethodCount);
                }

                if (userStatusIds != null && !userStatusIds.isEmpty()) {
                    p = addUserStatusPredicate(root, cb, p, userStatusIds);
                }

                if (userTagIds != null && !userTagIds.isEmpty()) {
                    p = addUserTagsPredicate(root, query, cb, p, userTagIds);
                }

				return p;
			}
		};
	}

    private static Predicate addExcludedTransactionTag(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb, Predicate p, TransactionTagType excludedTagType) {

		Subquery<TransactionTag> transactionTagSubquery = getSubqueryByTransactionTag(root, query, cb, excludedTagType);
		ListJoin<Transaction, TransactionTag> transactionTagJoinList = root.joinList(Transaction_.TAGS, JoinType.LEFT);

		p = cb.and(p, cb.or(
				transactionTagSubquery.getSelection().isNull(),
				transactionTagJoinList.isNull()
		));

		return p;
	}

	private static Predicate addIncludedTransactionTag(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb, Predicate p, TransactionTagType includedTagType) {

		Subquery<TransactionTag> transactionTagSubquery = getSubqueryByTransactionTag(root, query, cb, includedTagType);
		p = cb.and(p, transactionTagSubquery.getSelection().isNotNull());

		return p;
	}

	private static Subquery<TransactionTag> getSubqueryByTransactionTag(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb, TransactionTagType tagType) {
		Subquery<TransactionTag> transactionTagSubquery = query.subquery(TransactionTag.class);
		Root<TransactionTag> transactionTagSubqueryRoot = transactionTagSubquery.from(TransactionTag.class);
		Join<TransactionTag, Transaction> reversTransactionJoin = transactionTagSubqueryRoot.join(TransactionTag_.transaction);

		Predicate subQueryP = cb.equal(reversTransactionJoin.get(Transaction_.ID), root.get(Transaction_.ID));

		subQueryP = cb.and(subQueryP, cb.equal(transactionTagSubqueryRoot.get(TransactionTag_.TYPE), tagType));
		transactionTagSubquery.select(transactionTagSubqueryRoot).where(subQueryP);

		return transactionTagSubquery;
	}

    private static Predicate addDepositCountLimitPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb, Predicate p, String depositCountQuery) {
        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<Transaction> subQueryRoot = subQuery.from(Transaction.class);
        Join<Transaction, User> userJoin = subQueryRoot.join(Transaction_.user, JoinType.INNER);
        Join<Transaction, TransactionStatus> statusJoin = subQueryRoot.join(Transaction_.status, JoinType.INNER);

        Predicate subQueryP = cb.equal(userJoin.get(User_.id), root.get(Transaction_.user).get(User_.ID));
        subQueryP = cb.and(subQueryP, cb.equal(userJoin.get(User_.id), subQueryRoot.get(Transaction_.user).get(User_.ID)));
        subQueryP = cb.and(subQueryP, cb.equal(statusJoin.get(TransactionStatus_.CODE), "SUCCESS"));
        subQueryP = cb.and(subQueryP, cb.equal(subQueryRoot.get(Transaction_.transactionType), TransactionType.fromDescription("DEPOSIT")));

        Expression<Long> expressionCount = subQuery.select(cb.count(subQueryRoot)).where(subQueryP).getSelection();

        return cb.and(p, QueryWithLogicalOperatorsUtil.resolveConditionalExpressionLong(cb, expressionCount, depositCountQuery));
    }

    private static Predicate addDaysSinceFirstDepositLimitPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb, Predicate p, String daysSinceFirstDepositQuery) {
        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<Transaction> subQueryRoot = subQuery.from(Transaction.class);
        Join<Transaction, User> userJoin = subQueryRoot.join(Transaction_.user, JoinType.INNER);
        Join<Transaction, TransactionStatus> statusJoin = subQueryRoot.join(Transaction_.status, JoinType.INNER);

        Predicate subQueryP = cb.equal(userJoin.get(User_.id), root.get(Transaction_.user).get(User_.ID));
        subQueryP = cb.and(subQueryP, cb.equal(userJoin.get(User_.id), subQueryRoot.get(Transaction_.user).get(User_.ID)));
        subQueryP = cb.and(subQueryP, cb.equal(statusJoin.get(TransactionStatus_.CODE), "SUCCESS"));
        subQueryP = cb.and(subQueryP, cb.equal(subQueryRoot.get(Transaction_.transactionType), TransactionType.fromDescription("DEPOSIT")));

        Expression<Long> diff = cb.function(
                "round",
                Long.class,
                cb.diff(
                        cb.quot(cb.function("unix_timestamp", Long.class, cb.currentTime()), DAY_IN_SECONDS),
                        cb.quot(cb.function("unix_timestamp", Long.class, subQueryRoot.get(Transaction_.createdOn)), DAY_IN_SECONDS)
                ));

        Expression<Long> expressionCount = subQuery.select(cb.max(diff)).where(subQueryP).getSelection();

        return cb.and(p, QueryWithLogicalOperatorsUtil.resolveConditionalExpressionLong(cb, expressionCount, daysSinceFirstDepositQuery));
    }

    private static Predicate addTransactionAmountPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb, Predicate p, String transactionAmountQuery) {
        try {
            char[] formattedQueryArray = getFormattedQueryArray(transactionAmountQuery);
            String queryConditionValue = getQueryConditionValue(formattedQueryArray);
            long amountInCentsValue = getAmountInCentsValue(formattedQueryArray);
            Expression<Long> amountExpression = root.get(Transaction_.amountCents);
            return cb.and(p, QueryWithLogicalOperatorsUtil.resolveConditionalExpressionLong(cb, amountExpression, queryConditionValue+amountInCentsValue));
        } catch (Exception ex) {
            log.error("Invalid query condition value, transactionAmount=" + transactionAmountQuery, ex);
        }
        return p;
    }

    private static Predicate addActivePaymentMethodCountPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb, Predicate p, String activePaymentMethodCount) {

        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<ProcessorUserCard> processorUserCardRoot = subQuery.from(ProcessorUserCard.class);
        Join<ProcessorUserCard, User> processorUserCardUserJoin = processorUserCardRoot.join(ProcessorUserCard_.user, JoinType.INNER);
        Join<ProcessorAccountStatus, ProcessorUserCard> accountStatusJoin = processorUserCardRoot.join(ProcessorUserCard_.STATUS, JoinType.INNER);

        Predicate subQueryP = cb.equal(processorUserCardRoot.get(ProcessorUserCard_.user).get(User_.id), processorUserCardUserJoin.get(User_.id));
        subQueryP = cb.and(subQueryP, cb.equal(root.get(Transaction_.user).get(User_.ID), processorUserCardUserJoin.get(User_.id)));
        subQueryP = cb.and(subQueryP, cb.equal(accountStatusJoin.get(ProcessorAccountStatus_.NAME),  "ACTIVE"));

        Expression<Long> expressionCount = subQuery.select(cb.count(processorUserCardRoot)).where(subQueryP).getSelection();

        return cb.and(p, QueryWithLogicalOperatorsUtil.resolveConditionalExpressionLong(cb, expressionCount, activePaymentMethodCount));
    }

    private static Predicate addUserStatusPredicate(Root<Transaction> root, CriteriaBuilder cb, Predicate p, List<Long> userStatusIds) {
        try {
            Join<Transaction, User> userJoin = root.join(Transaction_.user, JoinType.INNER);
            p = cb.and(p, userJoin.get(User_.statusId).in(userStatusIds));
        } catch (NumberFormatException ex) {
            log.error("Invalid query condition value, userStatusId=(" + userStatusIds.toString() + ")", ex);
        }
        return p;
    }

    private static Predicate addUserTagsPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb, Predicate p, List<Long> userTagIds) {
        Join<Transaction, User> userJoin = root.join(Transaction_.user, JoinType.INNER);
        Join<User,UserCategory> userTagJoin = userJoin.join(User_.userCategories, JoinType.INNER);
        p = cb.and(p, userTagJoin.get(UserCategory_.userCategoryId).in(userTagIds));
        query.distinct(true);
        return p;
    }
}
