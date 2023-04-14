package lithium.service.limit.data.repositories;


import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.limit.data.entities.RealityCheckSet;



public interface RealityCheckSetRepository extends FindOrCreateByGuidRepository<RealityCheckSet, Long> {
    RealityCheckSet findByGuid(String guid);
}
