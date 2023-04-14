package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorUser;
import lithium.service.cashier.data.entities.User;

public interface DomainMethodProcessorUserRepository extends PagingAndSortingRepository<DomainMethodProcessorUser, Long> {
	List<DomainMethodProcessorUser> findByUserGuid(String userGuid);
	List<DomainMethodProcessorUser> findByDomainMethodProcessor(DomainMethodProcessor domainMethodProcessor);
	DomainMethodProcessorUser findByDomainMethodProcessorAndUser(DomainMethodProcessor domainMethodProcessor, User user);
	Page<DomainMethodProcessorUser> findByDomainMethodProcessor(DomainMethodProcessor domainMethodProcessor, Pageable pageable);
//	DomainMethodProcessorUser findByDomainMethodProcessorIdAndUserGuid(Long domainMethodProcessorId, String userGuid);

	default DomainMethodProcessorUser findOne(Long id) {
		return findById(id).orElse(null);
	}

}