package lithium.service.notifications.data.repositories;

import lithium.service.notifications.data.entities.Label;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends PagingAndSortingRepository<Label, Long> {

  @CacheEvict( cacheNames = {"lithium.service.notifications.data.entities.Label.byId", "lithium.service.notifications.data.entities.Label.byName"}, allEntries = true)
  @Override
  <S extends Label> S save(S entity);

  @Override
  @Cacheable( value = "lithium.service.notifications.data.entities.Label.byId", unless = "#result == null")
  Optional<Label> findById(Long id);

  @Cacheable( value = "lithium.service.notifications.data.entities.Label.byName", unless = "#result == null" )
  Label findByName(String name);

}