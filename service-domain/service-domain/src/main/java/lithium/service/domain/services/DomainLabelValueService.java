package lithium.service.domain.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status400BadRequestException;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainRevision;
import lithium.service.domain.data.entities.DomainRevisionLabelValue;
import lithium.service.domain.data.entities.LabelValue;
import lithium.service.domain.data.objects.DomainRevisionBasic;
import lithium.service.domain.data.objects.DomainRevisionLabelValueBasic;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.DomainRevisionLabelValueRepository;
import lithium.service.domain.data.repositories.DomainRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DomainLabelValueService {
	@Autowired LabelValueService labelValueService;
	@Autowired DomainRevisionLabelValueRepository domainRevisionLabelValueRepository;
	@Autowired DomainRevisionRepository domainRevisionRepository;
	@Autowired DomainRepository domainRepository;

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10, exclude={NotRetryableErrorCodeException.class}, include=Exception.class)
	public DomainRevision save(DomainRevisionBasic domainRevisionBasic) throws Status400BadRequestException {
		Domain domain = domainRepository.findByName(domainRevisionBasic.getDomainName());
		DomainRevision rv = domainRevisionRepository.save(DomainRevision.builder().domain(domain).build());
		domain.setCurrent(rv);
		domain = domainRepository.save(domain);

		List<DomainRevisionLabelValue> lvList = new ArrayList<>();

		for (DomainRevisionLabelValueBasic lbv: domainRevisionBasic.getLabelValues()) {
			LabelValue dbLblVal = labelValueService.findOrCreate(lbv.getLabel(), lbv.getValue());
      if ((dbLblVal.getValue().equalsIgnoreCase("FALSE")) || (dbLblVal.getValue().equalsIgnoreCase("TRUE") )) {
        dbLblVal.setValue(dbLblVal.getValue().toLowerCase());
      }
			try {
				lvList.add(
					domainRevisionLabelValueRepository.save(
						DomainRevisionLabelValue.builder()
						.domainRevision(rv)
						.label(dbLblVal.getLabel())
						.labelValue(dbLblVal)
						.build()
					)
				);
			} catch (DataIntegrityViolationException e) {
				throw new Status400BadRequestException("Failed to save settings. Ensure setting names are unique.");
			}
		}
		rv.setLabelValueList(lvList);
		// Just making absolutely sure that the cache is evicted once all settings have been written. There could be
		// inconsistencies on the cached object if the cache has already been set, but all settings haven't persisted
		// yet.
		domain.setCurrent(rv);
		domain = domainRepository.save(domain);
		return rv;
	}
}

