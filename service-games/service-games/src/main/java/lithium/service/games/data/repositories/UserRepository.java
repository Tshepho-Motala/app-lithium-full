package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {
	User findByGuid(String guid);

	@Query("select o from #{#entityName} o where o.id = :id")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	User findForUpdate(@Param("id") Long id);
}