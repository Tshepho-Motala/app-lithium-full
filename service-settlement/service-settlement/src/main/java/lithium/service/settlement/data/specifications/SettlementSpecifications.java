package lithium.service.settlement.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.settlement.data.entities.BatchSettlements;
import lithium.service.settlement.data.entities.BatchSettlements_;
import lithium.service.settlement.data.entities.Entity;
import lithium.service.settlement.data.entities.Entity_;
import lithium.service.settlement.data.entities.Settlement;
import lithium.service.settlement.data.entities.Settlement_;

public class SettlementSpecifications {
	public static Specification<Settlement> entityUuidIs(final String entityUuid) {
		return new Specification<Settlement>() {
			@Override
			public Predicate toPredicate(Root<Settlement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Settlement, Entity> entityJoin = root.join(Settlement_.entity, JoinType.INNER);
				Predicate p = cb.equal(entityJoin.get(Entity_.uuid), entityUuid);
				return p;
			}
		};
	}
	
	public static Specification<Settlement> any(final String search) {
		return new Specification<Settlement>() {
			@Override
			public Predicate toPredicate(Root<Settlement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.like(cb.upper(root.get(Settlement_.createdBy)), "%" + search.toUpperCase() + "%");
			}
		};
	}
	
	public static Specification<Settlement> inBatch(final Long batchId) {
		return new Specification<Settlement>() {
			@Override
			public Predicate toPredicate(Root<Settlement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Settlement, BatchSettlements> batchJoin = root.join(Settlement_.batchSettlements, JoinType.INNER);
				Predicate p = cb.equal(batchJoin.get(BatchSettlements_.id), batchId);
				return p;
			}
		};
	}
}
