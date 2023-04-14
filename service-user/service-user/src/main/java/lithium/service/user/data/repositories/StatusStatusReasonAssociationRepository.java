package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.data.entities.StatusStatusReasonAssociation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StatusStatusReasonAssociationRepository extends PagingAndSortingRepository<StatusStatusReasonAssociation, Long> {
	StatusStatusReasonAssociation findByStatusAndReason(Status status, StatusReason reason);
	List<StatusStatusReasonAssociation> findByStatus(Status status);
}
