package lithium.service.limit.data.specifications;

import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.DomainRestrictionSet_;
import lithium.service.limit.data.entities.Domain_;
import lithium.service.limit.data.entities.User;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.data.entities.UserRestrictionSet_;
import lithium.service.limit.data.entities.User_;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;

public class UserRestrictionSetSpecification {
    public static Specification<UserRestrictionSet> domain(Domain domain) {
        return ((root, query, cb) -> {
            Join<UserRestrictionSet, DomainRestrictionSet> join = root.join("set", JoinType.INNER);
            return  cb.equal(join.get(DomainRestrictionSet_.domain).get(Domain_.name), domain.getName());
        });
    }

    public static  Specification<UserRestrictionSet> active() {

        return ((root, criteriaQuery, cb) -> {
            DateTime now = DateTime.now();
            return  cb.or( cb.isNull(root.get(UserRestrictionSet_.activeTo)),
                    cb.lessThan(root.get(UserRestrictionSet_.activeTo), now.toDate()));
        });
    }

    public static Specification<UserRestrictionSet> withRestrictions(List<String> restrictionNames) {
        return ((root, query, cb) -> {
            Join<UserRestrictionSet, DomainRestrictionSet> join = root.join("set", JoinType.INNER);
            return cb.and(join.get(DomainRestrictionSet_.name).in(restrictionNames));
        });
    }

    public static Specification<UserRestrictionSet> withoutUsers(List<String> guids) {
        return ((root, query, cb) -> {
            Join<UserRestrictionSet, User> join = root.join("user", JoinType.INNER);
            return  join.get(User_.guid).in(guids).not();
        });
    }
}
