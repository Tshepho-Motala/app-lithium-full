package lithium.service.cdn.cms.data.repositories;

import lithium.service.cdn.cms.data.entities.CmsAsset;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CmsAssetRepository extends PagingAndSortingRepository<CmsAsset, Long>, JpaSpecificationExecutor<CmsAsset> {
  CmsAsset findFirstByNameAndDomainNameAndType(String name, String domainName, String type);
  List<CmsAsset> findByDomainName(String domainName);
  List<CmsAsset> findByDomainNameAndTypeAndDeletedFalse(String domainName, String type);

}
