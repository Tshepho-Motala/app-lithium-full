package lithium.service.leaderboard.data.specifications;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import lithium.service.leaderboard.data.entities.Entry;
import lithium.service.leaderboard.data.entities.Entry_;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardHistory;
import lithium.service.leaderboard.data.entities.LeaderboardHistory_;
import lithium.service.leaderboard.data.entities.User;

public class EntrySpecifications {
	
	public static Specification<Entry> currentTopLeaderboard(Leaderboard leaderboard) {
		return new Specification<Entry>() {
			@Override
			public Predicate toPredicate(Root<Entry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				DateTime now = DateTime.now();
				Join<Entry, LeaderboardHistory> leaderboardHistoryJoin = root.join(Entry_.leaderboardHistory, JoinType.INNER);
//				Join<LeaderboardHistory, Leaderboard> leaderboardJoin = leaderboardHistoryJoin.join(LeaderboardHistory_.leaderboard, JoinType.INNER);
				Predicate p = cb.equal(leaderboardHistoryJoin.get(LeaderboardHistory_.leaderboard), leaderboard);
				p = cb.and(p, cb.lessThanOrEqualTo(root.get(Entry_.rank), leaderboard.getAmount()));
				p = cb.and(p, cb.and(
					cb.greaterThanOrEqualTo(leaderboardHistoryJoin.get(LeaderboardHistory_.endDate), now),
					cb.lessThanOrEqualTo(leaderboardHistoryJoin.get(LeaderboardHistory_.startDate), now),
					cb.between(cb.literal(now), leaderboardHistoryJoin.get(LeaderboardHistory_.startDate), leaderboardHistoryJoin.get(LeaderboardHistory_.endDate))
				));
				return p;
			}
		};
	}
	public static Specification<Entry> currentLeaderboard(Leaderboard leaderboard, User user) {
		return new Specification<Entry>() {
			@Override
			public Predicate toPredicate(Root<Entry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				DateTime now = DateTime.now();
				Join<Entry, LeaderboardHistory> leaderboardHistoryJoin = root.join(Entry_.leaderboardHistory, JoinType.INNER);
				Predicate p = cb.equal(leaderboardHistoryJoin.get(LeaderboardHistory_.leaderboard), leaderboard);
				
				p = cb.and(p, cb.equal(root.get(Entry_.user), user));
				p = cb.and(p, cb.and(
					cb.greaterThanOrEqualTo(leaderboardHistoryJoin.get(LeaderboardHistory_.endDate), now),
					cb.lessThanOrEqualTo(leaderboardHistoryJoin.get(LeaderboardHistory_.startDate), now),
					cb.between(cb.literal(now), leaderboardHistoryJoin.get(LeaderboardHistory_.startDate), leaderboardHistoryJoin.get(LeaderboardHistory_.endDate))
				));
				return p;
			}
		};
	}
	
	public static Specification<Entry> leaderboardHistory(Long leaderboardHistoryId) {
		return new Specification<Entry>() {
			@Override
			public Predicate toPredicate(Root<Entry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Entry, LeaderboardHistory> leaderboardHistoryJoin = root.join(Entry_.leaderboardHistory, JoinType.INNER);
				return cb.equal(leaderboardHistoryJoin.get(LeaderboardHistory_.id), leaderboardHistoryId);
			}
		};
	}
}