package lithium.service.domain.data.specifications;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.Domain_;
import lithium.service.domain.data.entities.ProviderAuthClient;
import lithium.service.domain.data.entities.ProviderAuthClient_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class ProviderAuthClientSpecification {

	public static Specification<ProviderAuthClient> domainName(String domainName) {
		return new Specification<ProviderAuthClient>() {
			@Override
			public Predicate toPredicate(Root<ProviderAuthClient> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<ProviderAuthClient, Domain> joinDomain = root.join(ProviderAuthClient_.domain, JoinType.INNER);
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				return p;
			}
		};
	}
	
	public static Specification<ProviderAuthClient> domainNamesIn(List<String> domainNames) {
		return new Specification<ProviderAuthClient>() {
			@Override
			public Predicate toPredicate(Root<ProviderAuthClient> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<ProviderAuthClient, Domain> joinDomain = root.join(ProviderAuthClient_.domain, JoinType.INNER);
				Predicate p = joinDomain.get(Domain_.name).in(domainNames);
				return p;
			}
		};
	}

	public static Specification<ProviderAuthClient> any(String value) {
		return new Specification<ProviderAuthClient>() {
			@Override
			public Predicate toPredicate(Root<ProviderAuthClient> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return null;
			}
		};
	}
}
