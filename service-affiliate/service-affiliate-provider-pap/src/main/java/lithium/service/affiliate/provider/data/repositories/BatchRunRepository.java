package lithium.service.affiliate.provider.data.repositories;

import java.util.Date;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.BatchRun;

public interface BatchRunRepository extends PagingAndSortingRepository<BatchRun, Long> {

	BatchRun findByDomainMachineNameAndGranularityAndCurrencyAndDateStartAndDateEnd(String domain, int granularity,
			String currency, Date dateStart, Date dateEnd);

	default BatchRun findOne(Long id) {
		return findById(id).orElse(null);
	}

}