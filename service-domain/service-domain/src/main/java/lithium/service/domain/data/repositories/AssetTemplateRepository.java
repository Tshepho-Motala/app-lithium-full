package lithium.service.domain.data.repositories;

import lithium.service.domain.data.entities.AssetTemplate;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AssetTemplateRepository extends PagingAndSortingRepository<AssetTemplate, Long>, JpaSpecificationExecutor<AssetTemplate> {
  AssetTemplate findOneByDomainNameAndNameAndLangAndDeletedFalse(String domainName, String name, String lang);
  default AssetTemplate findOne(Long id) {
    return findById(id).orElse(null);
  }
}
