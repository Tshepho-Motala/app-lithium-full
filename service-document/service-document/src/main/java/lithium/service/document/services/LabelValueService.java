package lithium.service.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lithium.service.document.data.entities.Label;
import lithium.service.document.data.entities.LabelValue;
import lithium.service.document.data.repositories.LabelValueRepository;

@Deprecated
@Service
public class LabelValueService {
	
	@Autowired LabelService labelService;
	
	@Autowired LabelValueRepository repository;

	public LabelValue processCreateRequest(LabelValue o) {
		repository.save(o);
		return o;
	}

	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	@Transactional
	public LabelValue findOrCreate(Label l, String value) {
		
		LabelValue o = repository.findByLabelAndValue(l, value);
		
		if (o != null) return o;
		return processCreateRequest(LabelValue.builder().label(l).value(value).build());
	}
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	@Transactional
	public LabelValue findOrCreate(String labelName, String labelValue) {
		Label l = labelService.findOrCreate(labelName);
		return findOrCreate(l, labelValue);
	}
}
