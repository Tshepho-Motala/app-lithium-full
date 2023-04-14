package lithium.service.user.search.data.repositories.user_search;

import lithium.service.user.search.data.entities.CurrentAccountBalance;
import lithium.service.user.search.data.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("user_search.CurrentAccountBalanceRepository")
public interface CurrentAccountBalanceRepository extends PagingAndSortingRepository<CurrentAccountBalance, Long>, JpaSpecificationExecutor<CurrentAccountBalance> {
  CurrentAccountBalance findByUser(User user);
}
