package lithium.service.cashier.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Limits;

public interface LimitsRepository extends PagingAndSortingRepository<Limits, Long> {

    default Limits findOne(Long id) {
        return findById(id).orElse(null);
    }

}