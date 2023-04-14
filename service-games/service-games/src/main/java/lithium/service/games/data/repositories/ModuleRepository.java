package lithium.service.games.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.games.data.entities.progressivejackpotfeeds.Module;

public interface ModuleRepository extends FindOrCreateByNameRepository<Module, Long> {
}
