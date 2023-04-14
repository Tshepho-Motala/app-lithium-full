package lithium.service.document.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.document.data.entities.AuthorService;

@Deprecated
public interface AuthorServiceRepository extends PagingAndSortingRepository<AuthorService, Long> {
	AuthorService findById(long id);
	AuthorService findByName(String name);
}
