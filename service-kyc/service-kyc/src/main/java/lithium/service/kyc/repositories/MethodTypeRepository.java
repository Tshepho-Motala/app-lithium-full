package lithium.service.kyc.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.kyc.entities.MethodType;

public interface MethodTypeRepository extends FindOrCreateByNameRepository<MethodType, Long> {
}
