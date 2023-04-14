package lithium.service.user.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.user.data.entities.StatusReason;

public interface StatusReasonRepository extends FindOrCreateByNameRepository<StatusReason, Long> {
  default StatusReason findOne(Long id) {
    return findById(id).orElse(null);
  }
}
