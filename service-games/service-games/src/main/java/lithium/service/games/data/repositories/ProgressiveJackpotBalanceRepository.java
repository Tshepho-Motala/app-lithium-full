package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProgressiveJackpotBalanceRepository extends JpaRepository<ProgressiveJackpotBalance, Long>, JpaSpecificationExecutor<ProgressiveJackpotBalance> {
    List<ProgressiveJackpotBalance> findByGameSupplierDomainName(String domainName);
}
