package lithium.service.xp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.raf.client.objects.RAFConversionRequest;
import lithium.service.raf.client.stream.RAFConversionStream;
import lithium.service.raf.enums.RAFConversionType;

@Service
public class ReferralService {
	@Autowired RAFConversionStream rafConversionStream;
	
	public void triggerRAFConversion(String playerGuid, Integer xpLevel) {
		rafConversionStream.process(
			RAFConversionRequest.builder()
			.playerGuid(playerGuid)
			.type(RAFConversionType.XP_LEVEL)
			.xpLevel(xpLevel).build()
		);
	}
}
