package lithium.service.affiliate.provider.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Campaign;

public interface CampaignRepository extends PagingAndSortingRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {

	public Campaign findByGuid(String guid);

	default Campaign findOne(Long id) {
		return findById(id).orElse(null);
	}


}