package lithium.service.cashier.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodUser;
import lithium.service.cashier.data.entities.User;

public interface DomainMethodUserRepository extends PagingAndSortingRepository<DomainMethodUser, Long>, JpaSpecificationExecutor<DomainMethodUser> {
	DomainMethodUser findByDomainMethodAndUser(DomainMethod domainMethod, User user);

	default DomainMethodUser findOne(Long id) {
		return findById(id).orElse(null);
	}

}