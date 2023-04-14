package lithium.service.product.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.product.data.entities.Payout;
import lithium.service.product.data.entities.Product;

public interface PayoutRepository extends PagingAndSortingRepository<Payout, Long>, JpaSpecificationExecutor<Payout> {
	List<Payout> findByProduct(Product product);
	Payout findByBonusCodeAndCurrencyCodeAndProduct(String bonusCode, String currencyCode, Product product);
}
