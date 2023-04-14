package lithium.service.cashier.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.client.objects.ProcessedProcessorProperty;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.objects.ProcessedProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InternalMethodsService {
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
//	@Autowired
//	private DomainMethodProcessorProfileService domainMethodProcessorProfileService;
//	@Autowired
//	private UserService userService;
	
//	public ProcessedMethod findProcessedProcessor(DomainMethod domainMethod, String userGuid) {
//		User user = userService.findOrCreate(userGuid);
//		ProcessedMethod processedMethod = buildProcessedMethod(domainMethod);
//		processedMethod = domainMethodProcessorsLinkedToProfile(user, processedMethod);
//		return processedMethod;
//	}
	
	public List<ProcessedProcessorProperty> findProcessedProcessorProperties(DomainMethodProcessor domainMethodProcessor) {
		return mapDomainMethodProcessorProperty(domainMethodProcessorService.propertiesWithDefaults(domainMethodProcessor.getId()));
	}
	
//	public ProcessedMethod domainMethodProcessorsLinkedToProfile(User user, ProcessedMethod processedMethod) {
//		if (user.getProfile() == null) return processedMethod;
//		List<DomainMethodProcessorProfile> domainMethodProcessorProfiles = domainMethodProcessorProfileService.findByProfile(user.getProfile().getId());
//		log.info("domainMethodProcessorProfiles : "+domainMethodProcessorProfiles);
//		domainMethodProcessorProfiles.stream()
//		.forEach(dmpp -> {
//			ProcessedProcessor processedProcessor = ProcessedProcessor.builder()
//				.processorId(dmpp.getDomainMethodProcessor().getProcessor().getId())
//				.domainMethodProcessorId(dmpp.getDomainMethodProcessor().getId())
//				.name(dmpp.getDomainMethodProcessor().getProcessor().getName())
//				.url(dmpp.getDomainMethodProcessor().getProcessor().getUrl())
//				.weight(dmpp.getWeight())
//				.fees(dmpp.getFees())
//				.limits(dmpp.getLimits())
//				.properties(mapDomainMethodProcessorProperty(domainMethodProcessorService.propertiesWithDefaults(dmpp.getDomainMethodProcessor().getId())))
//				.build();
//			
//			processedMethod.setProcessors(
//				Stream.concat(
//					Arrays.asList(processedProcessor).stream(), processedMethod.getProcessors().stream()
//				).distinct().collect(Collectors.toList())
//			);
//			
//		});
//		return processedMethod;
//	}
	
//	public ProcessedMethod buildProcessedMethod(DomainMethod domainMethod) {
//		log.trace("buildProcessedMethod");
//		
//		return ProcessedMethod.builder()
//			.methodId(domainMethod.getMethod().getId())
//			.domainMethodId(domainMethod.getId())
//			.name(domainMethod.getName())
//			.priority(domainMethod.getPriority())
//			.domain(domainMethod.getDomain())
//			.image(domainMethod.getImage())
//			.processors(mapProcessedProcessor(domainMethodProcessors(domainMethod)))
//			.build();
//	}
	
	public List<ProcessedProcessor> mapProcessedProcessor(List<DomainMethodProcessor> domainMethodProcessors) {
		return domainMethodProcessors.stream()
		.filter(dmps -> (dmps.getEnabled() && dmps.getProcessor().getEnabled() && !dmps.getDeleted()))
		.map(dmps -> {
			log.info("Domain method ID: "+dmps.getId() + "Processor ID: "+ dmps.getProcessor().getId()+" with Processor name: "+dmps.getProcessor().getName());
			return ProcessedProcessor.builder()
			.processorId(dmps.getProcessor().getId())
			.domainMethodProcessorId(dmps.getId())
			.name(dmps.getProcessor().getName())
			.url(dmps.getProcessor().getUrl())
			.weight(dmps.getWeight())
			.fees((dmps.getFees()!=null)?dmps.getFees():dmps.getProcessor().getFees())
			.limits((dmps.getLimits()!=null)?dmps.getLimits():dmps.getProcessor().getLimits())
			.properties(mapDomainMethodProcessorProperty(domainMethodProcessorService.propertiesWithDefaults(dmps.getId())))
			.build();
		})
		.collect(Collectors.toList());
	}
	
	public List<ProcessedProcessorProperty> mapDomainMethodProcessorProperty(List<DomainMethodProcessorProperty> propertiesWithDefaults) {
		return propertiesWithDefaults.stream()
			.map(dmpp -> {
				return ProcessedProcessorProperty.builder()
					.id(dmpp.getId())
					.name(dmpp.getProcessorProperty().getName())
					.value(dmpp.getValue())
					.type(dmpp.getProcessorProperty().getType())
					.description(dmpp.getProcessorProperty().getDescription())
					.build();
			})
			.collect(Collectors.toList());
	}
	
//	private List<DomainMethodProcessor> domainMethodProcessors(DomainMethod domainMethod) {
//		List<DomainMethodProcessor> domainMethodProcessors = domainMethodProcessorService.list(domainMethod.getId()).stream()
//		.filter(dmp -> ((dmp.getProcessor().getEnabled()) && (dmp.getEnabled()) && (!dmp.getDeleted())))
//		.collect(Collectors.toList());
//		log.info("DomainMethodProcessors (domainMethod:"+domainMethod+") Found : "+domainMethodProcessors);
//		return domainMethodProcessors;
//	}
}
