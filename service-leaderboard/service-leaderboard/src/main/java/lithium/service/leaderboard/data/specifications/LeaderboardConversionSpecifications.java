package lithium.service.leaderboard.data.specifications;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardConversion;
import lithium.service.leaderboard.data.entities.LeaderboardConversion_;
import lithium.service.leaderboard.data.entities.Leaderboard_;

public class LeaderboardConversionSpecifications {
	
	public static Specification<LeaderboardConversion> leaderboard(Long leaderboardId) {
		return new Specification<LeaderboardConversion>() {
			@Override
			public Predicate toPredicate(Root<LeaderboardConversion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<LeaderboardConversion, Leaderboard> leaderboardJoin = root.join(LeaderboardConversion_.leaderboard, JoinType.INNER);
				return cb.equal(leaderboardJoin.get(Leaderboard_.id), leaderboardId);
			}
		};
	}
}