package lithium.service.migration.repo;

import java.util.Optional;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.models.enities.Progress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRepo extends CrudRepository<Progress, Long> {

  Progress findProgressById(long id);
  Optional<Progress> findFirstByIdGreaterThanAndMigrationType(long id, MigrationType migrationType);
  Progress findTopByMigrationTypeOrderByCreatedDateDesc(MigrationType migrationType);

}
