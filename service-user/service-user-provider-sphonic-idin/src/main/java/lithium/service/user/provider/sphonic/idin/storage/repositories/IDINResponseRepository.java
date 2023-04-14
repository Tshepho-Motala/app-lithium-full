package lithium.service.user.provider.sphonic.idin.storage.repositories;

import java.util.List;
import lithium.service.user.provider.sphonic.idin.storage.entities.IDINResponse;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IDINResponseRepository extends PagingAndSortingRepository<IDINResponse, Long> {
    IDINResponse findIDINResponseByIdinRequestIdAndStage(Long idinRequestId, Integer stage);
    IDINResponse findFirstByIdinRequestIdOrderByIdDesc(Long idinRequestId);
}
