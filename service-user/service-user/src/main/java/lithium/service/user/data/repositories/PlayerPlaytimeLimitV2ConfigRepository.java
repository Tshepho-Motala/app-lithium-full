package lithium.service.user.data.repositories;

import java.util.Optional;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Config;
import lithium.service.user.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerPlaytimeLimitV2ConfigRepository extends PagingAndSortingRepository<PlayerPlaytimeLimitV2Config, Long>,
    JpaSpecificationExecutor<PlayerPlaytimeLimitV2Config> {

  Optional<PlayerPlaytimeLimitV2Config> findByUser(User user);
  Optional<PlayerPlaytimeLimitV2Config> findByUser_Guid(String guid);

  PlayerPlaytimeLimitV2Config findById(long id);

  Page<PlayerPlaytimeLimitV2Config> findAllByPendingConfigRevisionIsNotNull(Pageable pageRequest);
}
