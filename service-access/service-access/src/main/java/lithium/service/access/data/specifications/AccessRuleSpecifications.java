package lithium.service.access.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.AccessControlList_;
import lithium.service.access.data.entities.AccessRule;
import lithium.service.access.data.entities.AccessRule_;
import lithium.service.access.data.entities.Domain;
import lithium.service.access.data.entities.Domain_;
import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.List_;
import lithium.service.access.data.entities.Value;
import lithium.service.access.data.entities.Value_;

public class AccessRuleSpecifications {
	public static Specification<AccessRule> anyContains(final String value) {
		return new Specification<AccessRule>() {
			@Override
			public Predicate toPredicate(Root<AccessRule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(AccessRule_.name)), "%" + value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(AccessRule_.defaultAction).as(String.class)), "%" + value.toUpperCase() + "%"));
				Join<AccessRule, Domain> joinDomain = root.join(AccessRule_.domain, JoinType.INNER);
				p = cb.or(p, cb.like(cb.upper(joinDomain.get(Domain_.name)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
	
	public static Specification<AccessRule> domainIn(java.util.List<String> domains) {
		return new Specification<AccessRule>() {
			@Override
			public Predicate toPredicate(Root<AccessRule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<AccessRule, Domain> joinDomain = root.join(AccessRule_.domain, JoinType.INNER);
				Predicate p = joinDomain.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
	
	public static Specification<AccessRule> findValueInListInRuleInDomain(String domainName, String listName, String accessRuleName, String value) {
		return new Specification<AccessRule>() {
			@Override
			public Predicate toPredicate(Root<AccessRule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<AccessRule, Domain> joinDomain = root.join(AccessRule_.domain, JoinType.INNER);
				Join<AccessRule, AccessControlList> aclJoin = root.join(AccessRule_.accessControlList, JoinType.LEFT);
				Join<AccessControlList, List> listJoin = aclJoin.join(AccessControlList_.list, JoinType.LEFT);
				Join<List, Value> valueJoin = listJoin.join("values");
//				Join<List, ListType> listTypeJoin = listJoin.join(List_.listType, JoinType.LEFT);
				
				Predicate p = cb.like(cb.upper(joinDomain.get(Domain_.name)), "%" + domainName.toUpperCase() + "%");
				p = cb.and(
					p,
					cb.isTrue(root.get(AccessRule_.enabled)),
					cb.equal(cb.upper(root.get(AccessRule_.name)), accessRuleName.toUpperCase()),
					cb.isTrue(aclJoin.get(AccessControlList_.enabled)),
					cb.equal(cb.upper(listJoin.get(List_.name)), listName.toUpperCase()),
					cb.isTrue(listJoin.get(List_.enabled)),
					cb.equal(valueJoin.get(Value_.data), value)
				);
				
				return p;
			}
		};
	}
	
	public static Specification<AccessRule> findByListInRuleInDomain(String domainName, String listName, String accessRuleName) {
		return new Specification<AccessRule>() {
			@Override
			public Predicate toPredicate(Root<AccessRule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<AccessRule, Domain> joinDomain = root.join(AccessRule_.domain, JoinType.INNER);
				Join<AccessRule, AccessControlList> aclJoin = root.join(AccessRule_.accessControlList, JoinType.LEFT);
				Join<AccessControlList, List> listJoin = aclJoin.join(AccessControlList_.list, JoinType.LEFT);
				
				Predicate p = cb.like(cb.upper(joinDomain.get(Domain_.name)), "%" + domainName.toUpperCase() + "%");
				p = cb.and(
					p,
					cb.isTrue(root.get(AccessRule_.enabled)),
					cb.equal(cb.upper(root.get(AccessRule_.name)), accessRuleName.toUpperCase()),
					cb.isTrue(aclJoin.get(AccessControlList_.enabled)),
					cb.equal(cb.upper(listJoin.get(List_.name)), listName.toUpperCase()),
					cb.isTrue(listJoin.get(List_.enabled))
				);
				
				return p;
			}
		};
	}
}