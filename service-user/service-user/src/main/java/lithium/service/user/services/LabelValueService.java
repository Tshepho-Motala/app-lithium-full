package lithium.service.user.services;

import lithium.util.Hash;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.user.data.entities.Label;
import lithium.service.user.data.entities.LabelValue;
import lithium.service.user.data.repositories.LabelValueRepository;

@Service
public class LabelValueService {
	
	@Autowired LabelService labelService;
	
	@Autowired LabelValueRepository repository;

	public LabelValue processCreateRequest(LabelValue o) {
		repository.save(o);
		return o;
	}

	@SneakyThrows
  public LabelValue findOrCreate(Label l, String value) {
		
		LabelValue o = repository.findByLabelAndValue(l, value);
		
		if (o != null) return o;
    if (value == null || value.trim().isEmpty()) value = " ";
		return processCreateRequest(LabelValue.builder().label(l).value(value).sha1(Hash.builder(value).sha1()).build());
	}
	
	public LabelValue findOrCreate(String labelName, String labelValue) {
		Label l = labelService.findOrCreate(labelName);
		return findOrCreate(l, labelValue);
	}
}
