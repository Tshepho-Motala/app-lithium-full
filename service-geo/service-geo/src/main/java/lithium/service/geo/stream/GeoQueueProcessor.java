package lithium.service.geo.stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;


import lithium.service.geo.client.objects.GeoLabelTransactionStreamData;
import lithium.service.geo.services.GeoService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(GeoQueueSink.class)
@Slf4j
public class GeoQueueProcessor {
	
	@Autowired GeoService geoService;
	@Autowired ModelMapper mapper;
	

	//FIXME: If error occurs, place tran back on queue or do some magic to keep it on queue until processed (saw something in queue docs, just need to go read about it again).
	@StreamListener(GeoQueueSink.INPUT)
	void handle(GeoLabelTransactionStreamData entry) throws Exception {
		log.info("Received a transaction from the queue for processing: " + entry);
		
		geoService.addTransactionGeoDeviceLabels(entry.getUserGuid(), entry.getTransactionId());
		
		log.info("Completed a transaction from the queue for processing: " + entry);
	}
	
}
