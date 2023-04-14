package service.casino.provider.cataboom.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import service.casino.provider.cataboom.entities.Campaign;
import service.casino.provider.cataboom.entities.Campaign_;


public class CampaignSpecifications {
	public static Specification<Campaign> any(String search) {
		return new Specification<Campaign>() {
			@Override
			public Predicate toPredicate(Root<Campaign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
					cb.like(cb.upper(root.get(Campaign_.campaignName)), "%" + search.toUpperCase() + "%")
				);
			}
		};
	}

		
		public static Specification<Campaign> domain(String domain) {
			return new Specification<Campaign>() {
				@Override
				public Predicate toPredicate(Root<Campaign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Predicate p = cb.equal(cb.upper(root.get(Campaign_.domainName)), domain.toUpperCase());
					return p;
				}
			};
	}
}
