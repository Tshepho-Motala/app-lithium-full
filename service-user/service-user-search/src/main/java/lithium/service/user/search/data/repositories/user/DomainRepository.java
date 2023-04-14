package lithium.service.user.search.data.repositories.user;

import lithium.service.user.data.entities.Domain;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("user.DomainRepository")
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {

  Domain findByName(String name);
}
