package lithium.service.document.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.document.data.entities.File;

public interface FileRepository extends PagingAndSortingRepository<File, Long> {
	Iterable<File> findBySizeAndMd5Hash(long imageSize, String md5Hash);
}
