package lithium.service.user.threshold.data.repositories;

import javax.persistence.LockModeType;
import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.user.threshold.data.entities.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {

  User findByGuid(String userGuid);

  @Query( "select o from #{#entityName} o where o.guid = :guid" )
  @Lock( LockModeType.PESSIMISTIC_FORCE_INCREMENT )
  User findForUpdate(@Param( "guid" ) String guid);
}
