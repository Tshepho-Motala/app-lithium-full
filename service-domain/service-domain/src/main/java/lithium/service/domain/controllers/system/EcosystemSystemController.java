package lithium.service.domain.controllers.system;

import java.util.ArrayList;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status469InvalidInputException;
import lithium.service.domain.client.EcosystemClient;
import lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship;
import lithium.service.domain.services.EcosystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
public class EcosystemSystemController implements EcosystemClient {
  //TODO: Wire up an interface and provide other services (service-user) with endpoints they will need.
  // Allowed to reg in ecosystem (domain list) (mutual exclusivity check)
  // Data propagation to domains in ecosystem (domain list)
  // Special login allowed via referential login of domain in ecosystem (dom 1 logged, allow login to domain 2 for same user) (domain list)

  private EcosystemService ecosystemService;

  @Autowired
  public EcosystemSystemController(EcosystemService ecosystemService) {
    this.ecosystemService = ecosystemService;
  }

  @Cacheable(value = "lithium.service.domain.ecosystem.domain-relationships.by-ecosystem-name", unless = "#result == null")
  @Override
  @PostMapping("/system/ecosystem/domain-relationship/list-by-ecosystem-name")
  @ResponseBody
  public ArrayList<EcosystemDomainRelationship> listEcosystemDomainRelationshipsByEcosystemName(
      @RequestParam("ecosystemName") String ecosystemName)
      throws Status469InvalidInputException {
    return ecosystemService.listEcosystemDomainRelationshipsByEcosystemName(ecosystemName);
  }

  @Cacheable(value = "lithium.service.domain.ecosystem.domain-relationships.by-ecosystem-name", unless = "#result == null")
  @Override
  @PostMapping("/system/ecosystem/domain-relationship/list-by-domain-name")
  @ResponseBody
  public ArrayList<EcosystemDomainRelationship> listEcosystemDomainRelationshipsByDomainName(
      @RequestParam("domainName") String domainName)
      throws Status469InvalidInputException {
    return ecosystemService.listEcosystemDomainRelationshipsByDomainName(domainName);
  }


  @Cacheable(value = "lithium.service.domain.ecosystem.domain-in-any-ecosystem", unless = "#result == null")
  @Override
  @PostMapping("/system/ecosystem/is-domain-in-any-ecosystem")
  @ResponseBody
  public boolean isDomainInAnyEcosystem(String domainName) {
    return ecosystemService.isDomainInAnyEcosystem(domainName);
  }
}

