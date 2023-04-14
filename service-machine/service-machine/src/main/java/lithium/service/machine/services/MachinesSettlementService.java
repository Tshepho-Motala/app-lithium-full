package lithium.service.machine.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.entities.MachineSettlement;
import lithium.service.machine.data.entities.MachineSettlementProcessingBoundary;
import lithium.service.machine.data.repositories.MachineSettlementProcessingBoundaryRepository;
import lithium.service.machine.data.repositories.MachineSettlementRepository;
import lithium.service.machine.data.specifications.MachineSettlementSpecifications;
import lithium.service.settlement.client.SettlementClient;
import lithium.service.settlement.client.objects.BatchSettlements;

@Service
public class MachinesSettlementService {
	@Autowired MachineSettlementRepository jobRepo;
	@Autowired MachineSettlementProcessingBoundaryRepository jobProcessingBoundaryRepo;
	@Autowired DomainService domainService;
	@Autowired LithiumServiceClientFactory services;
	
	public DataTableResponse<MachineSettlement> table(DataTableRequest request, String domainName) {
		Domain domain = domainService.findOrCreate(domainName);
		
		Specification<MachineSettlement> spec = Specification.where(MachineSettlementSpecifications.domain(domain));
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<MachineSettlement> s = Specification.where(MachineSettlementSpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		
		Page<MachineSettlement> pageList = jobRepo.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<>(request, pageList);
	}
	
	public MachineSettlement create(String domainName, String batchName, Date dateStart, Date dateEnd, String createdBy) throws Exception {
		MachineSettlement byBatchName = jobRepo.findByBatchName(batchName);
		if (byBatchName != null)
			throw new Exception("A machine settlement already with this batch name exists");
		MachineSettlement exists = jobRepo.findByDateStartAndDateEnd(dateStart, dateEnd);
		if (exists != null)
			throw new Exception("A machine settlement job for the period " + dateStart.toString() + " to "
					+ dateEnd.toString() + " and already exists. Batch name: " + exists.getBatchName());
		
		MachineSettlement job = MachineSettlement.builder()
		.batchName(batchName)
		.domain(domainService.findOrCreate(domainName))
		.dateStart(dateStart)
		.dateEnd(dateEnd)
		.createdBy(createdBy)
		.build();
		job = jobRepo.save(job);
		return job;
	}
	
	public MachineSettlement rerun(MachineSettlement job) throws Exception {
		if (!job.getCompleted())
			throw new Exception("Machine settlement job has not completed");
		if (job.getProcessing())
			throw new Exception("Machine settlement job is currently processing");
		SettlementClient client = services.target(SettlementClient.class, "service-settlement", true);
		Response<BatchSettlements> response = client.initBatchRerun(job.getDomain().getName(), job.getBatchName());
		if (response.isSuccessful()) {
			MachineSettlementProcessingBoundary boundary = jobProcessingBoundaryRepo.findOne(job.getBoundary().getId());
			if (boundary != null) {
				boundary.setLastDateProcessed(null);
				boundary.setLastMachineIdProcessed(null);
				boundary.setLastLocationDistConfigRevIdProcessed(null);
				boundary.setLastRelationshipDistConfigRevIdProcessed(null);
				boundary = jobProcessingBoundaryRepo.save(boundary);
				job.setBoundary(boundary);
			}
			job.setRerun(true);
			job.setStartedOn(null);
			job.setCompleted(false);
			job.setProcessing(false);
			job.setLastFailedDate(null);
			job.setLastFailedReason(null);
			job = jobRepo.save(job);
			return job;
		} else {
			throw new Exception("Initiating batch rerun failed");
		}
	}
}
