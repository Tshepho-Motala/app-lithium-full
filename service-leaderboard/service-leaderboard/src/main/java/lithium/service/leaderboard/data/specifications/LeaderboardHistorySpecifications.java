package lithium.service.leaderboard.data.specifications;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardHistory;
import lithium.service.leaderboard.data.entities.LeaderboardHistory_;
import lithium.service.leaderboard.data.entities.Leaderboard_;

public class LeaderboardHistorySpecifications {
	
	public static Specification<LeaderboardHistory> leaderboard(Long leaderboardId) {
		return new Specification<LeaderboardHistory>() {
			@Override
			public Predicate toPredicate(Root<LeaderboardHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<LeaderboardHistory, Leaderboard> leaderboardJoin = root.join(LeaderboardHistory_.leaderboard, JoinType.INNER);
				return cb.equal(leaderboardJoin.get(Leaderboard_.id), leaderboardId);
			}
		};
	}
	
	public static Specification<LeaderboardHistory> exists(Leaderboard leaderboard, DateTime dateStart, DateTime dateEnd) {
		return new Specification<LeaderboardHistory>() {
			@Override
			public Predicate toPredicate(Root<LeaderboardHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(LeaderboardHistory_.leaderboard), leaderboard);
				p = cb.and(p, cb.equal(root.get(LeaderboardHistory_.closed), false));
				p = cb.and(p, cb.or(
					cb.between(root.get(LeaderboardHistory_.startDate), dateStart, dateEnd),
					cb.between(root.get(LeaderboardHistory_.endDate), dateStart, dateEnd),
					cb.between(cb.literal(dateStart), root.get(LeaderboardHistory_.startDate), root.get(LeaderboardHistory_.endDate))
				));
				return p;
			}
		};
	}
}