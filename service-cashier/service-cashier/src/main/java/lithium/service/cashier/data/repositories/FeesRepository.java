package lithium.service.cashier.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Fees;

public interface FeesRepository extends PagingAndSortingRepository<Fees, Long> {

    default Fees findOne(Long id) {
        return findById(id).orElse(null);
    }
}