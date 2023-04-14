package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.OpenBetsOpMigrationAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenBetsOpMigrationAuditRepository extends JpaRepository<OpenBetsOpMigrationAudit, Long> {
}
