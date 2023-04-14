package lithium.service.user.data.repositories;

import java.util.List;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lithium.jpa.repository.FindOrCreateByNameRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.Status;

public interface StatusRepository extends FindOrCreateByNameRepository<Status, Long> {
  default Status findOne(Long id) {
    return findById(id).orElse(null);
  }

  Iterable<Status> findAllByNameIsNot(@Size(min = 2, max = 35) @Pattern(regexp = "\\w") String name);
}
