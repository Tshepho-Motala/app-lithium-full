package lithium.service.affiliate.provider.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.CampaignAd;

public interface CampaignAdRepository extends PagingAndSortingRepository<CampaignAd, Long>, JpaSpecificationExecutor<CampaignAd> {

	public Page<CampaignAd> findByCampaignId(Long id, Pageable pageRequest);
	public CampaignAd findByGuid(String guid);
	
}