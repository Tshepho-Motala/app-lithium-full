package lithium.service.cashier.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Image;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {
}
