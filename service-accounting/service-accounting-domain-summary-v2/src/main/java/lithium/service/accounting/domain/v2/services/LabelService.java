package lithium.service.accounting.domain.v2.services;

import lithium.service.accounting.domain.v2.storage.entities.Label;
import lithium.service.accounting.domain.v2.storage.repositories.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class LabelService {
	@Autowired private LabelRepository repository;

	@Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
	public Label findOrCreate(String name) {
		Label label = repository.findByName(name);
		if (label != null) return label;
		return processCreateRequest(Label.builder().name(name).build());
	}

	private Label processCreateRequest(Label label) {
		repository.save(label);
		return label;
	}
}
