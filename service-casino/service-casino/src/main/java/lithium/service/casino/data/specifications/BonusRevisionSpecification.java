package lithium.service.casino.data.specifications;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lithium.service.casino.data.entities.Domain_;
import org.springframework.data.jpa.domain.Specification;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.BonusRevision_;
import lithium.service.casino.data.entities.Bonus_;
import lithium.specification.JoinableSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BonusRevisionSpecification {
	
	public static Specification<BonusRevision> byBonusId(final String search, final Long bonusId) {
		return new JoinableSpecification<BonusRevision>() {
			@Override
			public Predicate toPredicate(Root<BonusRevision> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				log.debug("search: "+search+" bonusId: "+bonusId);
//				query = query.orderBy(cb.desc(root.get(BonusRevision_.id)));
				return cb.and(
					cb.equal(this.joinList(root, BonusRevision_.bonus, JoinType.LEFT).get(Bonus_.id), bonusId),
					cb.or(
						cb.like(root.get(BonusRevision_.bonusCode), search.toLowerCase()+"%"),
						cb.like(root.get(BonusRevision_.bonusName), search.toLowerCase()+"%")
					)
				);
			}
		};
	}

	public static Specification<BonusRevision> byDomains(final List<String> activeDomains) {
		return (root, query, cb) -> root.join(BonusRevision_.domain, JoinType.INNER).get(Domain_.name).in(activeDomains);
	}
}
