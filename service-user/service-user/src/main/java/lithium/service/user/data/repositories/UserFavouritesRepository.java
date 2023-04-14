package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.UserFavourites;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserFavouritesRepository extends PagingAndSortingRepository<UserFavourites, Long> {

}
