package lithium.service.games.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.games.data.entities.Graphic;

public interface GraphicRepository extends PagingAndSortingRepository<Graphic, Long> {
	Iterable<Graphic> findBySizeAndMd5HashAndDeletedFalse(long imageSize, String md5Hash);
}
