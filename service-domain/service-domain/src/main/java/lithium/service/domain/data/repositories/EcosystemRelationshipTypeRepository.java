package lithium.service.domain.data.repositories;

import java.util.ArrayList;
import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.domain.data.entities.EcosystemRelationshipType;

public interface EcosystemRelationshipTypeRepository extends FindOrCreateByCodeRepository<EcosystemRelationshipType, Long> {
  ArrayList<EcosystemRelationshipType> findByEnabledTrueAndDeletedFalse();

  default EcosystemRelationshipType findOne(Long id) {
    return findById(id).orElse(null);
  }
}
