package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.BetRound;
import lithium.service.casino.data.entities.Provider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

import java.util.List;

public interface BetRoundRepository extends PagingAndSortingRepository<BetRound, Long>, JpaSpecificationExecutor<BetRound> {
	BetRound findByProviderAndGuid(Provider provider, String guid);
	List<BetRound> findByCreatedDateLessThan(Long createdDate, Pageable pageable);
	List<BetRound> findBetRoundByUserIdOrderByIdAsc(long id);
}
