package lithium.service.xp.data.repositories;

import lithium.service.xp.data.entities.Level;
import lithium.service.xp.data.entities.LevelNotification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface LevelNotificationRepository extends PagingAndSortingRepository<LevelNotification, Long>, JpaSpecificationExecutor<LevelNotification> {
	@Modifying
	@Transactional
	void deleteByLevel(Level level);
}
