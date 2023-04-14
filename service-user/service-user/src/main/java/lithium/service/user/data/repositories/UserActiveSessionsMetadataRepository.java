package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserActiveSessionsMetadata;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserActiveSessionsMetadataRepository extends PagingAndSortingRepository<UserActiveSessionsMetadata, Long> {
  UserActiveSessionsMetadata findByUser(User user);

  // TODO: Time
  UserActiveSessionsMetadata findByUserId(Long userId);
  UserActiveSessionsMetadata findByUserGuid(String userGuid);
}
