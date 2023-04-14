package lithium.service.promo.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.promo.data.entities.Graphic;

public interface GraphicRepository extends PagingAndSortingRepository<Graphic, Long>, JpaSpecificationExecutor<Graphic> {
  default void delete(Long id) {
    deleteById(id);
  }
}
