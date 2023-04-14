package lithium.service.leaderboard.data.specifications;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardPlaceNotification;
import lithium.service.leaderboard.data.entities.LeaderboardPlaceNotification_;
import lithium.service.leaderboard.data.entities.Leaderboard_;

public class LeaderboardPlaceNotificationSpecifications {
	
	public static Specification<LeaderboardPlaceNotification> leaderboard(Long leaderboardId) {
		return new Specification<LeaderboardPlaceNotification>() {
			@Override
			public Predicate toPredicate(Root<LeaderboardPlaceNotification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<LeaderboardPlaceNotification, Leaderboard> leaderboardJoin = root.join(LeaderboardPlaceNotification_.leaderboard, JoinType.INNER);
				return cb.equal(leaderboardJoin.get(Leaderboard_.id), leaderboardId);
			}
		};
	}
}