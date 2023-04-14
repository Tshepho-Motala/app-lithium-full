package lithium.service.promo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.promo.data.entities.Label;
import lithium.service.promo.data.repositories.LabelRepository;

@Service
public class LabelService {
	@Autowired
	private LabelRepository labelRepository;
	
	public Label findOrCreate(String name) {
		Label label = labelRepository.findByName(name);
		if (label == null) {
			label = labelRepository.save(
				Label.builder().name(name).build()
			);
		}
		return label;
	}
	
	public List<Label> findAll() {
		List<Label> labels = new ArrayList<>();
		labelRepository.findAll().forEach(labels::add);
		return labels;
	}
}