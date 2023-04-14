package lithium.service.promo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.promo.data.entities.Label;
import lithium.service.promo.data.entities.LabelValue;
import lithium.service.promo.data.repositories.LabelValueRepository;

@Service
public class LabelValueService {
	@Autowired LabelService labelService;
	@Autowired LabelValueRepository labelValueRepository;
	
	public LabelValue findOrCreate(Label l, String value) {
		LabelValue labelValue = labelValueRepository.findByLabelAndValue(l, value);
		if (labelValue == null) {
			labelValue = labelValueRepository.save(
				LabelValue.builder()
				.label(l)
				.value(value)
				.build()
			);
		}
		return labelValue;
	}
	
	public LabelValue findOrCreate(String labelName, String labelValue) {
		Label l = labelService.findOrCreate(labelName);
		return findOrCreate(l, labelValue);
	}
}
