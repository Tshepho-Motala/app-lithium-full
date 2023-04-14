package lithium.service.xp.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.xp.data.entities.Level;
import lithium.service.xp.data.entities.Scheme;

public interface LevelRepository extends PagingAndSortingRepository<Level, Long>, JpaSpecificationExecutor<Level> {
	List<Level> findLevelsByScheme(Scheme scheme);
}
