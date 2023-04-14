package lithium.service.user.services;

import java.util.List;
import java.util.Optional;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.metrics.TimeThisMethod;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.translate.client.objects.LoginError;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class DomainService {
	@Autowired DomainRepository domainRepository;
  @Autowired CachingDomainClientService cachingDomainClientService;
  @Autowired UserService userService;
  @Autowired MessageSource messageSource;

  public Domain findOrCreate(String name) {
    Domain domain = domainRepository.findByName(name.toLowerCase());
    if (domain == null) {
      domain = Domain.builder().name(name.toLowerCase()).build();
      domainRepository.save(domain);
    }
    return domain;
  }

  @TimeThisMethod
  public String domainResolver(String ecosystemNameOrDomainName, String uniqueIdentifier)
      throws Status403AccessDeniedException {
    if (cachingDomainClientService.isEcosystemName(ecosystemNameOrDomainName)) {
      // Ecosystem name provided
      String ecosystemName = ecosystemNameOrDomainName;
      List<String> meDomains = cachingDomainClientService.listMutuallyExclusiveDomainsWithinAnEcosystem(ecosystemName);
      if (meDomains.isEmpty()) {
        throw new Status403AccessDeniedException(LoginError.ACCESS_DENIED.getDescription());
      }
      final Optional<User> byDomainNameAndEmail = userService.findMutualExclusiveUserByDomainNamesAndEmailThenCellphoneNumberThenUsername(meDomains,
          uniqueIdentifier);
      if (byDomainNameAndEmail.isPresent()) {
        return byDomainNameAndEmail.get().getDomain().getName();
      }
      //Return any mutually exclusive domain name as this would provide the Status430UserUpgradeRequiredException since the user does not exist on any mutually exclusive domains
      return meDomains.get(0);
    } else {
      // Domain name provided
      String domainName = ecosystemNameOrDomainName;
      if (!cachingDomainClientService.isDomainName(domainName)) {
        throw new Status403AccessDeniedException(LoginError.ACCESS_DENIED.getDescription());
      }
      return domainName;
    }
  }

  public Optional<String> getRegisteredExclusiveDomainName(String ecosystemName, String uniqueIdentifier) throws Status403AccessDeniedException {
    List<String> meDomains = cachingDomainClientService.listMutuallyExclusiveDomainsWithinAnEcosystem(ecosystemName);
    final Optional<User> byDomainNameAndEmail = userService.findByDomainNameInAndEmail(meDomains,
        uniqueIdentifier);
    return byDomainNameAndEmail.map(user -> user.getDomain().getName());
  }

  public Domain findDomainByName(String name) throws Status550ServiceDomainClientException {
    lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(name);
    return findOrCreate(domain.getName());
  }
}
