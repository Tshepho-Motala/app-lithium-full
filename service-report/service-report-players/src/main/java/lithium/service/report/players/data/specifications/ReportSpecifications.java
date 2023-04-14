package lithium.service.report.players.data.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import lithium.service.report.players.data.entities.ReportAction;
import lithium.service.report.players.data.entities.ReportAction_;
import org.springframework.data.jpa.domain.Specification;

import lithium.service.report.players.data.entities.Report;
import lithium.service.report.players.data.entities.ReportRevision;
import lithium.service.report.players.data.entities.ReportRevision_;
import lithium.service.report.players.data.entities.Report_;

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

	public static Specification<Report> actionIn(final String action){
		return (root, query, cb) -> {

			Join<Report, ReportRevision> revisionReportJoin = root.join(Report_.current, JoinType.INNER);
			Root<ReportAction> reportActionRoot = query.from(ReportAction.class);
			Join<ReportAction, ReportRevision> reportRevisionJoin = reportActionRoot.join(ReportAction_.reportRevision, JoinType.INNER);

			Predicate p = cb.equal(revisionReportJoin.get(ReportRevision_.id), reportRevisionJoin.get(ReportRevision_.id));

			p = cb.and(p, cb.equal(reportActionRoot.get(ReportAction_.actionType), action));
			return p;
		};
	}
}
