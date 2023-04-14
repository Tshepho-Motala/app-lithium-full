package lithium.service.domain.client;

import java.util.ArrayList;
import lithium.exceptions.Status469InvalidInputException;
import lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-domain")
public interface EcosystemClient {

  @RequestMapping(method = RequestMethod.POST, path = "/system/ecosystem/domain-relationship/list-by-ecosystem-name")
  public ArrayList<EcosystemDomainRelationship> listEcosystemDomainRelationshipsByEcosystemName(
      @RequestParam("ecosystemName") final String ecosystemName)
      throws Status469InvalidInputException;

  @RequestMapping(method = RequestMethod.POST, path = "/system/ecosystem/domain-relationship/list-by-domain-name")
  public ArrayList<EcosystemDomainRelationship> listEcosystemDomainRelationshipsByDomainName(
      @RequestParam("domainName") final String domainName)
      throws Status469InvalidInputException;

  @RequestMapping(method = RequestMethod.POST, path = "/system/ecosystem/is-domain-in-any-ecosystem")
  public boolean isDomainInAnyEcosystem(
      @RequestParam("domainName") final String domainName);
}
