package lithium.service.machine.data.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.MachineSettlement;

public interface MachineSettlementRepository extends PagingAndSortingRepository<MachineSettlement, Long>, JpaSpecificationExecutor<MachineSettlement> {
	List<MachineSettlement> findByCompletedFalse();
	MachineSettlement findByBatchName(String batchName);
	MachineSettlement findByDateStartAndDateEnd(Date dateStart, Date dateEnd);
}
