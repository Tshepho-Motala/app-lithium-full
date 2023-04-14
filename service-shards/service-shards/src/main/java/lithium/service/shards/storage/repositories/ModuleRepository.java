package lithium.service.shards.storage.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.shards.storage.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
	Module findByName(String name);
}
