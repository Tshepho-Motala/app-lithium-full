package lithium.service.machine.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.Machine_;
import lithium.service.machine.data.entities.Status;
import lithium.service.machine.data.entities.Status_;
import lithium.specification.JoinableSpecification;

public class MachineSpecifications {

	public static Specification<Machine> any(final String search) {
		return new JoinableSpecification<Machine>() {
			@Override
			public Predicate toPredicate(Root<Machine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//TODO the upper will cause the index not to be used. We need to either implement hibernate-search with jms master slave or
				//     a copy of every search column stored in uppercase (given the complexity of hibernate-search and the fact that storage
				//     will be as much or even more if we go that route, the additional column doesn't sound like such a bad idea...
				Predicate p = cb.like(cb.upper(root.get(Machine_.name)), search.toUpperCase() + "%");
				p = cb.or(p, cb.like(root.get(Machine_.guid), search + "%"));
				p = cb.or(p, cb.like(root.get(Machine_.id).as(String.class), search + "%"));
				p = cb.or(p, cb.like(this.joinList(root, Machine_.status, JoinType.INNER).get(Status_.name), search.toUpperCase() + "%"));
				return p;
			}
		};
	}
	
	public static Specification<Machine> domain(final Domain domain) {
		return new Specification<Machine>() {
			@Override
			public Predicate toPredicate(Root<Machine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(Machine_.domain), domain);
			}
		};
	}
	
	public static Specification<Machine> status(final Status status) {
		return new Specification<Machine>() {
			@Override
			public Predicate toPredicate(Root<Machine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(Machine_.status), status);
			}
		};
	}

	
}
