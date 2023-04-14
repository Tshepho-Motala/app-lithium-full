package lithium.service.affiliate.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.affiliate.provider.data.entities.BatchRunStatus;
import lithium.service.affiliate.provider.data.repositories.BatchRunStatusRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BatchRunStatusService {
	public static final String BATCH_RUN_STATUS_NEW = "new";
	public static final String BATCH_RUN_STATUS_RUNNING = "running";
	public static final String BATCH_RUN_STATUS_COMPLETE = "complete";
	
	@Autowired LithiumServiceClientFactory services;
	@Autowired private BatchRunStatusRepository repository;
	
	public BatchRunStatus findOrCreate(String statusName) {
		BatchRunStatus status = repository.findByName(statusName.toLowerCase());
		if (status == null) {
			status = BatchRunStatus.builder().name(statusName).build();
			status = repository.save(status);
		}
		return status;
	}
}