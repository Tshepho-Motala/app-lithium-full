package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.entities.DomainSummaryV2MigrationProgress;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainSummaryV2MigrationProgressRepository extends PagingAndSortingRepository<DomainSummaryV2MigrationProgress,Long> {
    default DomainSummaryV2MigrationProgress findOne(Long id) {
        return findById(id).orElse(null);
    }
}
