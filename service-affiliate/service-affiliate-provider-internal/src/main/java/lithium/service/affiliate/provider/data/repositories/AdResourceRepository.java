package lithium.service.affiliate.provider.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Ad;
import lithium.service.affiliate.provider.data.entities.AdResource;

public interface AdResourceRepository extends PagingAndSortingRepository<AdResource, Long> {

	AdResource findByAdIdAndFilename(long adId, String filename);
	
}