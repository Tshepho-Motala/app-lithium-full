package lithium.service.cashier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodUser;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.DomainMethodUserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DomainMethodUserService {
	@Autowired
	private UserService userService;
	@Autowired
	private DomainMethodService domainMethodService;
	@Autowired
	private DomainMethodUserRepository domainMethodUserRepository;
	
	public DomainMethodUser createOrUpdate(DomainMethodUser domainMethodUser) {
		log.info("createOrUpdate DomainMethod : "+domainMethodUser.getDomainMethod().getName()+ " and User GUID: "+domainMethodUser.getUser().getGuid());
		log.debug("createOrUpdate DomainMethodUser:"+domainMethodUser);
		User user = userService.findOrCreate(domainMethodUser.getUser().getGuid());
		
		DomainMethod domainMethod = domainMethodService.find(domainMethodUser.getDomainMethod().getId());
		
		domainMethodUser.setUser(user);
		domainMethodUser.setDomainMethod(domainMethod);
		
		return domainMethodUserRepository.save(domainMethodUser);
	}
	
	public DomainMethodUser find(DomainMethod domainMethod, String userGuid) {
		return domainMethodUserRepository.findByDomainMethodAndUser(domainMethod, userService.findOrCreate(userGuid));
	}
	
	public DomainMethodUser find(Long id) {
		return domainMethodUserRepository.findOne(id);
	}
	
	public DomainMethodUser toggleEnable(DomainMethodUser dm) {
		dm.setEnabled(!dm.getEnabled());
		return domainMethodUserRepository.save(dm);
	}

	public void setEnabled(User user, DomainMethod domainMethod, boolean isEnabled) {
		DomainMethodUser domainMethodUser = domainMethodUserRepository.findByDomainMethodAndUser(domainMethod, user);

		if ( domainMethodUser == null) {
			domainMethodUser = DomainMethodUser.builder().user(user).domainMethod(domainMethod).build();
		} else if (domainMethodUser.getEnabled().booleanValue() == isEnabled) {
			return;
		}

		domainMethodUser.setEnabled(isEnabled);
		domainMethodUserRepository.save(domainMethodUser);
	}

	public boolean getEnabled(User user, DomainMethod domainMethod) {
		DomainMethodUser domainMethodUser = domainMethodUserRepository.findByDomainMethodAndUser(domainMethod, user);
		//return true if domain method user does not exists
		return domainMethodUser == null || domainMethodUser.getEnabled().booleanValue();
	}
}
