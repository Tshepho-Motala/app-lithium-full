package lithium.service.entity.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.entity.data.entities.Domain;
import lithium.service.entity.data.entities.Entity;
import lithium.service.entity.data.entities.EntityType;
import lithium.service.entity.data.entities.Entity_;

public class EntitySpecifications {

	public static Specification<Entity> any(final String search) {
		return new Specification<Entity>() {
			@Override
			public Predicate toPredicate(Root<Entity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//TODO the upper will cause the index not to be used. We need to either implement hibernate-search with jms master slave or
				//     a copy of every search column stored in uppercase (given the complexity of hibernate-search and the fact that storage
				//     will be as much or even more if we go that route, the additional column doesn't sound like such a bad idea...
				return cb.like(cb.upper(root.get(Entity_.name)), search.toUpperCase() + "%");
			}
		};
	}
	
	public static Specification<Entity> domain(final Domain domain) {
		return new Specification<Entity>() {
			@Override
			public Predicate toPredicate(Root<Entity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(Entity_.domain), domain);
			}
		};
	}
	
	public static Specification<Entity> entityType(final EntityType entityType) {
		return new Specification<Entity>() {
			@Override
			public Predicate toPredicate(Root<Entity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get(Entity_.entityType), entityType);
			}
		};
	}

	
}
