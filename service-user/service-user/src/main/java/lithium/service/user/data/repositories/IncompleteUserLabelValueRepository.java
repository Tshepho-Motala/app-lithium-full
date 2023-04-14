package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.entities.IncompleteUserLabelValue;
import lithium.service.user.data.entities.LabelValue;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Optional;

public interface IncompleteUserLabelValueRepository extends PagingAndSortingRepository<IncompleteUserLabelValue, Long> ,JpaSpecificationExecutor<IncompleteUserLabelValue>  {
  Optional<IncompleteUserLabelValue> findByIncompleteUserAndLabelValueLabelName(IncompleteUser incompleteUser, String name);
  IncompleteUserLabelValue findIncompleteUserLabelValueByLabelValue(LabelValue labelValue);
}
