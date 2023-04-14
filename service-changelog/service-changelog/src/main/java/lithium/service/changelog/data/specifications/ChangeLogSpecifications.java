package lithium.service.changelog.data.specifications;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lithium.service.changelog.data.entities.Category;
import lithium.service.changelog.data.entities.Category_;
import lithium.service.changelog.data.entities.ChangeLogEntity_;
import lithium.service.changelog.data.entities.ChangeLogType_;
import lithium.service.changelog.data.entities.SubCategory;
import lithium.service.changelog.data.entities.SubCategory_;
import org.springframework.data.jpa.domain.Specification;

import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogEntity;
import lithium.service.changelog.data.entities.ChangeLogType;
import lithium.service.changelog.data.entities.ChangeLog_;
import lithium.service.changelog.data.entities.User;
import lithium.service.changelog.data.entities.User_;

public class ChangeLogSpecifications {
	public static Specification<ChangeLog> withIdAndEntitiesAndTypes(Long entityRecordId, List<ChangeLogEntity> entities, List<ChangeLogType> types) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(ChangeLog_.entityRecordId), entityRecordId);
				Join<ChangeLog, ChangeLogEntity> joinEntity = root.join(ChangeLog_.entity, JoinType.INNER);
				Predicate o = null;
				for (int i = 0; i < entities.size(); i++) {
					if (o == null) {
						o = cb.like(joinEntity.get(ChangeLogEntity_.name), entities.get(i).getName());;
					} else {
						o = cb.or(o, cb.like(joinEntity.get(ChangeLogEntity_.name), entities.get(i).getName()));
					}
				}
				p = cb.and(p, o);
				p = cb.and(p, root.get(ChangeLog_.type).in(types));
				return p;
			}
		};
	}

	
	public static Specification<ChangeLog> any(String value) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(ChangeLog_.comments), value + "%");
				Join<ChangeLog, User> joinUser = root.join(ChangeLog_.authorUser, JoinType.INNER);
				p = cb.or(p, cb.like(joinUser.get(User_.guid), value + "%"));
				return p;
			}
		};
	}

	public static Specification<ChangeLog> changeDateRangeStart(final Date changeDateRangeStart) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(ChangeLog_.changeDate).as(Date.class), changeDateRangeStart);
				return p;
			}
		};
	}

	public static Specification<ChangeLog> changeDateRangeEnd(final Date changeDateRangeEnd) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(ChangeLog_.changeDate).as(Date.class), changeDateRangeEnd);
				return p;
			}
		};
	}

	public static Specification<ChangeLog> priorityFrom(final String priority) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.greaterThanOrEqualTo(root.get(ChangeLog_.priority), Integer.parseInt(priority));
				return p;
			}
		};
	}

	public static Specification<ChangeLog> priorityTo(final String priority) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.lessThanOrEqualTo(root.get(ChangeLog_.priority), Integer.parseInt(priority));
				return p;
			}
		};
	}

	public static Specification<ChangeLog> pinned(final String pinned) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(ChangeLog_.pinned), Boolean.parseBoolean(pinned));
				return p;
			}
		};
	}

	public static Specification<ChangeLog> entities(final String[] entities) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<ChangeLog, ChangeLogEntity> entityJoin = root.join(ChangeLog_.entity, JoinType.INNER);
				Predicate p = entityJoin.get(ChangeLogEntity_.name).in(entities);
				return p;
			}
		};
	}

	public static Specification<ChangeLog> types(final String[] types) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<ChangeLog, ChangeLogType> typeJoin = root.join(ChangeLog_.type, JoinType.INNER);
				Predicate p = typeJoin.get(ChangeLogType_.name).in(types);
				return p;
			}
		};
	}

	public static Specification<ChangeLog> entryRecordId(final String entryRecordId) {
		return new Specification<ChangeLog>() {
			@Override
			public Predicate toPredicate(Root<ChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get(ChangeLog_.entityRecordId), Integer.parseInt(entryRecordId));
				return p;
			}
		};
	}

	public static Specification<ChangeLog> categories(final String[] categories) {
		return (root, query, cb) -> {
			Join<ChangeLog, Category> typeJoin = root.join(ChangeLog_.category, JoinType.INNER);
			return typeJoin.get(Category_.name).in(categories);
		};
	}

	public static Specification<ChangeLog> subCategories(final String[] subCategories) {
		return (root, query, cb) -> {
			Join<ChangeLog, SubCategory> typeJoin = root.join(ChangeLog_.subCategory, JoinType.INNER);
			return typeJoin.get(SubCategory_.name).in(subCategories);
		};
	}

	public static Specification<ChangeLog> deleted(final String deleted) {
		return (root, query, cb) -> cb.equal(root.get(ChangeLog_.deleted), Boolean.parseBoolean(deleted));
	}

	public static Specification<ChangeLog> domain(Long[] domainId) {
		return (root,query,cb)-> root.get(ChangeLog_.domain).in(domainId);
    }
}
