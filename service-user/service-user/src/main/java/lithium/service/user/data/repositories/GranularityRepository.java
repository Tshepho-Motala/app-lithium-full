package lithium.service.user.data.repositories;

import lithium.jpa.entity.EntityFactory;
import lithium.service.user.data.entities.Granularity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GranularityRepository extends PagingAndSortingRepository<Granularity, Long>, JpaSpecificationExecutor<Granularity> {
  Granularity findByType(String type);

  default void findOrCreateByType(String type, EntityFactory<Granularity> factory) {
    Granularity t = findByType(type);
    if (t == null) {
      t = factory.build();
      t.setType(type);
      save(t);
    }
  }
}
