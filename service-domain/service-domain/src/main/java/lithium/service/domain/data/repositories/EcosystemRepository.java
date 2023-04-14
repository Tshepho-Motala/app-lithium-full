package lithium.service.domain.data.repositories;

import java.util.ArrayList;
import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.domain.data.entities.Ecosystem;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "ecosystems", path = "ecosystems")
public interface EcosystemRepository extends FindOrCreateByNameRepository<Ecosystem, Long> {
  ArrayList<Ecosystem> findByEnabledTrueAndDeletedFalse();
  ArrayList<Ecosystem> findByDeletedFalse();

  @Override
  @CacheEvict(value = { "lithium.service.domain.ecosystem.domain-relationships.by-ecosystem-name",
      "lithium.service.domain.ecosystem.domain-relationships.by-domain-name",
      "lithium.service.domain.ecosystem.domain-relationships.is-ecosystem-name",
      "lithium.service.domain.ecosystem.domain-in-any-ecosystem",
      "lithium.service.domain.ecosystem.ecosystem-name.by-domain-name",
      "lithium.service.domain.ecosystem.domain-ecosystem-relationship-type"}, allEntries = true)
  <S extends Ecosystem> S save(S s);

  default Ecosystem findOne(Long id) {
    return findById(id).orElse(null);
  }
}
