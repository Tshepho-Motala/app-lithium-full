package lithium.service.leaderboard.data.specifications;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.leaderboard.data.entities.Domain;
import lithium.service.leaderboard.data.entities.Domain_;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.Leaderboard_;

public class LeaderboardSpecifications {
	public static Specification<Leaderboard> any(String search) {
		return new Specification<Leaderboard>() {
			@Override
			public Predicate toPredicate(Root<Leaderboard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Leaderboard, Domain> domainJoin = root.join(Leaderboard_.domain, JoinType.INNER);
				return cb.or(
					cb.like(cb.upper(domainJoin.get(Domain_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Leaderboard_.name)), "%" + search.toUpperCase() + "%"),
					cb.like(cb.upper(root.get(Leaderboard_.description)), "%" + search.toUpperCase() + "%")
//					cb.like(cb.upper(root.get(Leaderboard_.xpLevelMin).as(String.class)), "%" + search.toUpperCase() + "%"),
//					cb.like(cb.upper(root.get(Leaderboard_.xpLevelMax).as(String.class)), "%" + search.toUpperCase() + "%"),
//					cb.like(cb.upper(root.get(Leaderboard_.type).as(String.class)), "%" + search.toUpperCase() + "%"),
//					cb.like(cb.upper(root.get(Leaderboard_.ordering).as(String.class)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Leaderboard> domains(List<String> domains) {
		return new Specification<Leaderboard>() {
			@Override
			public Predicate toPredicate(Root<Leaderboard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Leaderboard, Domain> domainJoin = root.join(Leaderboard_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
	
	public static Specification<Leaderboard> visible(Boolean visible) {
		return new Specification<Leaderboard>() {
			@Override
			public Predicate toPredicate(Root<Leaderboard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(Leaderboard_.visible), visible);
				return p;
			}
		};
	}
	
	public static Specification<Leaderboard> enabled(Boolean enabled) {
		return new Specification<Leaderboard>() {
			@Override
			public Predicate toPredicate(Root<Leaderboard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(Leaderboard_.enabled), enabled);
				return p;
			}
		};
	}
}