package lithium.service.user.data.specifications;

import lithium.service.user.data.entities.ClosureReason;
import lithium.service.user.data.entities.ClosureReason_;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.Domain_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class ClosureReasonSpecification {
    public static Specification<ClosureReason> domain(String domainName) {
        return (root, query, cb) -> {
            Join<ClosureReason, Domain> joinDomain = root.join(ClosureReason_.domain, JoinType.INNER);
            return cb.equal(joinDomain.get(Domain_.name), domainName);
        };
    }

    public static Specification<ClosureReason> deleted(boolean deleted) {
        return (root, query, cb) ->
            cb.equal(root.get(ClosureReason_.deleted), deleted);
    }

    public static Specification<ClosureReason> any(String search) {
        return (root, query, cb) -> cb.or(
                cb.like(cb.upper(root.get(ClosureReason_.id).as(String.class)), "%" + search.toUpperCase() + "%"),
                cb.like(cb.upper(root.get(ClosureReason_.description)), "%" + search.toUpperCase() + "%")
        );
    }
}
