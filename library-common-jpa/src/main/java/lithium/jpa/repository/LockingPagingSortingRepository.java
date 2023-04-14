package lithium.jpa.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.io.Serializable;


@NoRepositoryBean
public interface LockingPagingSortingRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

	@Query("select o from #{#entityName} o where o.id = :id")
	@Lock(LockModeType.OPTIMISTIC)
	T findForUpdate(@Param("id") Long id);
	
}
