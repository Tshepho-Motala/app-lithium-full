package lithium.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import lithium.service.entity.data.entities.Status;
import lithium.service.entity.data.repositories.StatusRepository;

@Configuration
public class Init {
	@Autowired StatusRepository statusRepository;
	
	public void init() {
		createStatus("Enabled", "Entity is enabled", true);
		createStatus("Disabled", "Entity is disabled", false);
	}
	
	private Status createStatus(String name, String description, boolean enabled) {
		Status status = statusRepository.findByNameIgnoreCase(name);
		if (status == null) {
			status = statusRepository.save(
				Status.builder()
				.name(name)
				.description(description)
				.enabled(enabled)
				.deleted(false)
				.build()
			);
		}
		return status;
	}
}
