package lithium.service.user.search.data.repositories.user_search;

import javax.persistence.LockModeType;
import lithium.service.user.search.data.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("user_search.UserRepository")
public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {
  User findUserByGuid(String guid);

  @Query("select o from #{#entityName} o where o.guid = :guid")
  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  User findForUpdate(@Param("guid") String guid);
}
