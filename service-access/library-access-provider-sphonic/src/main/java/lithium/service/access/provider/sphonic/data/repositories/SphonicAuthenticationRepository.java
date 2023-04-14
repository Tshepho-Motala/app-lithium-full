package lithium.service.access.provider.sphonic.data.repositories;

import lithium.service.access.provider.sphonic.data.entities.Authentication;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

@NoRepositoryBean
public interface SphonicAuthenticationRepository<T extends Authentication, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
	public T findByDomainName(String domainName);
	public void deleteByDomainName(String domainName);
}
