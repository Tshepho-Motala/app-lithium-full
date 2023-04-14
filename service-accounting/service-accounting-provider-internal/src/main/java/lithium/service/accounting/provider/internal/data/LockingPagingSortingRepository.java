package lithium.service.accounting.provider.internal.data;

import java.io.Serializable;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


@NoRepositoryBean
public interface LockingPagingSortingRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

	@Query("select o from #{#entityName} o where o.id = :id")
	@Lock(LockModeType.OPTIMISTIC)
	T findForUpdate(@Param("id") Long id);
	
}
