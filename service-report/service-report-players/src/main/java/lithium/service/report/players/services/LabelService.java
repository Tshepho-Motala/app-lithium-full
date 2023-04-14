package lithium.service.report.players.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.report.players.data.entities.Label;
import lithium.service.report.players.data.repositories.LabelRepository;

@Service
public class LabelService {
	@Autowired LabelRepository repository;
	
	public Label processCreateRequest(Label label) {
		return repository.save(label);
	}
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public Label findOrCreate(String name) {
		Label label = repository.findByName(name);
		if (label != null) return label;
		return processCreateRequest(Label.builder().name(name).build());
	}
}