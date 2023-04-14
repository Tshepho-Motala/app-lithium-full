package lithium.service.settlement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.settlement.data.entities.Label;
import lithium.service.settlement.data.entities.LabelValue;
import lithium.service.settlement.data.repositories.LabelValueRepository;

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
	
	public LabelValue findOrCreate(String labelName, String labelValue) {
		Label l = labelService.findOrCreate(labelName);
		return findOrCreate(l, labelValue);
	}
}
