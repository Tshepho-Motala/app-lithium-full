package lithium.service.accounting.domain.v2.services;

import lithium.service.accounting.domain.v2.storage.entities.Label;
import lithium.service.accounting.domain.v2.storage.entities.LabelValue;
import lithium.service.accounting.domain.v2.storage.repositories.LabelValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class LabelValueService {
	@Autowired private LabelService labelService;
	@Autowired private LabelValueRepository repository;

	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
	public LabelValue findOrCreate(String labelName, String labelValue) {
		Label l = labelService.findOrCreate(labelName);
		return findOrCreate(l, labelValue);
	}

	private LabelValue processCreateRequest(LabelValue o) {
		repository.save(o);
		return o;
	}

	private LabelValue findOrCreate(Label l, String value) {
		LabelValue o = repository.findByLabelAndValue(l, value);
		
		if (o != null) return o;
		return processCreateRequest(LabelValue.builder().label(l).value(value).build());
	}
}
