package lithium.service.kyc.repositories;

import lithium.service.kyc.entities.VendorData;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface VendorDataRepository extends PagingAndSortingRepository<VendorData, Long> {
}
