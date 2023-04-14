package lithium.service.geo.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.geo.data.entities.FileUpdate;

public interface FileUpdateRepository extends PagingAndSortingRepository<FileUpdate, Long> {

	FileUpdate findByName(String name);

}
