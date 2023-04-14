package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.AccountType;
import lithium.service.accounting.domain.summary.storage.entities.SummaryReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryReconciliationRepository extends JpaRepository<SummaryReconciliation, Long> {
    default SummaryReconciliation findOne(Long id) {
        return findById(id).orElse(null);
    }
}
