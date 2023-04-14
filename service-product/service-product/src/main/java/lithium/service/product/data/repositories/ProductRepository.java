package lithium.service.product.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.product.data.entities.Domain;
import lithium.service.product.data.entities.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	Product findByGuidAndDomain(String guid, Domain domain);
	List<Product> findByDomain(Domain domain);
	List<Product> findByDomainAndLocalCurrenciesCurrencyCode(Domain domain, String currencyCode);
	List<Product> findByDomainAndCurrencyCode(Domain domain, String currencyCode);
//	Catalog findByDomainAndTypeAndOrderingAndGranularityAndXpLevelMinAndXpLevelMax(Domain domain, Type type, Ordering ordering, Granularity granularity, Integer xpLevelMin, Integer xpLevelMax);
//	Catalog findByDomainAndTypeAndGranularityAndXpLevelMinLessThanEqualAndXpLevelMaxGreaterThanEqual(Domain domain, Type type, Granularity granularity, Integer xpLevel, Integer xpLevel2);

	default Product findOne(Long id) {
		return findById(id).orElse(null);
	}
}
