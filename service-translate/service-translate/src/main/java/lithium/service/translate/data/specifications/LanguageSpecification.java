package lithium.service.translate.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.entities.Language_;

public class LanguageSpecification {

	public static Specification<Language> anyContains(final String value) {
		return new Specification<Language>() {
			@Override
			public Predicate toPredicate(Root<Language> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(Language_.description)), value.toUpperCase() + '%');
				p = cb.or(p, cb.like(cb.upper(root.get(Language_.locale2)), value.toUpperCase() + '%'));
				p = cb.or(p, cb.like(cb.upper(root.get(Language_.locale3)), value.toUpperCase() + '%'));
				return p;
			}
		};
	}

	public static Specification<Language> enabled(final Boolean value) {
		return new Specification<Language>() {
			@Override
			public Predicate toPredicate(Root<Language> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(Language_.enabled), value);
				return p;
			}
		};
	}

}
