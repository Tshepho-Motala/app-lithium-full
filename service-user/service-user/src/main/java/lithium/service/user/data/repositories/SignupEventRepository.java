package lithium.service.user.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.SignupEvent;

public interface SignupEventRepository extends PagingAndSortingRepository<SignupEvent, Long>, JpaSpecificationExecutor<SignupEvent> {
}