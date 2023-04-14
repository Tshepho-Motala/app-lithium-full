package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.entities.ProcessorMethod;

public interface ProcessorMethodRepository extends PagingAndSortingRepository<ProcessorMethod, Long> {
	List<ProcessorMethod> findByProcessor(Processor processor);
	List<ProcessorMethod> findByMethod(Method method);
	List<ProcessorMethod> findByProcessorId(Long processorId);
	List<ProcessorMethod> findByMethodId(Long methodId);
	
	ProcessorMethod findByProcessorAndMethod(Processor processor, Method method);
}