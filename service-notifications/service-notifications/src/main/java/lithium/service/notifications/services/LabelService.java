package lithium.service.notifications.services;

import lithium.service.notifications.data.entities.Label;
import lithium.service.notifications.data.repositories.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelService {

  @Autowired
  LabelRepository labelRepository;

  public Label processCreateRequest(Label label) {
    labelRepository.save(label);
    return label;
  }

  public Label findOrCreate(String name) {
    Label label = labelRepository.findByName(name);
		if (label != null) {
			return label;
		}
    return processCreateRequest(Label.builder().name(name).build());
  }
}
