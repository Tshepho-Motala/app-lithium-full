package lithium.service.casino.data.specifications;

import lithium.service.casino.data.entities.Bonus;
import lithium.service.casino.data.entities.BonusRevision_;
import lithium.service.casino.data.entities.Bonus_;
import lithium.service.casino.data.entities.Domain;
import lithium.specification.JoinableSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Slf4j
public class BonusSpecification {
	
	public static Specification<Bonus> table(final String search, final List<Domain> domains, final List<Boolean> status,
											 final List<Integer> bonusType, boolean deleted) {
		return new JoinableSpecification<Bonus>() {
			@Override
			public Predicate toPredicate(Root<Bonus> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				log.debug("search: "+search+" domains: "+domains+" status: "+status+" type: "+bonusType+" deleted: "+deleted);
				if (status != null) {
					return cb.or(
						cb.and(
							cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
							cb.like(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusCode), search.toLowerCase() + "%"),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.domain).in(domains)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.enabled).in(status)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusType).in(bonusType)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(deleted))
						),
						cb.and(
							cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
							cb.like(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusName), search.toLowerCase() + "%"),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.domain).in(domains)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.enabled).in(status)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusType).in(bonusType)), cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(deleted, null))
						),
						cb.and(
							cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
							cb.like(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusCode), search.toLowerCase() + "%"),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.domain).in(domains)),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.enabled).in(status)),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusType).in(bonusType)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(deleted))
						),
						cb.and(
							cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
							cb.like(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusName), search.toLowerCase() + "%"),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.domain).in(domains)),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.enabled).in(status)),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusType).in(bonusType)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(deleted))
						)
					);
				} else {
					return cb.or(
						cb.and(
							cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
							cb.like(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusCode), search.toLowerCase() + "%"),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.domain).in(domains)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusType).in(bonusType)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(deleted))
						),
						cb.and(
							cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
							cb.like(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusName), search.toLowerCase() + "%"),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.domain).in(domains)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusType).in(bonusType)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(deleted))
						),
						cb.and(
							cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
							cb.like(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusCode), search.toLowerCase() + "%"),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.domain).in(domains)),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusType).in(bonusType)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(deleted))
						),
						cb.and(
							cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
							cb.like(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusName), search.toLowerCase() + "%"),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.domain).in(domains)),
							cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusType).in(bonusType)),
							cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(deleted))
						)
					);
				}
			}
		};
	}
	
	public static Specification<Bonus> searchAll(final String search) {
		return new JoinableSpecification<Bonus>() {
			@Override
			public Predicate toPredicate(Root<Bonus> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
					cb.and(
						cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
						cb.like(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusCode), search.toLowerCase() + "%")
					),
					cb.and(
						cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
						cb.like(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.bonusName), search.toLowerCase() + "%")
					),
						cb.and(
								cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
								cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(false))
						),
					cb.and(
						cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
						cb.like(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusCode), search.toLowerCase() + "%")
					),
					cb.and(
						cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
						cb.like(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.bonusName), search.toLowerCase() + "%")
					),
						cb.and(
								cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
								cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.deleted).in(false))
						)
				);
			}
		};
	}
	
	public static Specification<Bonus> domainIn(final List<Domain> domains) {
		return new JoinableSpecification<Bonus>() {
			@Override
			public Predicate toPredicate(Root<Bonus> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
					cb.and(
						cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
						cb.isTrue(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.domain).in(domains))
					),
					cb.and(
						cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
						cb.isTrue(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.domain).in(domains))
					)
				);
			}
		};
	}
	
	public static Specification<Bonus> status(final String status) {
		return new JoinableSpecification<Bonus>() {
			@Override
			public Predicate toPredicate(Root<Bonus> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
					cb.and(
						cb.isNotNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
						cb.equal(this.joinList(root, Bonus_.current, JoinType.LEFT).get(BonusRevision_.enabled), status)
					),
					cb.and(
						cb.isNull(this.joinList(root, Bonus_.current, JoinType.LEFT)),
						cb.equal(this.joinList(root, Bonus_.edit, JoinType.LEFT).get(BonusRevision_.enabled), status)
					)
				);
			}
		};
	}
}
