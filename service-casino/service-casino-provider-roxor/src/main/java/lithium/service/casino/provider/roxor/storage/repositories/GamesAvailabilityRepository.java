package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.service.casino.provider.roxor.storage.entities.GamesAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GamesAvailabilityRepository extends JpaRepository<GamesAvailability, Long> {
    List<GamesAvailability> findByCreationDateAndUser_Guid(Date creationDate, String guid);
}
