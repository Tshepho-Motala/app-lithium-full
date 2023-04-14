package lithium.service.casino.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.Graphic;

public interface GraphicRepository extends PagingAndSortingRepository<Graphic, Long> {
	Iterable<Graphic> findBySizeAndMd5HashAndDeletedFalse(long imageSize, String md5Hash);
}
