package lithium.service.access.data.repositories;

import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.entities.User;
import lithium.service.access.data.entities.UserExternalListValidation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserExternalListValidationRepository extends PagingAndSortingRepository<UserExternalListValidation, Long>, JpaSpecificationExecutor<UserExternalListValidation> {
	UserExternalListValidation findByUserAndExternalList(User user, ExternalList externalList);
}
