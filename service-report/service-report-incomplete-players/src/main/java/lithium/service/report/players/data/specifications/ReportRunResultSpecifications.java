package lithium.service.report.players.data.specifications;

import lithium.service.report.players.data.entities.ReportRun;
import lithium.service.report.players.data.entities.ReportRunResults;
import lithium.service.report.players.data.entities.ReportRunResults_;
import lithium.service.report.players.data.entities.ReportRun_;
import lithium.service.report.players.data.entities.StringValue_;
import lithium.specification.JoinableSpecification;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ReportRunResultSpecifications {
	
	public Specification<ReportRunResults> any(final String search) {
		return new JoinableSpecification<ReportRunResults>() {
			@Override
			public Predicate toPredicate(Root<ReportRunResults> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				return cb.or(	
					cb.like(this.joinList(root, ReportRunResults_.username, JoinType.INNER).get(StringValue_.value), search.toLowerCase() + "%"),
					cb.like(this.joinList(root, ReportRunResults_.email, JoinType.INNER).get(StringValue_.value), search.toLowerCase() + "%"),
					cb.like(this.joinList(root, ReportRunResults_.firstName, JoinType.INNER).get(StringValue_.value), search + "%"),
					cb.like(this.joinList(root, ReportRunResults_.lastName, JoinType.INNER).get(StringValue_.value), search + "%"),
					cb.like(this.joinList(root, ReportRunResults_.cellphoneNumber, JoinType.INNER).get(StringValue_.value), search + "%"),
					cb.like(this.joinList(root, ReportRunResults_.gender, JoinType.INNER).get(StringValue_.value), search + "%"),
					cb.like(this.joinList(root, ReportRunResults_.stage, JoinType.INNER).get(StringValue_.value), search + "%")
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
