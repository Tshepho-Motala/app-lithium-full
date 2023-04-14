package lithium.service.machine.services;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.entities.Status;
import lithium.service.machine.data.repositories.StatusRepository;

@Service
public class StatusService {

	@Autowired StatusRepository statusRepository;
	@Autowired DomainService domainService;
	
	public Status NEW;
	public Status ACTIVE;
	public Status DISABLED;
	public Status ARCHIVED;

	public Status findOrCreate(String name, String description, boolean enabled) {
		Domain nullDomain = null;
		return this.findOrCreate(nullDomain, name, description, enabled);
	}

	public Status findOrCreate(String domain, String name, String description, boolean enabled) {
		return this.findOrCreate(domainService.findOrCreate(domain), name, description, enabled);
	}

	public Status findOrCreate(Domain domain, String name, String description, boolean enabled) {
		Status status = statusRepository.findByDomainAndName(domain, name);
		if (status == null) {
			status = statusRepository.save(
				Status.builder()
				.name(name)
				.domain(domain)
				.description(description)
				.enabled(enabled)
				.deleted(false)
				.build()
			);
		}
		return status;
	}
	
	@PostConstruct
	private void systemStatuses() {
		this.NEW = findOrCreate("NEW", "A new machine not yet enabled", false);
		this.ACTIVE = findOrCreate("ACTIVE", "An activated machine", true);
		this.DISABLED = findOrCreate("DISABLED", "A disabled machine", false);
		this.ARCHIVED = findOrCreate("ARCHIVED", "An archived machine", false);
	}
}
