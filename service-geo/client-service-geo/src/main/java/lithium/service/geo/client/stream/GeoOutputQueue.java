package lithium.service.geo.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface GeoOutputQueue {

	@Output("geooutput")
	public MessageChannel outputQueue();
	
}
