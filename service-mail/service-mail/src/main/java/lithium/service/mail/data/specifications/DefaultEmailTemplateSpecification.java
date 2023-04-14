package lithium.service.mail.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.mail.data.entities.DefaultEmailTemplate;
import lithium.service.mail.data.entities.DefaultEmailTemplate_;

public class DefaultEmailTemplateSpecification {
	public static Specification<DefaultEmailTemplate> any(String value) {
		return new Specification<DefaultEmailTemplate>() {
			@Override
			public Predicate toPredicate(Root<DefaultEmailTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(DefaultEmailTemplate_.name)), "%" + value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(DefaultEmailTemplate_.subject)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
}