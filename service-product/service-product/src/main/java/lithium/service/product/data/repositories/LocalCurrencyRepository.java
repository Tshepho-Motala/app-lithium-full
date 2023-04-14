package lithium.service.product.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.product.data.entities.Domain;
import lithium.service.product.data.entities.LocalCurrency;
import lithium.service.product.data.entities.Product;

public interface LocalCurrencyRepository extends PagingAndSortingRepository<LocalCurrency, Long>, JpaSpecificationExecutor<LocalCurrency> {
	List<LocalCurrency> findByProduct(Product product);
	List<LocalCurrency> findByCountryCodeAndProductDomain(String countryCode, Domain domain);
	List<LocalCurrency> findByCurrencyCodeAndProductDomain(String currencyCode, Domain domain);
	LocalCurrency findByCountryCodeAndCurrencyCodeAndProduct(String countryCode, String currencyCode, Product product); 
}
