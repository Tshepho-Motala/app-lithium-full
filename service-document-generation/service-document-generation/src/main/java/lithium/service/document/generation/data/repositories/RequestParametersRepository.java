package lithium.service.document.generation.data.repositories;

import lithium.service.document.generation.data.entities.RequestParameters;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RequestParametersRepository extends PagingAndSortingRepository<RequestParameters, Long> {
}
