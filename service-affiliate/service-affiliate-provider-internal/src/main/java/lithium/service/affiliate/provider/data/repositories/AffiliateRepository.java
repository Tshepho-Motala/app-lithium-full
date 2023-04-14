package lithium.service.affiliate.provider.data.repositories;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Affiliate;

public interface AffiliateRepository extends PagingAndSortingRepository<Affiliate, Long> {
	
	public Affiliate findByUserGuid(String userGuid);

	public Optional<Affiliate> findByGuid(String affiliateGuid);
	
}