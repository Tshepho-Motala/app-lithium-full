package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.Label;
import lithium.service.user.data.entities.UserRevision;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRevisionLabelValueRepository extends PagingAndSortingRepository<UserRevisionLabelValue, Long> {
  UserRevisionLabelValue findByUserRevisionAndLabelValueLabel(UserRevision userRevision, Label label);
}
