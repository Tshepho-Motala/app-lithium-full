package lithium.service.games.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.games.data.entities.GameCurrency;

public interface GameCurrencyRepository extends PagingAndSortingRepository<GameCurrency, Long>, JpaSpecificationExecutor<GameCurrency> {
}
