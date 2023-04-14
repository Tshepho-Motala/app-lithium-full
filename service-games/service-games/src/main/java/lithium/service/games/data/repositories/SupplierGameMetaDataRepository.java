package lithium.service.games.data.repositories;


import lithium.service.games.client.objects.supplier.GameVerticalEnum;
import lithium.service.games.data.entities.supplier.SupplierGameMetaData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SupplierGameMetaDataRepository extends PagingAndSortingRepository<SupplierGameMetaData, Long>, JpaSpecificationExecutor<SupplierGameMetaData> {
    SupplierGameMetaData findFirstBySupplierGameGuid(String supplierGameGuid);

    List<SupplierGameMetaData> findAllByGameVerticalNameAndGameDomainNameAndGameProviderGuidAndGameSupplierGameGuidNotNullAndGameEnabledTrue(GameVerticalEnum supplier, String domain, String provider, Pageable pageable);

    SupplierGameMetaData findFirstByGameId(Long aLong);
}
