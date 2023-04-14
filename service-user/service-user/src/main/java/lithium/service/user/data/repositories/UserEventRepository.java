package lithium.service.user.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserEvent;
import lithium.service.user.data.entities.UserEventProjection;

public interface UserEventRepository extends PagingAndSortingRepository<UserEvent, Long> {
	List<UserEventProjection> findByUserAndReceivedFalse(User user);
	List<UserEventProjection> findByUserAndReceivedFalseAndTypeIgnoreCase(User user, String type);
  default UserEvent findOne(Long id) {
    return findById(id).orElse(null);
  }
}
