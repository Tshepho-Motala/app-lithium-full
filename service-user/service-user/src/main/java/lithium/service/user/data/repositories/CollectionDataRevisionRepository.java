package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.CollectionDataRevision;
import org.springframework.data.repository.CrudRepository;

public interface CollectionDataRevisionRepository extends CrudRepository<CollectionDataRevision, Long> {
  CollectionDataRevision findFirstByUserIdOrderByIdDesc(Long userId);
}
