package lithium.service.xp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.xp.data.entities.Status;
import lithium.service.xp.data.repositories.StatusRepository;

@Service
public class StatusService {
	@Autowired StatusRepository repository;
	
	public Status findOrCreate(String name, String description) {
		Status status = repository.findByName(name);
		if (status == null) {
			status = repository.save(
				Status.builder()
				.name(name)
				.description(description)
				.build()
			);
		}
		return status;
	}
	
	public Status findByName(String name) {
		return repository.findByName(name);
	}
	
	public Iterable<Status> findAll() {
		return repository.findAll();
	}
}
