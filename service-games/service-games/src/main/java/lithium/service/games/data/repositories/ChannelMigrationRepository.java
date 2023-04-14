package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.ChannelMigration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelMigrationRepository extends JpaRepository<ChannelMigration, Long> {
}