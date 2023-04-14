package lithium.service.cashier.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.entities.ProcessorMethod;
import lithium.service.cashier.data.repositories.ProcessorMethodRepository;

@Service
public class ProcessorMethodService {
	@Autowired
	private ProcessorMethodRepository repo;
	
	public ProcessorMethod findByProcessorAndMethod(Processor processor, Method method) {
		return repo.findByProcessorAndMethod(processor, method);
	}
	
	public ProcessorMethod save(ProcessorMethod pm) {
		return repo.save(pm);
	}
	
	public List<ProcessorMethod> findByProcessor(Processor processor) {
		return repo.findByProcessor(processor);
	}
	
	public List<ProcessorMethod> findByMethod(Method method) {
		return repo.findByMethod(method);
	}

	public List<ProcessorMethod> findByProcessorId(Long processorId) {
		return repo.findByProcessorId(processorId);
	}
	
	public List<ProcessorMethod> findByMethodId(Long methodId) {
		return repo.findByMethodId(methodId);
	}

}
