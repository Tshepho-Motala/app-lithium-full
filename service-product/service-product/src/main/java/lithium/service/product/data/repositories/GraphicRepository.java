package lithium.service.product.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.product.data.entities.Graphic;

public interface GraphicRepository extends PagingAndSortingRepository<Graphic, Long> {
	Iterable<Graphic> findBySizeAndMd5HashAndDeletedFalse(long imageSize, String md5Hash);
}
