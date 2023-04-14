package lithium.service.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.domain.data.entities.Label;
import lithium.service.domain.data.entities.LabelValue;
import lithium.service.domain.data.repositories.LabelValueRepository;

@Service
public class LabelValueService {

	@Autowired
	LabelService labelService;

	@Autowired
	LabelValueRepository repository;

	public LabelValue processCreateRequest(LabelValue o) {
		repository.save(o);
		return o;
	}

	public LabelValue findOrCreate(String labelName, String value) {
		Label l = labelService.findOrCreate(labelName);
		LabelValue o = repository.findByLabelAndValue(l, value);
		if (o != null) return o;
		return processCreateRequest(LabelValue.builder().label(l).value(value).build());
	}
}
