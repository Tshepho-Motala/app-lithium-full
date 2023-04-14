package service.casino.provider.cataboom.repositories;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import service.casino.provider.cataboom.entities.PrizeFullfilment;

@Component
public interface PrizeFullfilmentRepository extends PagingAndSortingRepository<PrizeFullfilment, Long> {
	
}