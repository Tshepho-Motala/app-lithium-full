package lithium.service.settlement.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.settlement.data.entities.Settlement;
import lithium.service.settlement.data.entities.SettlementEntry;
import lithium.service.settlement.data.entities.SettlementEntry_;

public class SettlementEntrySpecifications {
	public static Specification<SettlementEntry> settlement(final Settlement settlement) {
		return new Specification<SettlementEntry>() {
			@Override
			public Predicate toPredicate(Root<SettlementEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(SettlementEntry_.settlement), settlement);
				return p;
			}
		};
	}
	
	public static Specification<SettlementEntry> any(final String search) {
		return new Specification<SettlementEntry>() {
			@Override
			public Predicate toPredicate(Root<SettlementEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(SettlementEntry_.description)), "%" + search.toUpperCase() + "%");
				p = cb.or(p, cb.like(root.get(SettlementEntry_.amount).as(String.class), "%" + search + "%"));
				return p;
			}
		};
	}
}
