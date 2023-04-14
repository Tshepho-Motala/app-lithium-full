package lithium.service.casino.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.casino.data.entities.AutoBonusAllocation;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.repositories.AutoBonusAllocationRepository;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.UserEventClient;
import lithium.service.user.client.objects.UserEvent;

@Service
public class AutoBonusAllocationService {
	@Autowired AutoBonusAllocationRepository repo;
	@Autowired DomainRepository domainRepo;
	@Autowired LithiumServiceClientFactory services;
	
	public static final Long BONUS_TYPE_SIGNUP = 0L;
	public static final Long BONUS_TYPE_DEPOSIT = 1L;
	
	private Domain findOrCreateDomain(String domainName) {
		Domain domain = domainRepo.findByName(domainName);
		if (domain == null) {
			domain = domainRepo.save(Domain.builder().name(domainName).build());
		}
		return domain;
	}
	
	public boolean checkRequest(String domainName, String token) {
		if (repo.findByDomainAndToken(findOrCreateDomain(domainName), token) == null) return true;
		return false;
	}
	
	public AutoBonusAllocation saveRequest(String domainName, String token, String requestIp, String requestedBy,
			String playerGuid,Long bonusType, Long bonusId, Long amountCents) {
		return repo.save(
			AutoBonusAllocation.builder()
			.domain(findOrCreateDomain(domainName))
			.token(token)
			.requestDate(new Date())
			.requestIp(requestIp)
			.requestedBy(requestedBy)
			.playerGuid(playerGuid)
			.bonusType(bonusType)
			.bonusId(bonusId)
			.amountCents(amountCents)
			.build()
		);
	}
	
	public AutoBonusAllocation writeUserEvent(AutoBonusAllocation autoBonusAllocation, String playerDomainName,
			String playerUsername, Long amountCents) throws LithiumServiceClientFactoryException {
		UserEventClient client = services.target(UserEventClient.class, "service-user", true);
		Response<UserEvent> response = client.registerEvent(playerDomainName, playerUsername,
			UserEvent.builder()
			.type("AUTO_DEPOSIT_BONUS_DEP")
			.message("Auto deposit bonus allocation: deposit amount")
			.data(amountCents.toString())
			.build()
		);
		if (response.isSuccessful()) {
			autoBonusAllocation.setUserEventId(response.getData().getId());
			return repo.save(autoBonusAllocation);
		}
		return null;
	}
}
