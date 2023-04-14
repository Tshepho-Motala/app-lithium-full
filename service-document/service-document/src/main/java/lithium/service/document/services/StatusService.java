package lithium.service.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.document.data.entities.AuthorService;
import lithium.service.document.data.entities.Status;
import lithium.service.document.data.repositories.StatusRepository;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
@Service
public class StatusService {
	@Autowired
	private StatusRepository statusRepo;

	public Status findOrCreateStatus(String statusString, AuthorService authorService) {
		
		if (statusString == null) return null;
		
		Status status = statusRepo.findByNameAndAuthorService(statusString, authorService);
		
		if (status == null) {
			status = statusRepo.save(
				Status.builder()
				.name(statusString)
				.authorService(authorService)
				.build()
			);
		}
		
		return status;
	}

}
