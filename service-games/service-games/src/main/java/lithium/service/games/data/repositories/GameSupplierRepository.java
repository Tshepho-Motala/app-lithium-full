package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.GameSupplier;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GameSupplierRepository extends PagingAndSortingRepository<GameSupplier, Long>,
		JpaSpecificationExecutor<GameSupplier> {
	GameSupplier findByDomainNameAndName(String domainName, String name);
	List<GameSupplier> findByDomainNameAndDeletedFalse(String domainName);

	default GameSupplier findOne(Long id) {
		return findById(id).orElse(null);
	}
	GameSupplier findFirstByName(String gameSupplier);
}
