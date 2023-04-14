package lithium.service.affiliate.provider.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.affiliate.provider.data.entities.Affiliate;
import lithium.service.affiliate.provider.data.entities.Affiliate_;
import lithium.service.affiliate.provider.data.entities.Brand;
import lithium.service.affiliate.provider.data.entities.Brand_;
import lithium.service.affiliate.provider.data.entities.Campaign;
import lithium.service.affiliate.provider.data.entities.CampaignRevision;
import lithium.service.affiliate.provider.data.entities.CampaignRevision_;
import lithium.service.affiliate.provider.data.entities.Campaign_;

public class CampaignSpecifications {


	public static Specification<Campaign> findByAffiliate(final String affiliateUserGuid) {
		return new Specification<Campaign>() {
			@Override
			public Predicate toPredicate(Root<Campaign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Campaign, CampaignRevision> joinCurrent = root.join(Campaign_.current, JoinType.INNER);
				Join<CampaignRevision, Affiliate> join = joinCurrent.join(CampaignRevision_.affiliate, JoinType.INNER);
				Predicate p = cb.equal(join.get(Affiliate_.guid), affiliateUserGuid);
				return p;
			}
		};
	}

	public static Specification<Campaign> findByBrand(final String brandMachineName) {
		return new Specification<Campaign>() {
			@Override
			public Predicate toPredicate(Root<Campaign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Campaign, CampaignRevision> joinCurrent = root.join(Campaign_.current, JoinType.INNER);
				Join<CampaignRevision, Brand> joinBrand = joinCurrent.join(CampaignRevision_.brand, JoinType.INNER);
				Predicate p = cb.equal(joinBrand.get(Brand_.machineName), brandMachineName);
				return p;
			}
		};
	}

	public static Specification<Campaign> deleted(final boolean deleted) {
		return new Specification<Campaign>() {
			@Override
			public Predicate toPredicate(Root<Campaign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Campaign, CampaignRevision> joinCurrent = root.join(Campaign_.current, JoinType.INNER);
				Predicate p = cb.equal(joinCurrent.get(CampaignRevision_.deleted), deleted);
				return p;
			}
		};
	}

	public static Specification<Campaign> archived(final boolean archived) {
		return new Specification<Campaign>() {
			@Override
			public Predicate toPredicate(Root<Campaign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Campaign, CampaignRevision> joinCurrent = root.join(Campaign_.current, JoinType.INNER);
				Predicate p = cb.equal(joinCurrent.get(CampaignRevision_.archived), archived);
				return p;
			}
		};
	}
	
	public static Specification<Campaign> any(final String search) {
		return new Specification<Campaign>() {
			@Override
			public Predicate toPredicate(Root<Campaign> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Campaign, CampaignRevision> joinCurrent = root.join(Campaign_.current, JoinType.INNER);
				Predicate p = cb.like(joinCurrent.get(CampaignRevision_.name), search + "%");
				return p;
			}
		};
	}


}