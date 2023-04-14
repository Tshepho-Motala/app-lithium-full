package lithium.service.casino.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.geo.client.objects.GeoLabelTransactionStreamData;
import lithium.service.geo.client.stream.GeoStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CasinoGeoService {
	
	@Autowired
	private ServiceCasinoConfigurationProperties properties;
	
	@Autowired private GeoStream geoStream;
	

	public void addTransactionGeoDeviceLabels(String userGuid, Long transactionId) throws Exception {
		if (properties.getTransactionGeoDeviceLabels().isEnabled()) {
			geoStream.register(GeoLabelTransactionStreamData.builder().userGuid(userGuid).transactionId(transactionId).build());
		}
	}
}
