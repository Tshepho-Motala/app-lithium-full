package lithium.service.raf.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.raf.client.objects.RAFConversionRequest;
import lithium.service.raf.data.entities.Configuration;
import lithium.service.raf.data.entities.Referral;
import lithium.service.raf.enums.RAFConversionType;
import lithium.service.raf.services.ConfigurationService;
import lithium.service.raf.services.ReferralService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(RAFConversionQueueSink.class)
@Slf4j
public class RAFConversionQueueProcessor {
	@Autowired ReferralService referralService;
	@Autowired ConfigurationService configurationService;
	
	@StreamListener(RAFConversionQueueSink.INPUT) 
	public void handle(RAFConversionRequest request) throws Exception {
		log.info("Received RAF conversion request " + request);
		String domainAndPlayer[] = request.getPlayerGuid().split("/");
		Referral referral = referralService.findByPlayerGuid(domainAndPlayer[0], domainAndPlayer[1]);
		if (referral != null && !referral.getConverted()) {
			Configuration configuration = configurationService.findOrCreate(domainAndPlayer[0]);
			if (configuration.getConversionType().getId().compareTo(request.getType().getId()) == 0) {
				boolean proceed = true;
				if (configuration.getConversionType().getType().contentEquals(RAFConversionType.XP_LEVEL.getType())) {
					if (request.getXpLevel() == null || configuration.getConversionXpLevel() == null ||
							configuration.getConversionXpLevel().compareTo(request.getXpLevel()) != 0) {
						proceed = false;
					}
				}
				if (proceed) referralService.processConversion(referral);
			}
		}
	}
}