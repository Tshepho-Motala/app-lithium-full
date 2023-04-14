package lithium.service.document.data.repositories;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.document.data.entities.Owner;

public interface OwnerRepository extends PagingAndSortingRepository<Owner, Long> {
	Owner findById(long id);
	Owner findByGuid(String guid);
}
