package lithium.service.accounting.provider.internal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.accounting.provider.internal.data.entities.Label;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.repositories.LabelValueRepository;

@Service
public class LabelValueService {
	
	@Autowired LabelService labelService;
	
	@Autowired LabelValueRepository repository;

	public LabelValue processCreateRequest(LabelValue o) {
		repository.save(o);
		return o;
	}

	public LabelValue findOrCreate(Label l, String value) {
		
		LabelValue o = repository.findByLabelAndValue(l, value);
		
		if (o != null) return o;
		return processCreateRequest(LabelValue.builder().label(l).value(value).build());
	}

    @Retryable
	public LabelValue findOrCreate(String labelName, String labelValue) {
		Label l = labelService.findOrCreate(labelName);
		return findOrCreate(l, labelValue);
	}

	public Page<LabelValue> getLabelValuesByLabelName(String labelName, Pageable pageable) {
		return repository.findByLabelName(labelName, pageable);
	}
}
