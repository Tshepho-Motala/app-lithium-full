package lithium.service.domain.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.Domain_;
import lithium.service.domain.data.entities.Template;
import lithium.service.domain.data.entities.TemplateRevision;
import lithium.service.domain.data.entities.TemplateRevision_;
import lithium.service.domain.data.entities.Template_;

public class TemplateSpecification {
	public static Specification<Template> domainName(String domainName) {
		return new Specification<Template>() {
			@Override
			public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Template, Domain> joinDomain = root.join(Template_.domain, JoinType.INNER);
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				return p;
			}
		};
	}
	
	public static Specification<Template> any(String value) {
		return new Specification<Template>() {
			@Override
			public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(Template_.name)), value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(Template_.lang)),  value.toUpperCase() + "%"));
				p = cb.or(p, cb.like(cb.upper(root.get(Template_.enabled).as(String.class)), "%" + value.toUpperCase() + "%"));
				Join<Template, TemplateRevision> joinRev = root.join(Template_.current, JoinType.INNER);
				p = cb.or(p, cb.like(cb.upper(joinRev.get(TemplateRevision_.description)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}


	public static Specification<Template> isDelete(boolean isDeleted) {
	  return ((root, query, cb) -> cb.equal(root.get(Template_.deleted), isDeleted));

  }
}
