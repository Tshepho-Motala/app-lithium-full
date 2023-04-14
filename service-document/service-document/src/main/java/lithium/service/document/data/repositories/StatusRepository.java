package lithium.service.document.data.repositories;


import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.document.data.entities.AuthorService;
import lithium.service.document.data.entities.Status;

@Deprecated
public interface StatusRepository extends PagingAndSortingRepository<Status, Long> {
	Status findById(long id);
	Status findByNameAndAuthorService(String name, AuthorService authorService);
}
