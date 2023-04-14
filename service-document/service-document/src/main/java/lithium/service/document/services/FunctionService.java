package lithium.service.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.document.data.entities.AuthorService;
import lithium.service.document.data.entities.Function;
import lithium.service.document.data.repositories.FunctionRepository;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
@Service
public class FunctionService {
	@Autowired
	private FunctionRepository functionRepo;

	public Function findOrCreateFunction(String documentFunction, AuthorService authorService) {
		
		if (documentFunction == null) return null;
		
		Function func = functionRepo.findByNameAndAuthorService(documentFunction, authorService);
				
		if (func == null) {
			func = functionRepo.save(
				Function.builder()
				.authorService(authorService)
				.name(documentFunction)
				.build()
			);
		}
		
		return func;
	}

}
