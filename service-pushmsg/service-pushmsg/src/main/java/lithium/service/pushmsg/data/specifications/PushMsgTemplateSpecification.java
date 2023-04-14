package lithium.service.pushmsg.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.pushmsg.data.entities.Domain;
import lithium.service.pushmsg.data.entities.Domain_;
import lithium.service.pushmsg.data.entities.PushMsgTemplate;
import lithium.service.pushmsg.data.entities.PushMsgTemplate_;

public class PushMsgTemplateSpecification {
	public static Specification<PushMsgTemplate> domain(String domainName) {
		return new Specification<PushMsgTemplate>() {
			@Override
			public Predicate toPredicate(Root<PushMsgTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<PushMsgTemplate, Domain> joinDomain = root.join(PushMsgTemplate_.domain, JoinType.INNER);
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				return p;
			}
		};
	}
	
	public static Specification<PushMsgTemplate> any(String value) {
		return new Specification<PushMsgTemplate>() {
			@Override
			public Predicate toPredicate(Root<PushMsgTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(PushMsgTemplate_.name)), "%" + value.toUpperCase() + "%");
//				p = cb.or(p, cb.like(cb.upper(root.get(PushMsgTemplate_.lang)), "%" + value.toUpperCase() + "%"));
				p = cb.or(p, cb.like(cb.upper(root.get(PushMsgTemplate_.enabled).as(String.class)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
}