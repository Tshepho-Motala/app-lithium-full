package lithium.service.machine.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.entities.MachineSettlement;
import lithium.service.machine.data.entities.MachineSettlement_;

public class MachineSettlementSpecifications {
	public static Specification<MachineSettlement> any(final String search) {
		return new Specification<MachineSettlement>() {
			@Override
			public Predicate toPredicate(Root<MachineSettlement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(cb.upper(root.get(MachineSettlement_.createdBy)), "%" + search.toUpperCase() + "%");
				return p;
			}
		};
	}
	
	public static Specification<MachineSettlement> domain(final Domain domain) {
		return new Specification<MachineSettlement>() {
			@Override
			public Predicate toPredicate(Root<MachineSettlement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(MachineSettlement_.domain), domain);
			}
		};
	}
}
