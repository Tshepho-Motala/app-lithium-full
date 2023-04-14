package lithium.service.domain.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainRevision;
import lithium.service.domain.data.entities.DomainRevision_;
import lithium.service.domain.data.entities.Domain_;

public class DomainRevisionSpecification {
	public static Specification<DomainRevision> domainName(String domainName) {
		return new Specification<DomainRevision>() {
			@Override
			public Predicate toPredicate(Root<DomainRevision> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<DomainRevision, Domain> joinDomain = root.join(DomainRevision_.domain, JoinType.INNER);
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				return p;
			}
		};
	}
	
	public static Specification<DomainRevision> any(String value) {
		return new Specification<DomainRevision>() {
			@Override
			public Predicate toPredicate(Root<DomainRevision> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return null;
			}
		};
	}
}
