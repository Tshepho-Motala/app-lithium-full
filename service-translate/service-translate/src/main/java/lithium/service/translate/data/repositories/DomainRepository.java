package lithium.service.translate.data.repositories;

import lithium.service.translate.data.entities.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DomainRepository extends JpaRepository<Domain, Long> {
    Optional<Domain> findByName(String domainName);
}
