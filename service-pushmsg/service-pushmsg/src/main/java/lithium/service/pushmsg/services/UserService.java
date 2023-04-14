package lithium.service.pushmsg.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.pushmsg.client.internal.DeviceEditRequest;
import lithium.service.pushmsg.client.internal.DeviceEditResponse;
import lithium.service.pushmsg.client.internal.DeviceRequest;
import lithium.service.pushmsg.client.internal.DeviceResponse;
import lithium.service.pushmsg.client.internal.DoProviderClient;
import lithium.service.pushmsg.data.entities.DomainProvider;
import lithium.service.pushmsg.data.entities.DomainProviderProperty;
import lithium.service.pushmsg.data.entities.ExternalUser;
import lithium.service.pushmsg.data.entities.User;
import lithium.service.pushmsg.data.repositories.ExternalUserRepository;
import lithium.service.pushmsg.data.repositories.UserRepository;
import lithium.service.pushmsg.data.specifications.PushMsgUserSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	@Autowired DomainService domainService;
	@Autowired DomainProviderService domainProviderService;
	@Autowired UserRepository userRepository;
	@Autowired ExternalUserRepository externalUserRepository;
	@Autowired LithiumServiceClientFactory serviceFactory;
	
	public List<User> find(String domainName) {
		return userRepository.findByDomainName(domainName);
	}
	
	public List<User> find(String domainName, String search) {
		return userRepository.findByDomainNameAndGuidContains(domainName, search);
	}
	
	public Page<User> findAll(String domainName, String search, PageRequest pageRequest) {
		Specification<User> spec = Specification.where(PushMsgUserSpecification.table(domainName, search));
		return userRepository.findAll(spec, pageRequest);
	}
	
	public User findOrCreate(String guid) {
		User user = userRepository.findByGuid(guid);
		if (user == null) user = userRepository.save(User.builder().guid(guid).domain(domainService.findOrCreate(guid.split("/")[0])).build());
		return user;
	}
	
	public User findAndUpdate(String guid, String uuid) {
		log.info("findAndUpdate("+guid+", "+uuid+")");
		User user = findOrCreate(guid);
		addExternalId(user, uuid);
		user.setExternalUsers(updateDeviceInfo(user));
		return user;
	}
	
	public User toggleOptOut(String guid) {
		User user = findOrCreate(guid);
		Boolean optOut = user.getOptOut();
		if (optOut == null) optOut = false;
		user.setOptOut(!optOut);
		user = userRepository.save(user);
		
		List<DomainProvider> domainProviders = domainProviderService.findAll(user.domainName());
		List<ExternalUser> externalUsers = externalUserRepository.findByUser(user);
		
		for (DomainProvider domainProvider:domainProviders) {
			log.debug("DomainProvider : "+domainProvider);
			if (domainProvider.getEnabled()==false) continue;
			DoProviderClient client = null;
			try {
				client = serviceFactory.target(DoProviderClient.class,
					domainProvider.getProvider().getUrl(),
					true
				);
				Map<String, String> properties = new HashMap<>();
				for (DomainProviderProperty prop: domainProviderService.propertiesWithDefaults(domainProvider.getId())) {
					properties.put(prop.getProviderProperty().getName(), prop.getValue());
				}
				log.info("toggleOptOut :: "+user);
				for (ExternalUser eu:externalUsers) {
					DeviceEditResponse dr = client.editDevice(
						DeviceEditRequest.builder()
						.uuid(eu.getUuid())
						.appId(properties.get("appId"))
						.properties(properties)
						.notificationTypes((optOut)?-2:1)
						.build()
					);
					log.info("DeviceEditResponse : "+dr);
				}
			} catch (LithiumServiceClientFactoryException e) {
				log.error(e.getMessage(), e);
			}
		}
		return user;
	}
	
	public List<User> usersFromGuid(List<String> userGuids) {
		List<User> users = new ArrayList<>();
		for (String guid:userGuids) {
			users.add(userRepository.findByGuid(guid));
		}
		return users;
	}
	
	public User findAndUpdate(String guid, List<String> externalPlayerIds) {
		User user = findOrCreate(guid);
		List<ExternalUser> eus = new ArrayList<>();
		for (String uuid:externalPlayerIds) {
			eus.add(addExternalId(user, uuid));
		}
//		user.setExternalUsers(eus);
		user.setExternalUsers(updateDeviceInfo(user));
		return user;
	}
	
	public List<ExternalUser> updateDeviceInfo(User user) {
		List<DomainProvider> domainProviders = domainProviderService.findAll(user.domainName());
		List<ExternalUser> externalUsers = externalUserRepository.findByUser(user);
		
		for (DomainProvider domainProvider:domainProviders) {
			log.debug("DomainProvider : "+domainProvider);
			if (domainProvider.getEnabled()==false) continue;
			DoProviderClient client = null;
			try {
				client = serviceFactory.target(DoProviderClient.class,
					domainProvider.getProvider().getUrl(),
					true
				);
				Map<String, String> properties = new HashMap<>();
				for (DomainProviderProperty prop: domainProviderService.propertiesWithDefaults(domainProvider.getId())) {
					properties.put(prop.getProviderProperty().getName(), prop.getValue());
				}
				for (ExternalUser eu:externalUsers) {
					DeviceResponse dr = client.deviceInfo(
						DeviceRequest.builder()
						.uuid(eu.getUuid())
						.properties(properties)
						.build()
					);
					eu = externalUserRepository.save(
						eu.toBuilder()
						.sessionCount(dr.getSessionCount())
						.deviceOs(dr.getDeviceOs())
						.deviceModel(dr.getDeviceModel())
						.deviceType(dr.getDeviceType())
						.lastActive(dr.getLastActive())
						.createdAt(dr.getCreatedAt())
						.ip(dr.getIp())
						.build()
					);
				}
			} catch (LithiumServiceClientFactoryException e) {
				log.error(e.getMessage(), e);
			}
		}
		return externalUsers;
	}
	
	public ExternalUser addExternalId(String guid, String uuid) {
		ExternalUser eu = externalUserRepository.findByUserGuidAndUuid(guid, uuid);
		if (eu == null) {
			User user = findOrCreate(guid);
			eu = externalUserRepository.save(ExternalUser.builder().user(user).uuid(uuid).build());
		}
		return eu;
	}
	public ExternalUser addExternalId(User user, String uuid) {
		log.debug("addExternalId("+user+", "+uuid+")");
		ExternalUser eu = externalUserRepository.findByUserAndUuid(user, uuid);
		if (eu == null) {
			eu = externalUserRepository.save(ExternalUser.builder().user(user).uuid(uuid).build());
		}
		return eu;
	}
	public User findUserByUuid(String uuid) {
		ExternalUser eu = externalUserRepository.findByUuid(uuid);
		User user = eu.getUser();
		return user;
	}
	public boolean unsubscribe(String uuid) {
		log.debug("Unsubscribing user with devide player id " + uuid);
		boolean isSubscribed = false;
		try {
			User user = findUserByUuid(uuid);
			log.debug("user with id user uuid" + uuid+ "found " + user.toString());
			user.setOptOut(true);
			if (null != userRepository.save(user)) {
				isSubscribed = true;
			}
 		} catch (NullPointerException nonSusnscribedEx) {
			isSubscribed = false;
		}
		return isSubscribed;
	}
}