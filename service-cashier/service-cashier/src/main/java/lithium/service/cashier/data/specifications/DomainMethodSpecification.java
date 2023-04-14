package lithium.service.cashier.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethod_;
import lithium.service.cashier.data.entities.Domain_;
import lithium.specification.JoinableSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DomainMethodSpecification {
	
	public static Specification<DomainMethod> table(final String search, final Domain domain) {
		return new JoinableSpecification<DomainMethod>() {
			@Override
			public Predicate toPredicate(Root<DomainMethod> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				log.debug("search: "+search+" domain: "+domain);
				return cb.and(
					cb.isNotNull(this.joinList(root, DomainMethod_.method, JoinType.RIGHT)),
					cb.or(
						cb.isNull(this.joinList(root, DomainMethod_.domain, JoinType.LEFT)),
						cb.equal(this.joinList(root, DomainMethod_.domain, JoinType.LEFT).get(Domain_.id), domain.getId()+"")
					)
				);
			}
		};
	}
}