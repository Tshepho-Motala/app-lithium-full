package lithium.service.report.games.data.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.report.games.data.entities.Report;
import lithium.service.report.games.data.entities.ReportRevision;
import lithium.service.report.games.data.entities.ReportRevision_;
import lithium.service.report.games.data.entities.Report_;

public class ReportSpecifications {
	public static Specification<Report> any(final String search) {
		return new Specification<Report>() {
			@Override
			public Predicate toPredicate(Root<Report> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//TODO the upper will cause the index not to be used. We need to either implement hibernate-search with jms master slave or
				//     a copy of every search column stored in uppercase (given the complexity of hibernate-search and the fact that storage
				//     will be as much or even more if we go that route, the additional column doesn't sound like such a bad idea...
				
				Join<Report, ReportRevision> joinRevision = root.join(Report_.current, JoinType.INNER);
				
				return cb.or(
					cb.like(cb.upper(joinRevision.get(ReportRevision_.name)), search.toUpperCase() + "%"),
					cb.like(cb.upper(joinRevision.get(ReportRevision_.description)), search.toUpperCase() + "%")
				);
			}
		};
	}
	
	public static Specification<Report> domainIn(final List<String> domains) {
		return new Specification<Report>() {
			@Override
			public Predicate toPredicate(Root<Report> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return root.get((Report_.domainName)).in(domains);
			}
		};
	}
}