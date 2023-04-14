package lithium.service.raf.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.raf.data.entities.Click;

public interface ClickRepository extends PagingAndSortingRepository<Click, Long>, JpaSpecificationExecutor<Click> {
}
