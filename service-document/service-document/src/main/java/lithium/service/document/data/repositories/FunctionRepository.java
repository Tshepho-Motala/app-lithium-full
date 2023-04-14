package lithium.service.document.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.document.data.entities.AuthorService;
import lithium.service.document.data.entities.Function;

@Deprecated
public interface FunctionRepository extends PagingAndSortingRepository<Function, Long> {
	Function findById(long id);
	Function findByNameAndAuthorService(String name, AuthorService authorService);
}
