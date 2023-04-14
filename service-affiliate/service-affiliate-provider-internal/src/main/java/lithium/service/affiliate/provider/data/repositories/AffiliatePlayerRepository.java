package lithium.service.affiliate.provider.data.repositories;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.AffiliatePlayer;

public interface AffiliatePlayerRepository extends PagingAndSortingRepository<AffiliatePlayer, Long> {

	public Optional<AffiliatePlayer> findByPlayerGuid(String guid);
	
}