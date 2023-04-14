package lithium.service.sms.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.sms.data.entities.Domain;
import lithium.service.sms.data.entities.Domain_;
import lithium.service.sms.data.entities.SMSTemplate;
import lithium.service.sms.data.entities.SMSTemplate_;

public class SMSTemplateSpecification {
	public static Specification<SMSTemplate> domain(String domainName) {
		return new Specification<SMSTemplate>() {
			@Override
			public Predicate toPredicate(Root<SMSTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<SMSTemplate, Domain> joinDomain = root.join(SMSTemplate_.domain, JoinType.INNER);
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				return p;
			}
		};
	}
	
	public static Specification<SMSTemplate> any(String value) {
		return new Specification<SMSTemplate>() {
			@Override
			public Predicate toPredicate(Root<SMSTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(SMSTemplate_.name)), "%" + value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(SMSTemplate_.lang)), "%" + value.toUpperCase() + "%"));
				p = cb.or(p, cb.like(cb.upper(root.get(SMSTemplate_.enabled).as(String.class)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
}