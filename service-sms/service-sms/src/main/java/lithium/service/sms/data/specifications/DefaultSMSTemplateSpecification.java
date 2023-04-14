package lithium.service.sms.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.sms.data.entities.DefaultSMSTemplate;
import lithium.service.sms.data.entities.DefaultSMSTemplate_;

public class DefaultSMSTemplateSpecification {
	public static Specification<DefaultSMSTemplate> any(String value) {
		return new Specification<DefaultSMSTemplate>() {
			@Override
			public Predicate toPredicate(Root<DefaultSMSTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(DefaultSMSTemplate_.name)), "%" + value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(DefaultSMSTemplate_.description)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
}