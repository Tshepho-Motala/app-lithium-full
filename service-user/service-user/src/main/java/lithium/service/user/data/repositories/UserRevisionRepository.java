package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserRevision;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRevisionRepository extends PagingAndSortingRepository<UserRevision, Long> {
  UserRevision findTopByUserOrderByIdDesc(User user);
}
