package lithium.service.report.games.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.report.games.data.entities.ReportRun;
import lithium.service.report.games.data.entities.ReportRunResults;
import lithium.service.report.games.data.entities.ReportRunResults_;
import lithium.service.report.games.data.entities.ReportRun_;
import lithium.service.report.games.data.entities.StringValue_;
import lithium.specification.JoinableSpecification;

public class ReportRunResultSpecifications {
	
	public Specification<ReportRunResults> any(final String search) {
		return new JoinableSpecification<ReportRunResults>() {
			@Override
			public Predicate toPredicate(Root<ReportRunResults> root, CriteriaQuery<?> query, CriteriaBuilder cb) {	
				return cb.or(
					cb.like(this.joinList(root, ReportRunResults_.name, JoinType.INNER).get(StringValue_.value), search.toLowerCase() + "%"),
					cb.like(this.joinList(root, ReportRunResults_.providerName, JoinType.INNER).get(StringValue_.value), search.toLowerCase() + "%")
				);
			}
		};
	}
	
	public Specification<ReportRunResults> reportRunId(final Long reportRunId) {
		return new Specification<ReportRunResults>() {
			@Override
			public Predicate toPredicate(Root<ReportRunResults> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<ReportRunResults, ReportRun> joinReportRun = root.join(ReportRunResults_.reportRun, JoinType.INNER);
				return cb.equal(joinReportRun.get(ReportRun_.id), reportRunId);
			}
		};
	}
}
