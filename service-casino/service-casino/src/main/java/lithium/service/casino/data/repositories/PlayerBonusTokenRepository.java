package lithium.service.casino.data.repositories;


import lithium.service.casino.client.objects.PlayerBonusTokenStatus;
import lithium.service.casino.data.entities.PlayerBonusToken;
import lithium.service.casino.data.entities.User;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface PlayerBonusTokenRepository extends PagingAndSortingRepository<PlayerBonusToken, Long> {
	List<PlayerBonusToken> findByUserAndStatus(User user, final int status);
	Page<PlayerBonusToken> findByUserAndStatus(User user, final int status, Pageable page);
	Page<PlayerBonusToken> findByExpiryDateBeforeAndStatus(Date now, Integer active, Pageable page);

	default PlayerBonusToken findOne(Long id) {
		return findById(id).orElse(null);
	}
}
