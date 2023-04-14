package lithium.service.casino.provider.slotapi.storage.specifications;

import lithium.service.casino.provider.slotapi.storage.entities.Bet;
import lithium.service.casino.provider.slotapi.storage.entities.BetResult;
import lithium.service.casino.provider.slotapi.storage.entities.BetResultKind;
import lithium.service.casino.provider.slotapi.storage.entities.BetResultKind_;
import lithium.service.casino.provider.slotapi.storage.entities.BetResult_;
import lithium.service.casino.provider.slotapi.storage.entities.BetRound;
import lithium.service.casino.provider.slotapi.storage.entities.BetRound_;
import lithium.service.casino.provider.slotapi.storage.entities.Bet_;
import lithium.service.casino.provider.slotapi.storage.entities.Game;
import lithium.service.casino.provider.slotapi.storage.entities.Game_;
import lithium.service.casino.provider.slotapi.storage.entities.User;
import lithium.service.casino.provider.slotapi.storage.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class BetSpecifications {
	public static Specification<Bet> any(final String search) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return null;
			}
		};
	}

	public static Specification<Bet> user(final String userGuid) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, BetRound> betRoundJoin = root.join(Bet_.betRound, JoinType.INNER);
				Join<BetRound, User> userJoin = betRoundJoin.join(BetRound_.user, JoinType.INNER);
				Predicate p = cb.equal(userJoin.get(User_.guid), userGuid);
				return p;
			}
		};
	}

	public static Specification<Bet> dateRangeStart(final Date dateRangeStart) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(Bet_.createdDate), dateRangeStart.getTime());
				return p;
			}
		};
	}

	public static Specification<Bet> dateRangeEnd(final Date dateRangeEnd) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(Bet_.createdDate), dateRangeEnd.getTime());
				return p;
			}
		};
	}

	public static Specification<Bet> statuses(final String[] statusCodes) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, BetRound> betRoundJoin = root.join(Bet_.betRound, JoinType.INNER);
				Join<BetRound, BetResult> betResultJoin = betRoundJoin.join(BetRound_.betResult, JoinType.INNER);
				Join<BetResult, BetResultKind> betResultKindJoin = betResultJoin.join(BetResult_.betResultKind, JoinType.INNER);
				Predicate p = betResultKindJoin.get(BetResultKind_.code).in(statusCodes);
				return p;
			}
		};
	}

	public static Specification<Bet> games(final String[] gameGuids) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, BetRound> betRoundJoin = root.join(Bet_.betRound, JoinType.INNER);
				Join<BetRound, Game> gameJoin = betRoundJoin.join(BetRound_.game, JoinType.INNER);
				Predicate p = gameJoin.get(Game_.guid).in(gameGuids);
				return p;
			}
		};
	}

	public static Specification<Bet> betRoundGuid(final String betRoundGuid) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, BetRound> betRoundJoin = root.join(Bet_.betRound, JoinType.INNER);
				Predicate p = cb.like(betRoundJoin.get(BetRound_.guid), betRoundGuid + "%");
				return p;
			}
		};
	}
}
