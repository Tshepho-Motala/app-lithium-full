package lithium.service.machine.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.MachineObserver;

public interface MachineObserverRepository extends PagingAndSortingRepository<MachineObserver, Long>, JpaSpecificationExecutor<MachineObserver> {
	MachineObserver findByObserverGuid(String guid);
	List<MachineObserver> findByMachine(Machine machine);
	MachineObserver findByMachineAndObserverGuid(Machine machine, String observerGuid);
}