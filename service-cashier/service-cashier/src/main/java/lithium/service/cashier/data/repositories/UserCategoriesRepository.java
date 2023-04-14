package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.entities.UserCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoriesRepository extends PagingAndSortingRepository<UserCategory, Long>  {
    List<UserCategory> findAllByUserGuid(String guid);
    Optional<UserCategory> findByUserAndUserCategoryId(User user, Long categoryId);
}
