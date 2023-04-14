package lithium.service.cashier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.raf.client.objects.RAFConversionRequest;
import lithium.service.raf.client.stream.RAFConversionStream;
import lithium.service.raf.enums.RAFConversionType;

@Service
public class ReferralService {
	@Autowired RAFConversionStream rafConversionStream;
	
	public void triggerRAFConversion(String playerGuid) {
		rafConversionStream.process(RAFConversionRequest.builder().playerGuid(playerGuid).type(RAFConversionType.DEPOSIT).build());
	}
}
