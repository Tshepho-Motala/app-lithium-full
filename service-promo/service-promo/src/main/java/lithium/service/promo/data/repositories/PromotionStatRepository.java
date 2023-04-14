package lithium.service.promo.data.repositories;

import lithium.service.promo.data.entities.PromotionStat;
import lithium.service.promo.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PromotionStatRepository extends PagingAndSortingRepository<PromotionStat, Long> {

  PromotionStat findByNameAndOwner(String name, User owner);
  //	PromotionStat findByOwnerAndTypeAndActionAndIdentifier(User user, Type type, Action action, String identifier);
}