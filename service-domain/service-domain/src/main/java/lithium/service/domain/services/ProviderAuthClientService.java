package lithium.service.domain.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.domain.client.exceptions.Status475ProviderAuthClientExistException;
import lithium.service.domain.client.exceptions.Status551ProviderAuthClientNotFoundException;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.ProviderAuthClient;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.ProviderAuthClientRepository;
import lithium.service.domain.data.specifications.ProviderAuthClientSpecification;
import lithium.tokens.LithiumTokenUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class ProviderAuthClientService {
	@Autowired DomainRepository domainRepository;
	@Autowired ProviderAuthClientRepository providerAuthClientRepository;
	@Autowired @Setter ChangeLogService changeLogService;
	@Autowired @Setter MessageSource messageSource;

	public Page<ProviderAuthClient> table(String domainName, String searchValue, Pageable pageable) {
		Specification<ProviderAuthClient> spec = Specification.where(ProviderAuthClientSpecification.domainName(domainName));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<ProviderAuthClient> s = Specification.where(ProviderAuthClientSpecification.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<ProviderAuthClient> result = providerAuthClientRepository.findAll(spec, pageable);
		return result;
	}
	
	public ProviderAuthClient add(ProviderAuthClient providerAuthClient, LithiumTokenUtil lithiumTokenUtil, Locale locale) throws Status475ProviderAuthClientExistException {
		log.info("DomainUserClient add [domainUserClient="+ providerAuthClient +"]");
		if (providerAuthClientRepository.findByDomainAndCode(providerAuthClient.getDomain(), providerAuthClient.getCode()) != null) {
			throw new Status475ProviderAuthClientExistException(messageSource.getMessage("UI_NETWORK_ADMIN.DOMAIN.CLIENTS.ERR.EXISTS", null, locale));
		}
		providerAuthClient = providerAuthClientRepository.save(providerAuthClient);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.copy(providerAuthClient, new ProviderAuthClient(), new String[] { "code", "description", "password" });
			changeLogService.registerChangesWithDomain(
				"providerAuthClient",
				"create",
				providerAuthClient.getId(),
				lithiumTokenUtil.guid(),
				null,
				null,
				clfc, Category.SUPPORT, SubCategory.PROVIDER, 0, providerAuthClient.getDomain().getName()
			);
		} catch (Exception e) {
			log.error("Could not save changelog.", e);
		}
		return providerAuthClient;
	}

	public ProviderAuthClient save(ProviderAuthClient providerAuthClient, LithiumTokenUtil lithiumTokenUtil, Locale locale) throws Status475ProviderAuthClientExistException {
		log.info("DomainUserClient save [domainUserClient="+ providerAuthClient +"]");

		ProviderAuthClient pacFromDb = providerAuthClientRepository.findOne(providerAuthClient.getId());
		ProviderAuthClient pacCopy = ProviderAuthClient.builder().build();
		BeanUtils.copyProperties(pacFromDb, pacCopy);

		pacFromDb.setCode(providerAuthClient.getCode());
		pacFromDb.setDescription(providerAuthClient.getDescription());
		pacFromDb.setPassword(providerAuthClient.getPassword());
		pacFromDb.setGuid();

		if (providerAuthClientRepository.findByDomainAndCodeAndIdNot(pacFromDb.getDomain(), pacFromDb.getCode(), pacFromDb.getId()) != null) {
			throw new Status475ProviderAuthClientExistException(messageSource.getMessage("UI_NETWORK_ADMIN.DOMAIN.CLIENTS.ERR.EXISTS", null, locale));
		}

		pacFromDb = providerAuthClientRepository.save(pacFromDb);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.copy(pacFromDb, pacCopy, new String[] { "guid", "code", "description", "password" });
			changeLogService.registerChangesWithDomain(
				"providerAuthClient",
				"edit",
				providerAuthClient.getId(),
				lithiumTokenUtil.guid(),
				null,
				null,
				clfc, Category.SUPPORT, SubCategory.PROVIDER, 0, providerAuthClient.getDomain().getName()
			);
		} catch (Exception e) {
			log.error("Could not save changelog.", e);
		}
		return pacFromDb;
	}

	public ProviderAuthClient find(Domain domain, String code) throws Status551ProviderAuthClientNotFoundException {
		log.info("Finding ProviderAuthClient for : "+domain.getName()+"/"+code);
		ProviderAuthClient pac = providerAuthClientRepository.findByDomainAndCode(domain, code);
		if (pac == null) throw new Status551ProviderAuthClientNotFoundException(domain.getName()+"/"+code+" not found.");
		return pac;
	}

	public void delete(ProviderAuthClient providerAuthClient) {
		log.info("Deleting ProviderAuthClient : "+providerAuthClient);
		providerAuthClientRepository.delete(providerAuthClient);
	}
}
