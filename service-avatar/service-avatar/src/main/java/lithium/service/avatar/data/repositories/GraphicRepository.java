package lithium.service.avatar.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.avatar.data.entities.Graphic;

public interface GraphicRepository extends PagingAndSortingRepository<Graphic, Long>, JpaSpecificationExecutor<Graphic> {
}
