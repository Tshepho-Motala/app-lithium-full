package lithium.service.raf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.client.data.BonusAllocate;
import lithium.service.casino.client.stream.TriggerBonusStream;
import lithium.service.raf.data.entities.Configuration;

@Service
public class BonusService {
	@Autowired TriggerBonusStream triggerBonusStream;
	@Autowired ConfigurationService configService;
	
	public void triggerReferralBonus(String domainName, String referrerGuid, String refereeGuid) {
		Configuration config = configService.findOrCreate(domainName);
		if (config.getReferrerBonusCode() != null && !config.getReferrerBonusCode().isEmpty()) {
			triggerBonusStream.process(
				BonusAllocate.builder()
				.bonusCode(config.getReferrerBonusCode())
				.playerGuid(referrerGuid)
				.build());
		}
		if (config.getRefereeBonusCode() != null && !config.getRefereeBonusCode().isEmpty()) {
			triggerBonusStream.process(
				BonusAllocate.builder()
				.bonusCode(config.getRefereeBonusCode())
				.playerGuid(refereeGuid)
				.build());
		}
	}
}
