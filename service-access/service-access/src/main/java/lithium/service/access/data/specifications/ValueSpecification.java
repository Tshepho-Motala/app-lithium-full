package lithium.service.access.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.List_;
import lithium.service.access.data.entities.Value;
import lithium.service.access.data.entities.Value_;

public class ValueSpecification {
	public static Specification<Value> findByListId(Long listId, String search) {
		return new Specification<Value>() {
			@Override
			public Predicate toPredicate(Root<Value> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Value, List> joinList = root.join(Value_.list, JoinType.LEFT);
				Predicate p = cb.and(
					cb.equal(joinList.get(List_.id), listId),
					cb.like(cb.upper(root.get(Value_.data)), "%" + search.toUpperCase() + "%")
				);
				return p;
			}
		};
	}
}