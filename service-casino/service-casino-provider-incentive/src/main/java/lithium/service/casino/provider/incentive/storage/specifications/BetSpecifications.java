package lithium.service.casino.provider.incentive.storage.specifications;


import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.BetSelection;
import lithium.service.casino.provider.incentive.storage.entities.BetSelection_;
import lithium.service.casino.provider.incentive.storage.entities.Bet_;
import lithium.service.casino.provider.incentive.storage.entities.Competition;
import lithium.service.casino.provider.incentive.storage.entities.Competition_;
import lithium.service.casino.provider.incentive.storage.entities.Domain;
import lithium.service.casino.provider.incentive.storage.entities.Domain_;
import lithium.service.casino.provider.incentive.storage.entities.Event;
import lithium.service.casino.provider.incentive.storage.entities.EventName;
import lithium.service.casino.provider.incentive.storage.entities.EventName_;
import lithium.service.casino.provider.incentive.storage.entities.Event_;
import lithium.service.casino.provider.incentive.storage.entities.Market;
import lithium.service.casino.provider.incentive.storage.entities.Market_;
import lithium.service.casino.provider.incentive.storage.entities.Placement;
import lithium.service.casino.provider.incentive.storage.entities.Placement_;
import lithium.service.casino.provider.incentive.storage.entities.Settlement;
import lithium.service.casino.provider.incentive.storage.entities.SettlementResult;
import lithium.service.casino.provider.incentive.storage.entities.SettlementResult_;
import lithium.service.casino.provider.incentive.storage.entities.Settlement_;
import lithium.service.casino.provider.incentive.storage.entities.Sport;
import lithium.service.casino.provider.incentive.storage.entities.Sport_;
import lithium.service.casino.provider.incentive.storage.entities.User;
import lithium.service.casino.provider.incentive.storage.entities.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class BetSpecifications {
	public static Specification<Bet> any(String search) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, Settlement> settlementJoin = root.join(Bet_.settlement, JoinType.LEFT);
				return cb.or(
					cb.like(root.get(Bet_.betTransactionId), search + "%"),
					cb.like(settlementJoin.get(Settlement_.settlementTransactionId), search + "%")
				);
			}
		};
	}

	public static Specification<Bet> betId(String betId) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(Bet_.betTransactionId), betId + "%");
				return p;
			}
		};
	}

	public static Specification<Bet> settlementId(String settlementId) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, Settlement> settlementJoin = root.join(Bet_.settlement, JoinType.LEFT);
				Predicate p = cb.like(settlementJoin.get(Settlement_.settlementTransactionId), settlementId + "%");
				return p;
			}
		};
	}

	public static Specification<Bet> domains(List<String> domains) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, Placement> placementJoin = root.join(Bet_.placement, JoinType.INNER);
				Join<Placement, Domain> domainJoin = placementJoin.join(Placement_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}

	public static Specification<Bet> user(String userGuid) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, Placement> placementJoin = root.join(Bet_.placement, JoinType.INNER);
				Join<Placement, User> userJoin = placementJoin.join(Placement_.user, JoinType.INNER);
				Predicate p = cb.equal(userJoin.get(User_.guid), userGuid);
				return p;
			}
		};
	}

	public static Specification<Bet> isSettled(boolean settled) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = (settled)
							? cb.isNotNull(root.get(Bet_.settlement))
							: cb.isNull(root.get(Bet_.settlement));
				return p;
			}
		};
	}

	public static Specification<Bet> settlementResult(String code) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, Settlement> settlementJoin = root.join(Bet_.settlement, JoinType.INNER);
				Join<Settlement, SettlementResult> settlementResultJoin = settlementJoin.join(Settlement_.settlementResult, JoinType.INNER);
				Predicate p = cb.equal(settlementResultJoin.get(SettlementResult_.code), code);
				return p;
			}
		};
	}

	public static Specification<Bet> betTimestampRangeStart(Date betTimestampRangeStart) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(Bet_.transactionTimestamp).as(Date.class), betTimestampRangeStart);
				return p;
			}
		};
	}

	public static Specification<Bet> betTimestampRangeEnd(Date betTimestampRangeEnd) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(Bet_.transactionTimestamp).as(Date.class), betTimestampRangeEnd);
				return p;
			}
		};
	}

	public static Specification<Bet> settlementTimestampRangeStart(Date settlementTimestampRangeStart) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, Settlement> settlementJoin = root.join(Bet_.settlement, JoinType.INNER);
				Predicate p = cb.greaterThanOrEqualTo(settlementJoin.get(Settlement_.transactionTimestamp).as(Date.class), settlementTimestampRangeStart);
				return p;
			}
		};
	}

	public static Specification<Bet> settlementTimestampRangeEnd(Date settlementTimestampRangeEnd) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, Settlement> settlementJoin = root.join(Bet_.settlement, JoinType.INNER);
				Predicate p = cb.lessThanOrEqualTo(settlementJoin.get(Settlement_.transactionTimestamp).as(Date.class), settlementTimestampRangeEnd);
				return p;
			}
		};
	}

	public static Specification<Bet> market(String market) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, BetSelection> betSelectJoin = root.join(Bet_.betSelections, JoinType.INNER);
				Join<BetSelection, Market> marketJoin = betSelectJoin.join(BetSelection_.market, JoinType.INNER);
				return cb.or(
						cb.like(marketJoin.get(Market_.code), market + "%"),
						cb.like(marketJoin.get(Market_.name), market + "%")
				);
			}
		};
	}

	public static Specification<Bet> eventName(String eventName) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, BetSelection> betSelectJoin = root.join(Bet_.betSelections, JoinType.INNER);
				Join<BetSelection, Event> eventJoin = betSelectJoin.join(BetSelection_.event, JoinType.INNER);
				Join<Event, EventName> eventNameJoin = eventJoin.join(Event_.eventName, JoinType.INNER);
				Predicate p = cb.like(eventNameJoin.get(EventName_.name), eventName + "%");
				return p;
			}
		};
	}

	public static Specification<Bet> sport(String sport) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, BetSelection> betSelectJoin = root.join(Bet_.betSelections, JoinType.INNER);
				Join<BetSelection, Sport> sportJoin = betSelectJoin.join(BetSelection_.sport, JoinType.INNER);
				return cb.or(
						cb.like(sportJoin.get(Sport_.code), sport + "%"),
						cb.like(sportJoin.get(Sport_.name), sport + "%")
				);
			}
		};
	}

	public static Specification<Bet> competition(String competition) {
		return new Specification<Bet>() {
			@Override
			public Predicate toPredicate(Root<Bet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Bet, BetSelection> betSelectJoin = root.join(Bet_.betSelections, JoinType.INNER);
				Join<BetSelection, Competition> competitionJoin = betSelectJoin.join(BetSelection_.competition, JoinType.INNER);
				return cb.or(
						cb.like(competitionJoin.get(Competition_.code), competition + "%"),
						cb.like(competitionJoin.get(Competition_.name), competition + "%")
				);
			}
		};
	}
}
