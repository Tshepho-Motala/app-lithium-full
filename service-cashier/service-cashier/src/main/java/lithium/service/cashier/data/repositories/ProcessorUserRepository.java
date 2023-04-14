package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorUser;
import lithium.service.cashier.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProcessorUserRepository extends PagingAndSortingRepository<ProcessorUser, Long> {
	ProcessorUser findByUserAndDomainMethodProcessor(User user, DomainMethodProcessor domainMethodProcessor);
	ProcessorUser findByUserIdAndDomainMethodProcessorId(long userId, long domainMethodProcessorId);
}
