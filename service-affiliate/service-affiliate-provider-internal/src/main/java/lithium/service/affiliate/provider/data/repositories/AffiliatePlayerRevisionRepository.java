package lithium.service.affiliate.provider.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.AffiliatePlayerRevision;

public interface AffiliatePlayerRevisionRepository extends PagingAndSortingRepository<AffiliatePlayerRevision, Long> {
	
	public List<AffiliatePlayerRevision> findByAffiliatePlayerIdOrderByEffectiveDateDesc(long affiliatePlayerId);

}