package lithium.service.user.search.data.specifications;

import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getFormattedQueryArray;
import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getAmountInCentsValue;
import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getQueryConditionValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory_;
import lithium.service.cashier.data.entities.Transaction_;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.Address_;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.LabelValue;
import lithium.service.user.data.entities.LabelValue_;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.entities.LoginEvent_;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.data.entities.StatusReason_;
import lithium.service.user.data.entities.Status_;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserEvent;
import lithium.service.user.data.entities.UserEvent_;
import lithium.service.user.data.entities.UserRevision;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.data.entities.UserRevisionLabelValue_;
import lithium.service.user.data.entities.UserRevision_;
import lithium.service.user.data.entities.User_;
import lithium.service.user.search.data.entities.CurrentAccountBalance;
import lithium.service.user.search.data.entities.CurrentAccountBalance_;
import lithium.service.user.search.data.entities.Document;
import lithium.service.user.search.data.entities.DocumentStatus;
import lithium.service.user.search.data.entities.DocumentStatus_;
import lithium.service.user.search.data.entities.Document_;
import lithium.service.user.search.data.entities.DomainRestriction;
import lithium.service.user.search.data.entities.DomainRestriction_;
import lithium.service.user.search.data.entities.UserRestriction;
import lithium.service.user.search.data.entities.UserRestriction_;
import lithium.specification.JoinableSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class UserSpecifications {

  public static Specification<User> cashier(
      final TransactionStatus transactionStatus,
      final Date startDate,
      final Date endDate
  ) {
    return new JoinableSpecification<User>() {
      @Override
      public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate p = cb.conjunction();
        if ((transactionStatus != null) || (startDate != null || endDate != null)) {
          root.alias("user");
          query.distinct(true);
          Root<lithium.service.cashier.data.entities.User> cashierUser = query.from(lithium.service.cashier.data.entities.User.class);
          cashierUser.alias("cashierUser");
          Root<lithium.service.cashier.data.entities.Transaction> cashierTran = query.from(lithium.service.cashier.data.entities.Transaction.class);
          cashierTran.alias("cashierTran");

          //        if (transactionType!=null) p = cb.and(p, cb.equal(cashierTran.get(Transaction_.transactionType), transactionType));
          //        if (domainMethod!=null) p = cb.and(p, cb.equal(cashierTran.get(Transaction_.domainMethod), domainMethod));
          //        if (transactionStatus!=null) p = cb.and(p, cb.isTrue(this.joinList(cashierTran, Transaction_.current, JoinType.LEFT).get(TransactionWorkflowHistory_.status).in(transactionStatus)));
          Join<lithium.service.cashier.data.entities.Transaction, lithium.service.cashier.data.entities.User> joinCashierUserTran = cashierTran.join(
              lithium.service.cashier.data.entities.Transaction_.user, JoinType.LEFT
          );

          p = cb.equal(root.get(User_.guid), cashierUser.get(lithium.service.cashier.data.entities.User_.guid));
          p = cb.and(p, cb.equal(joinCashierUserTran.get(lithium.service.cashier.data.entities.User_.guid),
              cashierUser.get(lithium.service.cashier.data.entities.User_.guid)));
          if (transactionStatus != null) {
            p = cb.and(p, cb.equal(this.joinList(cashierTran, Transaction_.current, JoinType.LEFT).get(TransactionWorkflowHistory_.status),
                (transactionStatus)));
          }

          if (startDate != null && endDate == null) {
            p = cb.and(p, cb.and(
                cb.greaterThanOrEqualTo(cashierTran.get(Transaction_.createdOn), startDate),
                cb.lessThan(cashierTran.get(Transaction_.createdOn), new Date()))
            );
          }

          if (endDate != null && startDate == null) {
            p = cb.and(p, cb.and(
                cb.greaterThanOrEqualTo(cashierTran.get(Transaction_.createdOn), new Date(0L)),
                cb.lessThan(cashierTran.get(Transaction_.createdOn), endDate))
            );
          }

          if (startDate != null && endDate != null) {
            p = cb.and(p, cb.and(
                cb.greaterThanOrEqualTo(cashierTran.get(Transaction_.createdOn), startDate),
                cb.lessThan(cashierTran.get(Transaction_.createdOn), endDate))
            );
          }
        }
        return p;
      }
    };
  }

  public static Specification<User> currentAccountBalance(final String currentAccountBalanceQuery) {
    return (JoinableSpecification<User>) (root, query, cb) -> {
      Predicate p = cb.conjunction();
      if (currentAccountBalanceQuery != null && !currentAccountBalanceQuery.isEmpty()) {
        root.alias("user");
        query.distinct(true);
        Root<lithium.service.user.search.data.entities.User> userSearchUser = query.from(lithium.service.user.search.data.entities.User.class);
        userSearchUser.alias("userSearchUser");
        p = cb.equal(root.get(User_.guid), userSearchUser.get(lithium.service.user.search.data.entities.User_.guid));
        Root<CurrentAccountBalance> currentAccountBalance = query.from(CurrentAccountBalance.class);
        currentAccountBalance.alias("currentAccountBalance");
        p = cb.and(p,
            cb.equal(
                userSearchUser.get(lithium.service.user.search.data.entities.User_.id),
                currentAccountBalance.get(CurrentAccountBalance_.user)
            )
        );
        p = getCurrentAccountBalancePredicate(cb, p, currentAccountBalanceQuery, currentAccountBalance);
      }
      return p;
    };
  }

  private static Predicate getCurrentAccountBalancePredicate(
      CriteriaBuilder cb, Predicate p, String currentAccountBalanceQuery, Root<CurrentAccountBalance> currentAccountBalance
  ) {
    try {
      char[] formattedQueryArray = getFormattedQueryArray(currentAccountBalanceQuery);
      String queryConditionValue = getQueryConditionValue(formattedQueryArray);
      Long queryAccountBalanceCentsValue = getAmountInCentsValue(formattedQueryArray);
      Expression<Long> userAccountBalance = cb.prod(currentAccountBalance.get(CurrentAccountBalance_.currentAccountBalance), -1L);
      switch (queryConditionValue) {
        case "<":
          p = cb.and(p, cb.lessThan(userAccountBalance, queryAccountBalanceCentsValue));
          break;
        case ">":
          p = cb.and(p, cb.greaterThan(userAccountBalance, queryAccountBalanceCentsValue));
          break;
        case "=":
          p = cb.and(p, cb.equal(userAccountBalance, queryAccountBalanceCentsValue));
          break;
        case "<=":
          p = cb.and(p, cb.lessThanOrEqualTo(userAccountBalance, queryAccountBalanceCentsValue));
          break;
        case ">=":
          p = cb.and(p, cb.greaterThanOrEqualTo(userAccountBalance, queryAccountBalanceCentsValue));
          break;
        case "!=":
          p = cb.and(p, cb.notEqual(userAccountBalance, queryAccountBalanceCentsValue));
          break;
        default:
          throw new IllegalArgumentException();
      }
    } catch (Exception e) {
      log.error("Invalid query condition value, currentAccountBalanceQuery=" + currentAccountBalanceQuery);
    }
    return p;
  }

  public static Specification<User> any(final String search) {
    return (root, query, cb) -> {
      //TODO the upper will cause the index not to be used. We need to either implement hibernate-search with jms master slave or
      //     a copy of every search column stored in uppercase (given the complexity of hibernate-search and the fact that storage
      //     will be as much or even more if we go that route, the additional column doesn't sound like such a bad idea...
//				Join<User, UserApiToken> userApiTokenJoin = root.join(User_.userApiToken, JoinType.LEFT);
      Predicate p = cb.or(
          cb.like(root.get(User_.guid), search.toUpperCase() + "%"),
          cb.like(root.get(User_.username), search.toUpperCase() + "%"),
          cb.like(root.get(User_.firstName), search.toUpperCase() + "%"),
          cb.like(root.get(User_.lastName), search.toUpperCase() + "%"),
          cb.like(root.get(User_.email), search.toLowerCase() + "%"),
          cb.like(root.get(User_.cellphoneNumber), search + "%")
      );
      try {
        long idSearch = Long.parseLong(search);
        p = cb.or(p, cb.equal(root.get(User_.id), idSearch));
      } catch (NumberFormatException e) {
        // Not logging anything, if not parsable, then ignore
      }
      return p;
    };
  }

  public static Specification<User> domainIn(final List<Domain> domains) {
    return (root, query, cb) -> {
      List<Long> domainIds = new ArrayList<>();
      for (Domain domain : domains) {
        domainIds.add(domain.getId());
      }

      if (domainIds.isEmpty()) domainIds.add(0L);

      return root.get(User_.domain).in(domainIds);
    };
  }

  public static Specification<User> userCategoriesIn(final List<UserCategory> userCategories) {
    return (root, query, cb) -> {
      Join<User, UserCategory> joinUserCategory = root.join(User_.userCategories, JoinType.INNER);
      Predicate p = joinUserCategory.in(userCategories);
      query.distinct(true);
      return p;
    };
  }

  public static Specification<User> usernameStartsWith(final String username) {
    return (root, query, cb) -> {
      Predicate p = cb.like(root.get(User_.username), username + "%");
      return p;
    };
  }

  public static Specification<User> firstNameStartsWith(final String firstName) {
    return (root, query, cb) -> {
      Predicate p = cb.like(root.get(User_.firstName), firstName + "%");
      return p;
    };
  }

  public static Specification<User> lastNameStartsWith(final String lastName) {
    return (root, query, cb) -> {
      Predicate p = cb.like(root.get(User_.lastName), lastName + "%");
      return p;
    };
  }

  public static Specification<User> signupDateRangeStart(final Date signupDateRangeStart) {
    return (root, query, cb) -> {
      Predicate p = cb.greaterThanOrEqualTo(root.get(User_.createdDate).as(Date.class), signupDateRangeStart);
      return p;
    };
  }

  public static Specification<User> signupDateRangeEnd(final Date signupDateRangeEnd) {
    return (root, query, cb) -> {
      Predicate p = cb.lessThanOrEqualTo(root.get(User_.createdDate).as(Date.class), signupDateRangeEnd);
      return p;
    };
  }

  public static Specification<User> statusesIn(final List<String> statuses) {
    return (root, query, cb) -> {
      Join<User, Status> statusJoin = root.join(User_.status, JoinType.INNER);
      Predicate p = statusJoin.get(Status_.name).in(statuses);
      return p;
    };
  }

  public static Specification<User> statusReasonsIn(final List<String> statusReasons) {
    return (root, query, cb) -> {
      Join<User, StatusReason> statusJoin = root.join(User_.statusReason, JoinType.INNER);
      Predicate p = statusJoin.get(StatusReason_.name).in(statusReasons);
      return p;
    };
  }

  public static Specification<User> idStartsWith(final String id) {
    return (root, query, cb) -> {
      Predicate p = cb.like(root.get(User_.id).as(String.class), id + "%");
      return p;
    };
  }

  public static Specification<User> email(final String id) {
    return (root, query, cb) -> {
      Predicate p = cb.like(root.get(User_.email).as(String.class), id + "%");
      return p;
    };
  }

  public static Specification<User> mobilenumber(final String id) {
    return (root, query, cb) -> {
      Predicate p = cb.like(root.get(User_.cellphoneNumber).as(String.class), id + "%");
      return p;
    };
  }

  public static Specification<User> postalcode(final String id) {
    return (root, query, cb) -> {
      Join<User, Address> joinAddress = root.join(User_.residentialAddress, JoinType.INNER);
      Predicate p = cb.like(joinAddress.get(Address_.postalCode).as(String.class), id + "%");
      return p;
    };
  }

  public static Specification<User> verificationstatusesIn(final List<Long> ids) {
    return (root, query, cb) -> {
      Predicate p = root.get(User_.verificationStatus).in(ids);
      return p;
    };
  }

  public static Specification<User> lastlogin(final Date lastloginStartDate, final Date lastloginEndDate) {
    return (root, query, cb) -> {

      Join<User, LoginEvent> joinLastLogin = root.join(User_.lastLogin, JoinType.INNER);
      Predicate p = cb.conjunction();

      if (lastloginStartDate != null) {
        p = cb.and(p, cb.greaterThanOrEqualTo(joinLastLogin.get(LoginEvent_.date), lastloginStartDate));
      }
      if (lastloginEndDate != null) {
        p = cb.and(p, cb.lessThan(joinLastLogin.get(LoginEvent_.date), lastloginEndDate));
      }

      return p;
    };
  }


  public static Specification<User> dateOfBirth(final Date dateOfBirthStartDate, final Date dateOfBirthEndDate) {
    return (root, query, cb) -> {

      Predicate p = cb.conjunction();

      // select DATE(CONCAT_WS('-', dob_year, dob_month, dob_day)) from user
      Expression<Date> dateConcatWSfunction = cb.function("DATE", Date.class,
          cb.function("CONCAT_WS", String.class,
              cb.literal("-"),
              root.get(User_.dobYear),
              root.get(User_.dobMonth),
              root.get(User_.dobDay)));

      if (dateOfBirthStartDate != null) {
        p = cb.and(p, cb.greaterThanOrEqualTo(dateConcatWSfunction, dateOfBirthStartDate));
      }
      if (dateOfBirthEndDate != null) {
        p = cb.and(p, cb.lessThanOrEqualTo(dateConcatWSfunction, dateOfBirthEndDate));
      }

      return p;
    };
  }


  public static Specification<User> lastdeposit(final Date lastdepositStartDate, final Date lastdepositEndDate) {
    return (root, query, cb) -> {

      Predicate p = cb.conjunction();
      ListJoin<User, UserEvent> userEventsJoin = root.join(User_.userEvents, JoinType.INNER);

      p = cb.and(p, userEventsJoin.get(UserEvent_.type).in(Arrays.asList(
          "CASHIER_DEPOSIT",
          "CASHIER_PAYOUT",
//						"CASHIER_PAYOUT_RESULT",
//						"PLAYER_STATUS_DISABLED",
//						"CASINO_BONUS",
          "CASHIER_DEPOSIT_RESULT",
          "MANUAL_DEPOSIT_BONUS_DEP"
      )));

      if (lastdepositStartDate != null) {
        p = cb.and(p, cb.greaterThanOrEqualTo(userEventsJoin.get(UserEvent_.createdOn), lastdepositStartDate));
      }
      if (lastdepositEndDate != null) {
        p = cb.and(p, cb.lessThan(userEventsJoin.get(UserEvent_.createdOn), lastdepositEndDate));
      }

      return p;
    };
  }

  public static Specification<User> clienttype(final List<String> clienttypeList) {
    return (root, query, cb) -> {

      Join<User, LoginEvent> joinLastLogin = root.join(User_.lastLogin, JoinType.INNER);
      Predicate p = joinLastLogin.get(LoginEvent_.providerAuthClient).in(clienttypeList);

      return p;
    };
  }

//  public static Specification<User> fetchUserEvents() {
//    return (root, query, cb) -> {
//      Predicate p = cb.conjunction();
//      Fetch<User, UserEvent> userEventsJoin = root.fetch(User_.userEvents, JoinType.INNER);
//      Fetch<User, LoginEvent> joinLastLogin = root.fetch(User_.lastLogin, JoinType.INNER);
//
//      return p;
//    };
//  }

  public static Specification<User> isTestAccount(final String isTestAccount) {
    return (root, query, cb) -> {
      boolean ta = Boolean.parseBoolean(isTestAccount);

      if (ta) {
        return cb.isTrue(root.get(User_.testAccount));
      } else {
        return cb.or(
            cb.isNull(root.get(User_.testAccount)), // Because it is a nullable field. Null = false.
            cb.isFalse(root.get(User_.testAccount))
        );
      }
    };
  }

  public static Specification<User> isAgeVerified(final String isAgeVerified) {
    return (root, query, cb) -> {
      boolean ageVerified = Boolean.parseBoolean(isAgeVerified);

      if (ageVerified) {
        return cb.isTrue(root.get(User_.ageVerified));
      } else {
        return cb.or(
            cb.isNull(root.get(User_.ageVerified)), // Because it is a nullable field. Null = false.
            cb.isFalse(root.get(User_.ageVerified))
        );
      }
    };
  }

  public static Specification<User> isAddressVerified(final String isAddressVerified) {
    return (root, query, cb) -> {
      boolean addressVerified = Boolean.parseBoolean(isAddressVerified);

      if (addressVerified) {
        return cb.isTrue(root.get(User_.addressVerified));
      } else {
        return cb.or(
            cb.isNull(root.get(User_.addressVerified)), // Because it is a nullable field. Null = false.
            cb.isFalse(root.get(User_.addressVerified))
        );
      }
    };
  }

  public static Specification<User> isEmailValidated(final String isEmailValidated) {
    return (root, query, cb) -> {
      return Boolean.parseBoolean(isEmailValidated) ? cb.isTrue(root.get(User_.emailValidated)) : cb.isFalse(root.get(User_.emailValidated));
    };
  }

  public static Specification<User> userRestrictions(
      final List<Long> domainRestrictionIds,
      final String activeRestriction,
      final Date restrictionActiveFromDate) {
    return (root, query, cb) -> {

      Predicate p = cb.conjunction();

      if (restrictionFilterEnabled(domainRestrictionIds, activeRestriction, restrictionActiveFromDate)) {
        root.alias("user");
        query.distinct(true);

        Root<lithium.service.user.search.data.entities.User> userSearchUserRoot = query.from(lithium.service.user.search.data.entities.User.class);
        userSearchUserRoot.alias("userSearchUserRoot");
        p = cb.equal(root.get(User_.guid), userSearchUserRoot.get(lithium.service.user.search.data.entities.User_.guid));

        Root<UserRestriction> userRestrictionRoot = query.from(UserRestriction.class);
        userRestrictionRoot.alias("userRestrictionRoot");
        p = cb.and(p,
            cb.equal(
                userSearchUserRoot.get(lithium.service.user.search.data.entities.User_.id),
                userRestrictionRoot.get(UserRestriction_.user)));

        Root<DomainRestriction> domainRestrictionRoot = query.from(DomainRestriction.class);
        domainRestrictionRoot.alias("domainRestrictionRoot");
        p = cb.and(p,
            cb.equal(
                domainRestrictionRoot.get(DomainRestriction_.id),
                userRestrictionRoot.get(UserRestriction_.domainRestriction)
            ));

        if (domainRestrictionIds != null && !domainRestrictionIds.isEmpty()) {
          p = cb.and(p, domainRestrictionRoot.get(DomainRestriction_.id).in(domainRestrictionIds));
        }

        if (activeRestriction != null) {
          boolean isActiveRestriction = Boolean.parseBoolean(activeRestriction);
          if (isActiveRestriction) {
            p = cb.and(p, cb.lessThanOrEqualTo(userRestrictionRoot.get(UserRestriction_.activeFrom), new Date()));
          } else {
            p = cb.and(p, cb.greaterThan(userRestrictionRoot.get(UserRestriction_.activeFrom), new Date()));
          }
        }

        if (restrictionActiveFromDate != null) {
          p = cb.and(p, cb.greaterThanOrEqualTo(userRestrictionRoot.get(UserRestriction_.activeFrom), restrictionActiveFromDate));
        }
      }
      return p;
    };
  }

  private static boolean restrictionFilterEnabled(List<Long> domainRestrictions, String activeRestriction, Date restrictionActiveFromDate) {
    return (domainRestrictions != null && !domainRestrictions.isEmpty())
        || activeRestriction != null
        || restrictionActiveFromDate != null;
  }

  public static Specification<User> documentStatuses(final String documentStatus) {
    return (root, query, cb) -> {
      Predicate p = cb.conjunction();
      if (documentStatus != null && !documentStatus.isEmpty()) {

        root.alias("user");
        query.distinct(true);

        Root<lithium.service.user.search.data.entities.User> userSearchUserRoot = query.from(lithium.service.user.search.data.entities.User.class);
        userSearchUserRoot.alias("userSearchUserRoot");

        Root<DocumentStatus> documentStatusRoot = query.from(DocumentStatus.class);
        documentStatusRoot.alias("documentStatusRoot");

        Root<Document> documentRoot = query.from(Document.class);
        documentRoot.alias("documentRoot");

        p = cb.equal(root.get(User_.guid), userSearchUserRoot.get(lithium.service.user.search.data.entities.User_.guid));

        p = cb.and(p,
            cb.equal(
                userSearchUserRoot.get(lithium.service.user.search.data.entities.User_.id),
                documentRoot.get(Document_.user)));

        p = cb.and(p, cb.isFalse(documentRoot.get(Document_.sensitive)));

        p = cb.and(p,
            cb.equal(
                documentRoot.get(Document_.status),
                documentStatusRoot.get(DocumentStatus_.id)
            ));

        p = cb.and(p, cb.equal(documentStatusRoot.get(DocumentStatus_.name).as(String.class), documentStatus));

      }
      return p;
    };
  }

  public static Specification<User> isDeleted(final boolean isUserDeleted) {
    return (root, query, cb) -> {
      if (isUserDeleted) {
        return cb.isTrue(root.get(User_.deleted));
      } else {
        return cb.isFalse(root.get(User_.deleted));
      }
    };
  }

  public static Specification<User> affiliatesLabels(final List<Long> affiliatesIds) {
    return (root, query, cb) -> {

      if (affiliatesIds != null && affiliatesIds.size() > 0) {
        query.distinct(true);

        Join<User, UserRevision> joinUserRevision = root.join(User_.current, JoinType.INNER);
        Join<UserRevision, UserRevisionLabelValue> joinRevisionLabel = joinUserRevision.join(UserRevision_.labelValueList, JoinType.INNER);
        Join<UserRevisionLabelValue, LabelValue> joinLabelValue = joinRevisionLabel.join(UserRevisionLabelValue_.labelValue, JoinType.INNER);

        return joinLabelValue.get(LabelValue_.id).in(affiliatesIds);

      } else {
        return cb.conjunction();
      }
    };
  }
}
