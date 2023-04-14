package lithium.service.casino.search.data.repositories.casino;

import lithium.service.casino.data.entities.BetRound;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("casino.BetRoundRepository")
public interface BetRoundRepository extends PagingAndSortingRepository<BetRound, Long>, JpaSpecificationExecutor<BetRound> {
}
