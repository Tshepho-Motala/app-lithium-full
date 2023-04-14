package lithium.service.affiliate.provider.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.affiliate.provider.ServiceAffiliateProviderPapModuleInfo;
import lithium.service.affiliate.provider.data.entities.BatchRun;
import lithium.service.affiliate.provider.data.entities.BatchRunStatus;
import lithium.service.affiliate.provider.data.entities.Domain;
import lithium.service.affiliate.provider.data.repositories.BatchRunRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BatchRunService {
	@Autowired private DomainService domainService;
	@Autowired LithiumServiceClientFactory services;
	@Autowired ServiceAffiliateProviderPapModuleInfo info;
	@Autowired private BatchRunRepository repository;
	@Autowired private BatchRunStatusService batchRunStatusService;
	
	public BatchRun find(String domain, int granularity, String currency, Date dateStart, Date dateEnd) {
		BatchRun batchRun = repository.findByDomainMachineNameAndGranularityAndCurrencyAndDateStartAndDateEnd(domain, granularity, currency, dateStart, dateEnd);
		return batchRun;
	}
	
	public BatchRun create(String domain, int granularity, String currency, Date dateStart, Date dateEnd) {
		BatchRun batchRun = find(domain, granularity, currency, dateStart, dateEnd);
		if (batchRun == null) {
			Domain d = domainService.findOrCreate(domain);
			BatchRunStatus brs = batchRunStatusService.findOrCreate(BatchRunStatusService.BATCH_RUN_STATUS_NEW);
			batchRun = BatchRun.builder()
					.domain(d)
					.granularity(granularity)
					.dateStart(dateStart)
					.dateEnd(dateEnd)
					.currency(currency)
					.status(brs)
					.build();
			batchRun = repository.save(batchRun);
		}
		return batchRun;
	}
	
	public BatchRun update(long batchRunId) {
		BatchRun batchRun = repository.findOne(batchRunId);
		batchRun.setLastActivityUpdate(new Date());
		batchRun.setStatus(batchRunStatusService.findOrCreate(BatchRunStatusService.BATCH_RUN_STATUS_RUNNING));
		batchRun = repository.save(batchRun);
		
		return batchRun;
	}
	
	public BatchRun finish(long batchRunId) {
		BatchRun batchRun = repository.findOne(batchRunId);
		batchRun.setLastActivityUpdate(new Date());
		batchRun.setStatus(batchRunStatusService.findOrCreate(BatchRunStatusService.BATCH_RUN_STATUS_COMPLETE));
		batchRun = repository.save(batchRun);
		log.info("Finished executing batch run" + batchRun);
		return batchRun;
	}
}