package lithium.service.user.search.data.repositories.cashier;

import lithium.service.cashier.data.entities.DomainMethod;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("cashier.DomainMethodRepository")
public interface DomainMethodRepository extends PagingAndSortingRepository<DomainMethod, Long>, JpaSpecificationExecutor<DomainMethod> {
  default DomainMethod findOne(Long id) {
    return findById(id).orElse(null);
  }
}
