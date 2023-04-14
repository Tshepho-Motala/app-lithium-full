package lithium.service.settlement.data.specifications;

import lithium.service.settlement.data.entities.BatchSettlements;
import lithium.service.settlement.data.entities.BatchSettlements_;
import lithium.service.settlement.data.entities.Domain;
import lithium.service.settlement.data.entities.Domain_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class BatchSettlementsSpecifications {
	public static Specification<BatchSettlements> domains(final List<String> domains) {
		return new Specification<BatchSettlements>() {
			@Override
			public Predicate toPredicate(Root<BatchSettlements> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<BatchSettlements, Domain> domainJoin = root.join(BatchSettlements_.domain, JoinType.INNER);
				Predicate p = domainJoin.get(Domain_.name).in(domains);
				return p;
			}
		};
	}
	
	public static Specification<BatchSettlements> any(final String value) {
		return new Specification<BatchSettlements>() {
			@Override
			public Predicate toPredicate(Root<BatchSettlements> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
