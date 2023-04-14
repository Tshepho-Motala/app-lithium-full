package lithium.service.mail.data.repositories;

import lithium.service.mail.data.entities.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderTypeRepository extends JpaRepository<ProviderType, Long> {
    ProviderType findOneByName(String name);
}
