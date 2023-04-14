package lithium.service.games.data.specifications;

import lithium.service.games.data.entities.Domain;
import lithium.service.games.data.entities.Domain_;
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.data.entities.GameSupplier_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class GameSupplierSpecifications {
	public static Specification<GameSupplier> any(final String search) {
		return (root, query, cb) -> {
			Predicate p = cb.like(root.get(GameSupplier_.name), "%" + search);
			return p;
		};
	}

	public static Specification<GameSupplier> domain(final String domainName) {
		return (root, query, cb) -> {
			Join<GameSupplier, Domain> domainJoin = root.join(GameSupplier_.domain, JoinType.INNER);
			Predicate p = cb.equal(domainJoin.get(Domain_.name), domainName);
			return p;
		};
	}

	public static Specification<GameSupplier> deleted(final boolean deleted) {
		return (root, query, cb) -> {
			Predicate p = cb.equal(root.get(GameSupplier_.deleted), deleted);
			return p;
		};
	}
}
