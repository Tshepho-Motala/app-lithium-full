package lithium.service.domain.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status469InvalidInputException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.Ecosystem;
import lithium.service.domain.data.entities.EcosystemDomainRelationship;
import lithium.service.domain.data.entities.EcosystemRelationshipType;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.EcosystemDomainRelationshipRepository;
import lithium.service.domain.data.repositories.EcosystemRelationshipTypeRepository;
import lithium.service.domain.data.repositories.EcosystemRepository;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
public class EcosystemService {

  private ChangeLogService changeLogService;
  private DomainRepository domainRepository;
  private EcosystemRepository ecosystemRepository;
  private EcosystemRelationshipTypeRepository ecosystemRelationshipTypeRepository;
  private EcosystemDomainRelationshipRepository ecosystemDomainRelationshipRepository;
  private ModelMapper mapper;

  @Autowired
  public EcosystemService(
      ChangeLogService changeLogService,
      DomainRepository domainRepository,
      EcosystemRepository ecosystemRepository,
      EcosystemRelationshipTypeRepository ecosystemRelationshipTypeRepository,
      EcosystemDomainRelationshipRepository ecosystemDomainRelationshipRepository,
      ModelMapper mapper) {
    this.changeLogService = changeLogService;
    this.domainRepository = domainRepository;
    this.ecosystemRepository = ecosystemRepository;
    this.ecosystemRelationshipTypeRepository = ecosystemRelationshipTypeRepository;
    this.ecosystemDomainRelationshipRepository = ecosystemDomainRelationshipRepository;
    this.mapper = mapper;
  }

  public ArrayList<lithium.service.domain.client.objects.ecosystem.Ecosystem> listEcosystems() {
    return ecosystemRepository
        .findByDeletedFalse()
        .stream()
        .map(es -> mapper.map(es, lithium.service.domain.client.objects.ecosystem.Ecosystem.class))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public lithium.service.domain.client.objects.ecosystem.Ecosystem createOrModifyEcosystem(
      final lithium.service.domain.client.objects.ecosystem.Ecosystem ecosystem, Principal principal)
      throws Status469InvalidInputException {
    Ecosystem internalEcosystem = null;

    //Check for existence or init
    String mode = "create";
    if (ecosystem.getId() != null && ecosystem.getId() > 0L) {
      internalEcosystem = ecosystemRepository.findOne(ecosystem.getId());
      mode = "edit";
      if (internalEcosystem == null) {
        throw new Status469InvalidInputException("Invalid ecosystem id provided.");
      }
    } else {
      internalEcosystem = Ecosystem.builder().name(ecosystem.getName()).build();
    }
    Ecosystem oldEcosystem = internalEcosystem;
    //Check for unique name
    Ecosystem nameCheckEcosystem = ecosystemRepository.findByName(ecosystem.getName());
    if (nameCheckEcosystem != null && nameCheckEcosystem.getId() != internalEcosystem.getId()) {
      throw new Status469InvalidInputException("An ecosystem with name " + ecosystem.getName() + " already exists.");
    }

    if (ecosystem.getDeleted()) {
      // Set the deleted ecosystem name to something new for collision avoidance of future ecosystem names
      if (ecosystem.getName().length() > 20) {
        ecosystem.setName(ecosystem.getName().substring(0, 20));
      }
      internalEcosystem.setName(ecosystem.getName() + "_" + System.currentTimeMillis());
      //ecosystem is being deleted, delete relationships too, otherwise other checks will fail.
      List<EcosystemDomainRelationship> relationshipList = ecosystemDomainRelationshipRepository.findByEcosystemId(ecosystem.getId());
      for (EcosystemDomainRelationship edr:relationshipList) {
        edr.setDeleted(true);
        edr.setEnabled(false);
        ecosystemDomainRelationshipRepository.save(edr);
      }
    }
    internalEcosystem.setDisplayName(ecosystem.getDisplayName());
    internalEcosystem.setDescription(ecosystem.getDescription());
    internalEcosystem.setEnabled( (ecosystem.getDeleted()) ? false : ecosystem.getEnabled() );
    internalEcosystem.setDeleted(ecosystem.getDeleted());
    ecosystemRepository.save(internalEcosystem);
    try {
      List<ChangeLogFieldChange> clfc = new ArrayList<>();
      clfc.add(ChangeLogFieldChange.builder().field("displayName")
        .fromValue((oldEcosystem == null) ? "" : oldEcosystem.getDisplayName())
        .toValue(ecosystem.getDisplayName())
        .build());

        clfc.add(ChangeLogFieldChange.builder().field("description")
        .fromValue((oldEcosystem == null) ? "" : oldEcosystem.getDescription())
        .toValue(internalEcosystem.getDescription())
        .build());

      changeLogService.registerChangesWithDomain("ecosystem", mode, internalEcosystem.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.ECOSYSTEM, 0, "default");

    } catch (Exception e){
      log.warn("Unable to complete changelog for ecosystem add: " + internalEcosystem);
    }
    return mapper.map(internalEcosystem, lithium.service.domain.client.objects.ecosystem.Ecosystem.class);
  }

  public ArrayList<lithium.service.domain.client.objects.ecosystem.EcosystemRelationshipType> listEcosystemRelationshipTypes() {
    return ecosystemRelationshipTypeRepository.findByEnabledTrueAndDeletedFalse()
        .stream()
        .map(ert -> mapper.map(ert, lithium.service.domain.client.objects.ecosystem.EcosystemRelationshipType.class))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Cacheable(value = "lithium.service.domain.ecosystem.domain-relationships.by-ecosystem-name", unless = "#result == null")
  public ArrayList<lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship> listEcosystemDomainRelationshipsByEcosystemName(
      final String ecosystemName)
      throws Status469InvalidInputException {
    Ecosystem nameCheckEcosystem = ecosystemRepository.findByName(ecosystemName);
    if (nameCheckEcosystem == null) {
      throw new Status469InvalidInputException("No ecosystem exists with name: " + ecosystemName);
    }

    return ecosystemDomainRelationshipRepository.findByEcosystemAndDeletedFalseAndEnabledTrue(nameCheckEcosystem)
        .stream()
        .map(edr -> {
          try {
            //Need to do this mapping to avoid the circular dependency for serialization. ModelMapper does not consider excluded elements when mapping
            ObjectMapper om = new ObjectMapper();
            om.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return om.readValue(JsonStringify.objectsToString(edr),
                lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship.class);
          } catch (Exception exception) {
            log.error("Problem parsing: " + edr + " message: " + exception.getMessage());
          }
          return null;
        })
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship addEcosystemDomainRelationship(
      @RequestParam("ecosystemId") Long ecosystemId,
      @RequestParam("domainId") Long domainId,
      @RequestParam("relationshipTypeId") Long relationshipTypeId, Principal principal)
      throws Status469InvalidInputException,
      Status500InternalServerErrorException {

    Ecosystem ecosystem = ecosystemRepository.findOne(ecosystemId);
    if (ecosystem == null) {
      throw new Status469InvalidInputException("No ecosystem exists with id: " + ecosystemId);
    }

    Domain domain = domainRepository.findOne(domainId);
    if (domain == null) {
      throw new Status469InvalidInputException("No domain exists with id: " + domainId);
    }
    EcosystemRelationshipType relationshipType = ecosystemRelationshipTypeRepository.findOne(relationshipTypeId);
    if (relationshipType == null) {
      throw new Status469InvalidInputException("No ecosystem relationship type exists with id: " + relationshipTypeId);
    }

    EcosystemDomainRelationship ecosystemDomainRelationship = ecosystemDomainRelationshipRepository.findByDomainNameAndEcosystemAndRelationship(domain.getName(), ecosystem, relationshipType);
    if (ecosystemDomainRelationship == null) {
      ecosystemDomainRelationship = EcosystemDomainRelationship.builder()
          .ecosystem(ecosystem)
          .domain(domain)
          .relationship(relationshipType)
          .build();
    } else {
      ecosystemDomainRelationship.setDeleted(false);
      ecosystemDomainRelationship.setEnabled(true);
    }
    try {
      ecosystemDomainRelationship = ecosystemDomainRelationshipRepository.save(ecosystemDomainRelationship);
    } catch(Exception exception) {
        log.error("Problem adding: " + ecosystemDomainRelationship + " message: " + exception.getMessage());
        throw new Status469InvalidInputException("Problem adding: " + ecosystemDomainRelationship );
    }

    if(ecosystemDomainRelationship != null && ecosystemDomainRelationship.getId() != null){
      try {
        List<ChangeLogFieldChange> clfc = changeLogService.copy(ecosystemDomainRelationship, new EcosystemDomainRelationship(),
            new String[] {"ecosystem", "domain","relationship"});
        changeLogService.registerChangesWithDomain("domainrelationship","create", ecosystemDomainRelationship.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.ECOSYSTEM, 0,
            domain.getName());
      } catch (Exception e){
        log.warn("Unable to complete changelog for ecosystem add: " + ecosystemDomainRelationship);
      }
    }

    try {
      //Need to do this mapping to avoid the circular dependency for serialization. ModelMapper does not consider excluded elements when mapping
      ObjectMapper om = new ObjectMapper();
      om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return om.readValue(JsonStringify.objectsToString(ecosystemDomainRelationship), lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship.class);
    } catch (Exception exception) {
      log.error("Problem parsing: " + ecosystemDomainRelationship + " message: " + exception.getMessage());
      throw new Status500InternalServerErrorException(exception.getMessage(), exception);
    }
  }

  public boolean removeEcosystemDomainRelationship(Long ecosystemDomainRelationshipId, Principal principal) throws Status469InvalidInputException {
    EcosystemDomainRelationship ecosystemDomainRelationship = ecosystemDomainRelationshipRepository.findOne(ecosystemDomainRelationshipId);
    if (ecosystemDomainRelationship == null) {
      throw new Status469InvalidInputException("No ecosystem domain relationship exists with id: " + ecosystemDomainRelationshipId);
    }
    ecosystemDomainRelationship.setEnabled(false);
    ecosystemDomainRelationship.setDeleted(true);
    ecosystemDomainRelationshipRepository.save(ecosystemDomainRelationship);
    try {
      List<ChangeLogFieldChange> clfc = changeLogService.copy(ecosystemDomainRelationship, new EcosystemDomainRelationship(),
          new String[] {"deleted", "enabled"});
      changeLogService.registerChangesWithDomain(
          "domainrelationship",
          "delete",
          ecosystemDomainRelationship.getId(),
          principal.getName(),
          null,
          null,
          clfc, Category.SUPPORT, SubCategory.ECOSYSTEM, 0, ecosystemDomainRelationship.getDomain().getName());
    } catch (Exception e) {
      log.warn("Unable to complete changelog for domain relationship delete:" + ecosystemDomainRelationshipId);
    }
    return true;
  }

  @Cacheable(value = "lithium.service.domain.ecosystem.domain-relationships.by-domain-name", unless = "#result == null")
  public ArrayList<lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship> listEcosystemDomainRelationshipsByDomainName(
      String domainName)
      throws Status469InvalidInputException {

    EcosystemDomainRelationship ecosystemDomainRelationship = ecosystemDomainRelationshipRepository.findByDomainNameAndDeletedFalseAndEnabledTrueAndEcosystemEnabledTrue(domainName);
    if (ecosystemDomainRelationship == null) {
      throw new Status469InvalidInputException("No domain exists within an ecosystem with the domain name: " + domainName);
    }
    return listEcosystemDomainRelationshipsByEcosystemName(ecosystemDomainRelationship.getEcosystem().getName());
  }

  @Cacheable(value = "lithium.service.domain.ecosystem.domain-in-any-ecosystem", unless = "#result == null")
  public boolean isDomainInAnyEcosystem(final String domainName) {
    if (ecosystemDomainRelationshipRepository.findByDomainNameAndDeletedFalseAndEnabledTrueAndEcosystemEnabledTrue(domainName) != null) {
      return true;
    }
    return false;
  }

  public boolean editEcosystemDomainDomainRootWelcomeEmail(Long ecosystemDomainRelationshipId, boolean status, Principal principal)
      throws Status469InvalidInputException {

    EcosystemDomainRelationship ecosystemDomainRelationship = ecosystemDomainRelationshipRepository.findOne(ecosystemDomainRelationshipId);

    if (ecosystemDomainRelationship == null) {
      throw new Status469InvalidInputException("No ecosystem domain relationship exists with id: " + ecosystemDomainRelationshipId);
    }

    ecosystemDomainRelationship.setDisableRootWelcomeEmail(status);

    ecosystemDomainRelationshipRepository.save(ecosystemDomainRelationship);

    try {
      List<ChangeLogFieldChange> clfc = changeLogService.copy(ecosystemDomainRelationship, new EcosystemDomainRelationship(),
          new String[] {"disableRootWelcomeEmail"});
      changeLogService.registerChangesWithDomain(
          "domainrelationship",
          "edit",
          ecosystemDomainRelationship.getId(),
          principal.getName(),
          null,
          null,
          clfc, Category.SUPPORT, SubCategory.ECOSYSTEM, 0, ecosystemDomainRelationship.getDomain().getName());
    } catch (Exception e) {
      log.warn("Unable to complete changelog for domain relationship delete:" + ecosystemDomainRelationshipId);
    }

    return true;
  }
}
