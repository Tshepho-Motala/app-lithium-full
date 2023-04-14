package lithium.service.cashier.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProfile;
import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.data.repositories.DomainMethodProfileRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DomainMethodProfileService {
	@Autowired
	private DomainMethodProfileRepository domainMethodProfileRepository;
	
	public DomainMethodProfile createOrUpdate(DomainMethodProfile domainMethodProfile) {
		log.debug("createOrUpdate DomainMethodProfile : "+domainMethodProfile);
		return domainMethodProfileRepository.save(domainMethodProfile);
	}
	
	public DomainMethodProfile find(DomainMethod domainMethod, Profile profile) {
		return domainMethodProfileRepository.findByDomainMethodAndProfile(domainMethod, profile);
	}
	
	public DomainMethodProfile find(Long id) {
		return domainMethodProfileRepository.findOne(id);
	}
	
	public DomainMethodProfile toggleEnable(DomainMethodProfile dm) {
		dm.setEnabled(!dm.getEnabled());
		return domainMethodProfileRepository.save(dm);
	}
	
	public List<DomainMethodProfile> findByProfile(Profile profile) {
		return domainMethodProfileRepository.findByProfile(profile);
	}
}