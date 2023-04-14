package lithium.service.domain.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lithium.exceptions.Status400BadRequestException;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainRevision;
import lithium.service.domain.data.entities.DomainRevisionLabelValue;
import lithium.service.domain.data.objects.DomainRevisionBasic;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.DomainRevisionRepository;
import lithium.service.domain.data.specifications.DomainRevisionSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DomainSettingsService {
	@Autowired DomainRepository domainRepo;
	@Autowired DomainRevisionRepository domainRevisionRepo;
	@Autowired DomainLabelValueService domainLabelValueService;

	public DomainRevisionLabelValue findCurrentDomainSetting(String domainName, String settingName) throws Exception {
		List<DomainRevisionLabelValue> settings = findCurrentDomainSettings(domainName);
		Optional<DomainRevisionLabelValue> setting = settings.stream().filter(drlv -> {
			return drlv.getLabelValue().getLabel().getName().contentEquals(settingName);
		}).findFirst();
		return (setting.isPresent()) ? setting.get() : null;
	}

	public List<DomainRevisionLabelValue> findCurrentDomainSettings(String domainName) throws Exception {
		Domain domain = domainRepo.findByName(domainName);
		if (domain == null) throw new Exception("Domain " + domainName + " does not exist");
		return (domain.getCurrent() != null)? domain.getCurrent().getLabelValueList(): new ArrayList<>();
	}

	public Page<DomainRevision> findSettingsHistoryByDomain(String domainName, String searchValue, Pageable pageable) {
		Specification<DomainRevision> spec = Specification.where(DomainRevisionSpecification.domainName(domainName));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<DomainRevision> s = Specification.where(DomainRevisionSpecification.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<DomainRevision> result = domainRevisionRepo.findAll(spec, pageable);
		return result;
	}

  @CacheEvict(value = "lithium.service.kyc.api.controller.method-list", allEntries = true)
  public DomainRevision add(DomainRevisionBasic domainRevisionBasic) throws Status400BadRequestException {
    log.info("DomainRevision add [domainRevisionBasic=" + domainRevisionBasic + "]");
    return domainLabelValueService.save(domainRevisionBasic);
  }
}
