package lithium.service.casino.search.data.specifications;

import java.util.Date;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import lithium.service.casino.data.entities.BetResult;
import lithium.service.casino.data.entities.BetResultKind;
import lithium.service.casino.data.entities.BetResultKind_;
import lithium.service.casino.data.entities.BetResult_;
import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.BetRound_;
import lithium.service.casino.data.entities.Game;
import lithium.service.casino.data.entities.Game_;
import lithium.service.casino.data.entities.Provider;
import lithium.service.casino.data.entities.Provider_;
import lithium.service.casino.data.entities.User;
import lithium.service.casino.data.entities.User_;
import org.springframework.data.jpa.domain.Specification;

public class BetRoundSpecifications {
  public static Specification<BetRound> any(final String search) {
    return (root, query, cb) -> {
      return null;
    };
  }

  public static Specification<BetRound> user(final String userGuid) {
    return (root, query, cb) -> {
      Join<BetRound, User> userJoin = root.join(BetRound_.user, JoinType.INNER);
      Predicate p = cb.equal(userJoin.get(User_.guid), userGuid);
      return p;
    };
  }

  public static Specification<BetRound> dateRangeStart(final Date dateRangeStart) {
    return (root, query, cb) -> {
      Predicate p = cb.greaterThanOrEqualTo(root.get(BetRound_.createdDate), dateRangeStart.getTime());
      return p;
    };
  }

  public static Specification<BetRound> dateRangeEnd(final Date dateRangeEnd) {
    return (root, query, cb) -> {
      Predicate p = cb.lessThanOrEqualTo(root.get(BetRound_.createdDate), dateRangeEnd.getTime());
      return p;
    };
  }

  public static Specification<BetRound> statuses(final String[] statusCodes) {
    return (root, query, cb) -> {
      Join<BetRound, BetResult> betResultJoin = root.join(BetRound_.lastBetResult, JoinType.INNER);
      Join<BetResult, BetResultKind> betResultKindJoin = betResultJoin.join(BetResult_.betResultKind, JoinType.INNER);
      Predicate p = betResultKindJoin.get(BetResultKind_.code).in(statusCodes);
      return p;
    };
  }

  public static Specification<BetRound> games(final String[] gameGuids) {
    return (root, query, cb) -> {
      Join<BetRound, Game> gameJoin = root.join(BetRound_.game, JoinType.INNER);
      Predicate p = gameJoin.get(Game_.guid).in(gameGuids);
      return p;
    };
  }

  public static Specification<BetRound> providers(final String[] providerGuids) {
    return (root, query, cb) -> {
      Join<BetRound, Provider> providerJoin = root.join(BetRound_.provider, JoinType.INNER);
      Predicate p = providerJoin.get(Provider_.guid).in(providerGuids);
      return p;
    };
  }

  public static Specification<BetRound> betRoundGuid(final String betRoundGuid) {
    return (root, query, cb) -> {
      Predicate p = cb.like(root.get(BetRound_.guid), betRoundGuid + "%");
      return p;
    };
  }
}
