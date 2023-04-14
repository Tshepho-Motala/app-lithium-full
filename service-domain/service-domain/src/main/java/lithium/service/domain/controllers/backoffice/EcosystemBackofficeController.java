package lithium.service.domain.controllers.backoffice;

import java.security.Principal;
import java.util.ArrayList;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status469InvalidInputException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.client.objects.ecosystem.Ecosystem;
import lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship;
import lithium.service.domain.client.objects.ecosystem.EcosystemRelationshipType;
import lithium.service.domain.services.EcosystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
public class EcosystemBackofficeController {

  private EcosystemService ecosystemService;

  @Autowired
  public EcosystemBackofficeController(EcosystemService ecosystemService) {
    this.ecosystemService = ecosystemService;
  }

  @GetMapping("/backoffice/ecosystem/list")
  @ResponseBody
  public ArrayList<Ecosystem> listEcosystems() {
    return ecosystemService.listEcosystems();
  }

  @PostMapping("/backoffice/ecosystem/create-or-modify")
  @ResponseBody
  public Ecosystem createOrModifyEcosystem(@RequestBody Ecosystem ecosystem, Principal principal) throws Status469InvalidInputException {
    return ecosystemService.createOrModifyEcosystem(ecosystem, principal);
  }

  @GetMapping("/backoffice/ecosystem/relationship-type/list")
  @ResponseBody
  public ArrayList<EcosystemRelationshipType> listRelationshipTypes() {
    return ecosystemService.listEcosystemRelationshipTypes();
  }

  @GetMapping("/backoffice/ecosystem/domain-relationship/list")
  @ResponseBody
  public ArrayList<EcosystemDomainRelationship> listEcosystemDomainRelationships(
      @RequestParam("ecosystemName") String ecosystemName)
      throws Status469InvalidInputException {
    return ecosystemService.listEcosystemDomainRelationshipsByEcosystemName(ecosystemName);
  }

  @PostMapping("/backoffice/ecosystem/domain-relationship/list-by-domain-name")
  @ResponseBody
  public ArrayList<EcosystemDomainRelationship> listEcosystemDomainRelationshipsByDomainName(
      @RequestParam("domainName") String domainName) {
      try {
        return ecosystemService.listEcosystemDomainRelationshipsByDomainName(domainName);
      } catch (Status469InvalidInputException status469InvalidInputException) {
        return new ArrayList<>();
      }
  }

  @GetMapping("/backoffice/ecosystem/domain-relationship/add")
  @ResponseBody
  public lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship addEcosystemDomainRelationship(
      @RequestParam("ecosystemId") Long ecosystemId,
      @RequestParam("domainId") Long domainId,
      @RequestParam("relationshipTypeId") Long relationshipTypeId, Principal principal)
      throws Status469InvalidInputException, Status500InternalServerErrorException {

    return ecosystemService.addEcosystemDomainRelationship(ecosystemId, domainId, relationshipTypeId, principal);
  }

  @GetMapping("/backoffice/ecosystem/domain-relationship/remove")
  @ResponseBody
  public boolean removeEcosystemDomainRelationship(@RequestParam("ecosystemDomainRelationshipId") Long ecosystemDomainRelationshipId, Principal principal) throws Status469InvalidInputException {
    return ecosystemService.removeEcosystemDomainRelationship(ecosystemDomainRelationshipId, principal);
  }

  @GetMapping("/backoffice/ecosystem/domain-relationship/edit/disable-root-welcome-email")
  @ResponseBody
  public boolean editEcosystemDomainDomainRootWelcomeEmail(@RequestParam("ecosystemDomainRelationshipId") Long ecosystemDomainRelationshipId, @RequestParam("status") boolean status, Principal principal) throws Status469InvalidInputException {
    return ecosystemService.editEcosystemDomainDomainRootWelcomeEmail(ecosystemDomainRelationshipId, status, principal);
  }
}
