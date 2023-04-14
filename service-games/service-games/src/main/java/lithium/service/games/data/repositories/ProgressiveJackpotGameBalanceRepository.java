package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotGameBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProgressiveJackpotGameBalanceRepository extends PagingAndSortingRepository<ProgressiveJackpotGameBalance, Long>,
        JpaRepository<ProgressiveJackpotGameBalance, Long> {
    Page<ProgressiveJackpotGameBalance> findByGameDomainName(String domainName, Pageable pageable);
    List<ProgressiveJackpotGameBalance> findByGameDomainName(String domainName);
}
