package lithium.service.domain.data.repositories;

import java.util.List;
import lithium.service.domain.data.entities.Ecosystem;
import lithium.service.domain.data.entities.EcosystemDomainRelationship;
import lithium.service.domain.data.entities.EcosystemRelationshipType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EcosystemDomainRelationshipRepository extends PagingAndSortingRepository<EcosystemDomainRelationship, Long> {
  List<EcosystemDomainRelationship> findByEcosystemId(Long ecosystemId);
  List<EcosystemDomainRelationship> findByEcosystemAndDeletedFalseAndEnabledTrue(Ecosystem ecosystem);
  EcosystemDomainRelationship findByDomainNameAndDeletedFalseAndEnabledTrueAndEcosystemEnabledTrue(String domainName);
  EcosystemDomainRelationship findByDomainNameAndEcosystemAndRelationship(String domainName, Ecosystem ecosystem, EcosystemRelationshipType relationshipType);

  @Override
  @CacheEvict(value = { "lithium.service.domain.ecosystem.domain-relationships.by-ecosystem-name",
                        "lithium.service.domain.ecosystem.domain-relationships.by-domain-name",
                        "lithium.service.domain.ecosystem.domain-in-any-ecosystem",
                        "lithium.service.domain.ecosystem.ecosystem-name.by-domain-name",
                        "lithium.service.domain.ecosystem.domain-ecosystem-relationship-type"}, allEntries = true)
  <S extends EcosystemDomainRelationship> S save(S s);

  default EcosystemDomainRelationship findOne(Long id) {
    return findById(id).orElse(null);
  }
}
