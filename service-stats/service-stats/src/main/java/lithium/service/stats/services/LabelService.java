package lithium.service.stats.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.stats.data.entities.Label;
import lithium.service.stats.data.repositories.LabelRepository;

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
}