package lithium.service.games.data.specifications;

import lithium.service.games.data.entities.Domain;
import lithium.service.games.data.entities.Domain_;
import lithium.service.games.data.entities.GameStudio;
import lithium.service.games.data.entities.GameStudio_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class GameStudioSpecifications {
    public static Specification<GameStudio> any(final String search) {
        return (root, query, cb) -> {
            Predicate p = cb.like(root.get(GameStudio_.name), search + "%");
            return p;
        };
    }

    public static Specification<GameStudio> domain(final String domainName) {
        return (root, query, cb) -> {
            Join<GameStudio, Domain> domainJoin = root.join(GameStudio_.domain, JoinType.INNER);
            Predicate p = cb.equal(domainJoin.get(Domain_.name), domainName);
            return p;
        };
    }

    public static Specification<GameStudio> deleted(final boolean deleted) {
        return (root, query, cb) -> {
            Predicate p = cb.equal(root.get(GameStudio_.deleted), deleted);
            return p;
        };
    }
}
