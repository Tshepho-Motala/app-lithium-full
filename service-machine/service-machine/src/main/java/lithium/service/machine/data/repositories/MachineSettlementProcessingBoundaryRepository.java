package lithium.service.machine.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.MachineSettlementProcessingBoundary;

public interface MachineSettlementProcessingBoundaryRepository extends PagingAndSortingRepository<MachineSettlementProcessingBoundary, Long>, JpaSpecificationExecutor<MachineSettlementProcessingBoundary> {

    default MachineSettlementProcessingBoundary findOne(Long id) {
        return findById(id).orElse(null);
    }

}
