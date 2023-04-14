package lithium.service.notifications.data.repositories;

import lithium.service.notifications.data.entities.InboxUser;
import lithium.service.notifications.data.entities.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface InboxUserRepository extends PagingAndSortingRepository<InboxUser, Long> {


    @Query("SELECT u FROM InboxUser u WHERE u.user.guid = :#{#user.guid}")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    InboxUser findByUserAlwaysLock(@Param("user") User user);
    InboxUser findByUser(@Param("user") User user);


    @CacheEvict(value = "lithium.service.notifications.services.inbox-user-service.find-by-user", key = "#entity.user.guid")
    @Override
    <S extends  InboxUser> S save(S entity);
}
