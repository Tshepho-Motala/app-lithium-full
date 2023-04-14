package lithium.service.user.search.data.repositories.cashier;

import lithium.service.cashier.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("cashier.UserRepository")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

  User findByGuid(String userGuid);
}
