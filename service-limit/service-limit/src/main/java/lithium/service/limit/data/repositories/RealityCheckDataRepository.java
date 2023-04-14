package lithium.service.limit.data.repositories;



import lithium.service.limit.data.entities.RealityCheckTrackData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;



public interface RealityCheckDataRepository extends PagingAndSortingRepository<RealityCheckTrackData,Long>  {
    Page<RealityCheckTrackData> findAllByGuid(String guid,Pageable pageable);
}
