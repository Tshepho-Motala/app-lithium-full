package lithium.service.cashier.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.ProcessorType;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.entities.ProcessorProperty;
import lithium.service.cashier.data.repositories.ProcessorPropertyRepository;
import lithium.service.cashier.data.repositories.ProcessorRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessorService {
	@Autowired
	private ProcessorRepository processorRepository;
	@Autowired
	private ProcessorPropertyRepository processorPropertyRepository;
	@Autowired
	private ProcessorMethodService processorMethodService;
	
	public Processor find(Long id) {
		return processorRepository.findOne(id);
	}
	public Processor findByCode(String code) {
		return processorRepository.findByCode(code);
	}
	
	public List<Processor> findByMethodId(Long methodId, ProcessorType type) {
		log.debug("List Processors for method : "+methodId);
		switch (type) {
			case WITHDRAW:
				return processorMethodService.findByMethodId(methodId).stream()
					.filter(processorMethod -> { return (processorMethod.getProcessor().getWithdraw())?true:false;})
					.map(processorMethod -> { return processorMethod.getProcessor(); })
					.collect(Collectors.toList());
			case DEPOSIT:
				return processorMethodService.findByMethodId(methodId).stream()
					.filter(processorMethod -> { return (processorMethod.getProcessor().getDeposit())?true:false;})
					.map(processorMethod -> { return processorMethod.getProcessor(); })
					.collect(Collectors.toList());
			default:
				return new ArrayList<>();
		}
	}
	
//	public Processor findRandomWeightedProcessor(Long methodId) {
//		List<Processor> processors = processorRepository.findByMethodId(methodId);
//		RandomCollection<Processor> randomWeightedProcessor = new RandomCollection<>();
//		for (Processor processor:processors) {
//			randomWeightedProcessor.add(1d, processor);
//		}
//		return randomWeightedProcessor.next();
//	}
	
	public Processor save(String name, String url, Boolean enabled, Boolean deposit, Boolean withdraw, Fees fees, Limits limits) {
		return save(
			Processor.builder()
			.name(name)
			.url(url)
			.enabled(enabled)
			.deposit(deposit)
			.withdraw(withdraw)
			.fees(fees)
			.limits(limits)
			.build()
		);
	}
	
	public Processor save(Processor processor) {
		return processorRepository.save(processor);
	}
	
	/// Processor Properties
	public ProcessorProperty findProperty(Long id) {
		return processorPropertyRepository.findOne(id);
	}
	public ProcessorProperty findPropertyByProcessorIdAndName(Processor processor, String name) {
		return processorPropertyRepository.findByProcessorAndName(processor, name);
	}
	public ProcessorProperty saveProperty(ProcessorProperty property) {
		return processorPropertyRepository.save(property);
	}
	public ProcessorProperty saveProperty(Processor processor, 
			lithium.service.cashier.client.objects.ProcessorProperty property) {
		return processorPropertyRepository.save(
			ProcessorProperty.builder()
			.processor(processor)
			.name(property.getName())
			.defaultValue(property.getDefaultValue())
			.type(property.getType())
			.description(property.getDescription())
			.build()
		);
	}
}
