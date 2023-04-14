package lithium.service.xp.data.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.xp.data.entities.Level;
import lithium.service.xp.data.entities.LevelBonus;

public interface LevelBonusRepository extends PagingAndSortingRepository<LevelBonus, Long>, JpaSpecificationExecutor<LevelBonus> {
	@Modifying
	@Transactional
	void deleteByLevel(Level level);
}
