package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProgressiveJackpotFeedRepository extends PagingAndSortingRepository<ProgressiveJackpotFeed, Long> {
    Page<ProgressiveJackpotFeed> findByGameSupplierDomainName(String domainName, Pageable pageable);
    List<ProgressiveJackpotFeed> findAllByEnabledIsTrue();
    default ProgressiveJackpotFeed findOne(Long id) {
        return findById(id).orElse(null);
    }

    ProgressiveJackpotFeed findByGameSupplier(GameSupplier gameSupplier);

}
