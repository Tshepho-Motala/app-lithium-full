package lithium.service.casino.data.repositories;


import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.BonusToken;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BonusTokenRepository extends PagingAndSortingRepository<BonusToken, Long> {
	List<BonusToken> findByBonusRevision(BonusRevision bonusRevision);
}
