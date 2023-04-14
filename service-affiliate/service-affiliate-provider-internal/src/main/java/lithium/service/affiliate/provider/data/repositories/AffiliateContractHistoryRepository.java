package lithium.service.affiliate.provider.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Affiliate;
import lithium.service.affiliate.provider.data.entities.AffiliateContractRevision;

public interface AffiliateContractHistoryRepository extends PagingAndSortingRepository<AffiliateContractRevision, Long> {

	public List<AffiliateContractRevision> findByAffiliateId(long affiliateId);
	
}