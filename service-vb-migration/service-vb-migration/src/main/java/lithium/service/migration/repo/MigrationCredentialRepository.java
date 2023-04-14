package lithium.service.migration.repo;

import lithium.service.libraryvbmigration.data.entities.MigrationCredential;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Optional;

public interface MigrationCredentialRepository extends PagingAndSortingRepository<MigrationCredential, Long> {

  @CacheEvict({
      "lithium.service.migration.credentials.by-guid",
      "lithium.service.migration.credentials.by-username",
  })
  @Override
  <S extends MigrationCredential> S save(S entity);
  Optional<MigrationCredential> findByCustomerId(String customerId);

  @Cacheable(value = "lithium.service.migration.credentials.by-guid", unless = "#result == null")
  Optional<MigrationCredential> findByPlayerGuid(String playerGuid);

  @Cacheable(value = "lithium.service.migration.credentials.by-username", unless = "#result == null")
  Optional<MigrationCredential> findByUsername(String username);
}
