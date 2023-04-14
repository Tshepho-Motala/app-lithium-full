package lithium.service.access.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.Value;

public interface ValueRepository extends PagingAndSortingRepository<Value, Long>, JpaSpecificationExecutor<Value> {
	Value findByListAndDataIgnoreCase(List list, String data);
  default Value findOne(Long id) {
    return findById(id).orElse(null);
  }
	java.util.List<Value> findAllByList(List list);
}
