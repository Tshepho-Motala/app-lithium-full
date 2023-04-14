package lithium.service.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.user.data.entities.Label;
import lithium.service.user.data.repositories.LabelRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class LabelService {
	
	@Autowired LabelRepository repository;

	public Label processCreateRequest(Label label) {
		repository.save(label);
		return label;
	}

	public Label findOrCreate(String name) {
		Label label = repository.findByName(name);
		if (label != null) return label;
		return processCreateRequest(Label.builder().name(name).build());
	}

  public List<Label> getLabelsByNames(List<String> names){
    return repository.findAllByNameIn(names);
  }
	
}
