package lithium.service.document.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.document.data.entities.ReviewStatus;

public interface ReviewStatusRepository extends FindOrCreateByNameRepository<ReviewStatus, Long> {

}