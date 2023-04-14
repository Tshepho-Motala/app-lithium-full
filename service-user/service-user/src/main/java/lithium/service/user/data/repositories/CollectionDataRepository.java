package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.CollectionData;
import org.springframework.data.repository.CrudRepository;

public interface CollectionDataRepository extends CrudRepository<CollectionData, Long> {
  CollectionData findByCollectionNameAndDataKeyAndAndDataValue(String collectionName, String dataKey,String dataValue);
}

