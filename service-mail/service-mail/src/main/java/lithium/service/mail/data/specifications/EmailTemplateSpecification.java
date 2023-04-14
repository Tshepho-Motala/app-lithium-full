package lithium.service.mail.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.mail.data.entities.Domain;
import lithium.service.mail.data.entities.Domain_;
import lithium.service.mail.data.entities.EmailTemplate;
import lithium.service.mail.data.entities.EmailTemplateRevision;
import lithium.service.mail.data.entities.EmailTemplateRevision_;
import lithium.service.mail.data.entities.EmailTemplate_;

public class EmailTemplateSpecification {
	public static Specification<EmailTemplate> domainName(String domainName) {
		return new Specification<EmailTemplate>() {
			@Override
			public Predicate toPredicate(Root<EmailTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<EmailTemplate, Domain> joinDomain = root.join(EmailTemplate_.domain, JoinType.INNER);
				Predicate p = cb.equal(joinDomain.get(Domain_.name), domainName);
				return p;
			}
		};
	}
	
	public static Specification<EmailTemplate> any(String value) {
		return new Specification<EmailTemplate>() {
			@Override
			public Predicate toPredicate(Root<EmailTemplate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(EmailTemplate_.name)), "%" + value.toUpperCase() + "%");
				p = cb.or(p, cb.like(cb.upper(root.get(EmailTemplate_.lang)), "%" + value.toUpperCase() + "%"));
				p = cb.or(p, cb.like(cb.upper(root.get(EmailTemplate_.enabled).as(String.class)), "%" + value.toUpperCase() + "%"));
				Join<EmailTemplate, EmailTemplateRevision> joinRev = root.join(EmailTemplate_.current, JoinType.INNER);
				p = cb.or(p, cb.like(cb.upper(joinRev.get(EmailTemplateRevision_.subject)), "%" + value.toUpperCase() + "%"));
				return p;
			}
		};
	}
}