package lithium.service.user.data.specifications;

import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.Domain_;
import lithium.service.user.data.entities.Label;
import lithium.service.user.data.entities.LabelValue;
import lithium.service.user.data.entities.LabelValue_;
import lithium.service.user.data.entities.Label_;
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
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UserSpecifications {

	public static Specification<User> any(final String search) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
			}
		};
	}

	public static Specification<User> domainIn(final List<Domain> domains) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Long> domainIds = new ArrayList<>();
				for (Domain domain: domains) {
					domainIds.add(domain.getId());
				}
//				Join<User, Domain> joinDomain = root.join(User_.domain, JoinType.INNER);
				Predicate p = root.get(User_.domain).in(domainIds);
				return p;
			}
		};
	}

	public static Specification<User> userCategoriesIn(final List<UserCategory> userCategories) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<User, UserCategory> joinUserCategory = root.join(User_.userCategories, JoinType.INNER);
				Predicate p = joinUserCategory.in(userCategories);
				query.distinct(true);
				return p;
			}
		};
	}

	public static Specification<User> userCategories(final Long userCategoryId) {
		return (root, query, cb) -> {
      Join<User, UserCategory> joinUserCategory = root.join(User_.userCategories, JoinType.INNER);
      return joinUserCategory.in(userCategoryId);
    };
	}

	public static Specification<User> userWithLabel(final String labelName, final String valueString) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<User, UserRevision> joinUserRevision = root.join(User_.current, JoinType.INNER);
				Join<UserRevision, UserRevisionLabelValue> joinUsrRevLabVal = joinUserRevision.join(UserRevision_.labelValueList, JoinType.INNER);
				Join<UserRevisionLabelValue, LabelValue> joinLabelValue =  joinUsrRevLabVal.join(UserRevisionLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);

				Predicate p = cb.like(joinLabel.get(Label_.name), labelName);
				if (valueString != null) {
					p = cb.and(p, cb.like(joinLabelValue.get(LabelValue_.value), valueString));
				}
				return p;
			}
		};
	}

	public static Specification<User> byDomainAndLabel(final String domainName, final String labelName) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<User, Domain> joinDomain = root.join(User_.domain, JoinType.INNER);
				Join<User, UserRevision> joinRevision = root.join(User_.current, JoinType.INNER);
				Join<UserRevision, UserRevisionLabelValue> joinRevisionLv = joinRevision.join(UserRevision_.labelValueList, JoinType.INNER);
				Join<UserRevisionLabelValue, LabelValue> joinLabelValue = joinRevisionLv.join(UserRevisionLabelValue_.labelValue, JoinType.INNER);
				Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);

				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				p = cb.and(p, cb.equal(joinLabel.get(Label_.name), labelName));

				return p;
			}
		};
	}

	public static Specification<User> usernameStartsWith(final String username) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(User_.username), username + "%");
				return p;
			}
		};
	}

	public static Specification<User> firstNameStartsWith(final String firstName) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(User_.firstName), firstName + "%");
				return p;
			}
		};
	}

	public static Specification<User> lastNameStartsWith(final String lastName) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(User_.lastName), lastName + "%");
				return p;
			}
		};
	}

	public static Specification<User> signupDateRangeStart(final Date signupDateRangeStart) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(User_.createdDate).as(Date.class), signupDateRangeStart);
				return p;
			}
		};
	}

	public static Specification<User> signupDateRangeEnd(final Date signupDateRangeEnd) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(User_.createdDate).as(Date.class), signupDateRangeEnd);
				return p;
			}
		};
	}

	public static Specification<User> statusesIn(final List<String> statuses) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<User, Status> statusJoin = root.join(User_.status, JoinType.INNER);
				Predicate p = statusJoin.get(Status_.name).in(statuses);
				return p;
			}
		};
	}

	public static Specification<User> statusReasonsIn(final List<String> statusReasons) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<User, StatusReason> statusJoin = root.join(User_.statusReason, JoinType.INNER);
				Predicate p = statusJoin.get(StatusReason_.name).in(statusReasons);
				return p;
			}
		};
	}

	public static Specification<User> idStartsWith(final String id) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(User_.id).as(String.class), id + "%");
				return p;
			}
		};
	}

	public static Specification<User> email(final String id) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(User_.email).as(String.class), id + "%");
				return p;
			}
		};
	}

	public static Specification<User> mobilenumber(final String id) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(User_.cellphoneNumber).as(String.class), id + "%");
				return p;
			}
		};
	}

	public static Specification<User> verificationstatusesIn(final List<Long> ids) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = root.get(User_.verificationStatus).in(ids);
				return p;
			}
		};
	}

	public static Specification<User> lastlogin(final Date lastloginStartDate, final Date lastloginEndDate) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				Join<User, LoginEvent> joinLastLogin = root.join(User_.lastLogin, JoinType.INNER);
				Predicate p = cb.conjunction();

				if (lastloginStartDate != null) {
					p = cb.and(p, cb.greaterThanOrEqualTo(joinLastLogin.get(LoginEvent_.date), lastloginStartDate));
				}
				if (lastloginEndDate != null) {
					p = cb.and(p, cb.lessThan(joinLastLogin.get(LoginEvent_.date), lastloginEndDate));
				}

				return p;
			}
		};
	}


	public static Specification<User> dateOfBirth(final Date dateOfBirthStartDate, final Date dateOfBirthEndDate) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

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
			}
		};
	}


	public static Specification<User> lastdeposit(final Date lastdepositStartDate, final Date lastdepositEndDate) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

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
			}
		};
	}

	public static Specification<User> clienttype(final List<String> clienttypeList) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				Join<User, LoginEvent> joinLastLogin = root.join(User_.lastLogin, JoinType.INNER);
				Predicate p = joinLastLogin.get(LoginEvent_.providerAuthClient).in(clienttypeList);

				return p;
			}
		};
	}

	public static Specification<User> fetchUserEvents() {

		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				Predicate p = cb.conjunction();
				Fetch<User, UserEvent> userEventsJoin = root.fetch(User_.userEvents, JoinType.INNER);
				Fetch<User, LoginEvent> joinLastLogin = root.fetch(User_.lastLogin, JoinType.INNER);

				return p;
			}
		};

	}

	public static Specification<User> isTestAccount(final String isTestAccount) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				boolean ta = Boolean.parseBoolean(isTestAccount);

				if (ta) {
				  return cb.isTrue(root.get(User_.testAccount));
        } else {
				  return cb.or(
				      cb.isNull(root.get(User_.testAccount)), // Because it is a nullable field. Null = false.
              cb.isFalse(root.get(User_.testAccount))
          );
        }
			}
		};
	}

	public static Specification<User> cellphoneNumberWithLeadingZero(int cellphoneLength) {
	  return ((root, query, cb) -> {
        Expression<Integer> lengthExpression = cb.length(root.get(User_.cellphoneNumber));
	      return cb.and(
	          cb.equal(lengthExpression, cellphoneLength),
            cb.like(root.get(User_.cellphoneNumber), "0%")
        );
    });
  }
  public static Specification<User> guidOrUsernameIn(List<String> usernameAndGuids) {
    return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or(
        root.get(User_.guid).in(usernameAndGuids),
        root.get(User_.username).in(usernameAndGuids)
    ));
  }

  public static Specification<User> guidIn(List<String> guids) {
    return (root, criteriaQuery, criteriaBuilder) -> root.get(User_.guid).in(guids);
  }

  public static Specification<User> withBirthdayOn(DateTime birthdayOn) {
    return ((root, criteriaQuery, criteriaBuilder) -> {
      int month = birthdayOn.getMonthOfYear();
      int day = birthdayOn.getDayOfMonth();

      Predicate predicate = criteriaBuilder.equal(root.get(User_.dobDay), day);
      predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(User_.dobMonth), month));

      return predicate;

    });
  }

  public static Specification<User> userByDomainWithLabelAndOptionalValue(final List<String> domainNames, final String labelName, final String valueString) {
    return (root, query, cb) -> {
      Join<User, Domain> userDomainJoin = root.join(User_.domain, JoinType.INNER);
      Join<User, UserRevision> joinUserRevision = root.join(User_.current, JoinType.INNER);
      Join<UserRevision, UserRevisionLabelValue> joinUsrRevLabVal = joinUserRevision.join(UserRevision_.labelValueList, JoinType.INNER);
      Join<UserRevisionLabelValue, LabelValue> joinLabelValue =  joinUsrRevLabVal.join(UserRevisionLabelValue_.labelValue, JoinType.INNER);
      Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);

      Expression<String> expression = userDomainJoin.get(Domain_.name);

      Predicate p = cb.like(joinLabel.get(Label_.name), labelName);
      p = cb.and(p, expression.in(domainNames));
      if (valueString != null) {
        p = cb.and(p, cb.like(joinLabelValue.get(LabelValue_.value), valueString));
      }

      return p;
    };
  }

}
