package lithium.service.affiliate.provider.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.affiliate.provider.data.entities.Ad;
import lithium.service.affiliate.provider.data.entities.AdRevision;
import lithium.service.affiliate.provider.data.entities.AdRevision_;
import lithium.service.affiliate.provider.data.entities.Ad_;
import lithium.service.affiliate.provider.data.entities.Brand;
import lithium.service.affiliate.provider.data.entities.Brand_;

public class AdSpecifications {


	public static Specification<Ad> findByBrandMachineName(final String brandMachineName) {
		return new Specification<Ad>() {
			@Override
			public Predicate toPredicate(Root<Ad> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Ad, AdRevision> joinCurrent = root.join(Ad_.current, JoinType.INNER);
				Join<AdRevision, Brand> joinBrand = joinCurrent.join(AdRevision_.brand, JoinType.INNER);
				Predicate p = cb.equal(joinBrand.get(Brand_.machineName), brandMachineName);
				return p;
			}
		};
	}
	

	public static Specification<Ad> findByAny(final String search) {
		return new Specification<Ad>() {
			@Override
			public Predicate toPredicate(Root<Ad> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get(Ad_.name), "%" + search + "%");
				return p;
			}
		};
	}


	public static Specification<Ad> findByType(final Integer adType) {
		return new Specification<Ad>() {
			@Override
			public Predicate toPredicate(Root<Ad> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Ad, AdRevision> joinCurrent = root.join(Ad_.current, JoinType.INNER);
				Predicate p = cb.equal(joinCurrent.get(AdRevision_.type), adType);
				return p;
			}
		};
	}

}