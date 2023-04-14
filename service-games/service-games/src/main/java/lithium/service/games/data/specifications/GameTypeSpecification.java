package lithium.service.games.data.specifications;

import lithium.service.games.data.entities.Domain;
import lithium.service.games.data.entities.Domain_;
import lithium.service.games.data.entities.GameType;
import lithium.service.games.data.entities.GameType_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class GameTypeSpecification {
    public static Specification<GameType> any(final String search) {
        return (root, query, cb) -> {
            Predicate p = cb.like(root.get(GameType_.name), search + "%");
            return p;
        };
    }

    public static Specification<GameType> domain(final String domainName) {
        return (root, query, cb) -> {
            Join<GameType, Domain> domainJoin = root.join(GameType_.domain, JoinType.INNER);
            Predicate p = cb.equal(domainJoin.get(Domain_.name), domainName);
            return p;
        };
    }

    public static Specification<GameType> deleted(final boolean deleted) {
        return (root, query, cb) -> {
            Predicate p = cb.equal(root.get(GameType_.deleted), deleted);
            return p;
        };
    }
}
