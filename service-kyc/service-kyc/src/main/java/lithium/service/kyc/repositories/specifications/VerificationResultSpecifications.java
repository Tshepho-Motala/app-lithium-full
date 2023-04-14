package lithium.service.kyc.repositories.specifications;

import lithium.service.kyc.entities.User;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.entities.VerificationResult_;
import lithium.specification.JoinableSpecification;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class VerificationResultSpecifications {
    public static Specification<VerificationResult> table(
            final User user,
            final DateTime startDate,
            final DateTime endDate
    ) {
        return new JoinableSpecification<VerificationResult>() {
            @Override
            public Predicate toPredicate(Root<VerificationResult> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate p = cb.conjunction();
                if (user != null) p = cb.and(p, cb.equal(root.get(VerificationResult_.user), user));
                DateTime tmpEndDate = endDate.plusDays(1);
                p = cb.and(p, cb.and(
                        cb.greaterThanOrEqualTo(root.get(VerificationResult_.createdOn), startDate.toDate()),
                        cb.lessThan(root.get(VerificationResult_.createdOn), tmpEndDate.toDate())));
                return p;
            }
        };
    }
}
