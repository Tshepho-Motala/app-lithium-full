package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
    @Query("select o from #{#entityName} o where o.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    User findForUpdate(@Param("id") Long id);
}
