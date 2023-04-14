package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.CollectionDataRevisionEntry;
import org.springframework.data.repository.CrudRepository;

public interface CollectionDataRevisionEntryRepository extends CrudRepository<CollectionDataRevisionEntry, Long> {
  CollectionDataRevisionEntry findByCollectionRevisionIdAndCollectionDataId(Long collectionRevId, Long collectionDataId);
}
