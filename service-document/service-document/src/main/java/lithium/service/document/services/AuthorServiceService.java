package lithium.service.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.document.data.entities.AuthorService;
import lithium.service.document.data.repositories.AuthorServiceRepository;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
@Service
public class AuthorServiceService {
	@Autowired
	private AuthorServiceRepository authorServiceRepo;

	public AuthorService findOrCreateAuthorService(String authorServiceName) {
		AuthorService authService = authorServiceRepo.findByName(authorServiceName);
			
		if (authService == null) {
			authService = authorServiceRepo.save(
			AuthorService.builder()
			.name(authorServiceName)
			.build());
		}
		
		return authService;
	}

}
