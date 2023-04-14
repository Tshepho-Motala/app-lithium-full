package lithium.service.cashier.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.entities.ProcessorProperty;

public interface ProcessorPropertyRepository extends PagingAndSortingRepository<ProcessorProperty, Long> {
	ProcessorProperty findByProcessorAndName(Processor processor, String name);

	default ProcessorProperty findOne(Long id) {
		return findById(id).orElse(null);
	}
}
