package lithium.service.games.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lithium.service.games.data.entities.Label;
import lithium.service.games.data.repositories.LabelRepository;

@Service
public class LabelService {
	
	@Autowired LabelRepository repository;

	public Label processCreateRequest(Label label) {
		repository.save(label);
		return label;
	}

	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public Label findOrCreate(String name) {
		Label label = repository.findByName(name);
		if (label != null) return label;
		return processCreateRequest(Label.builder().name(name).build());
	}
	
}
