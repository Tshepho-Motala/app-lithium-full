package lithium.service.event.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.event.entities.Currency;

public interface CurrencyRepository extends PagingAndSortingRepository<Currency, Long> {

	public Currency findByCode(String code);
	
}
