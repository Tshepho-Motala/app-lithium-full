package lithium.service.raf.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.raf.client.objects.RAFConversionRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RAFConversionStream {
	@Autowired RAFConversionStreamOutputQueue channel;
	
	public void process(RAFConversionRequest request) {
		try {
			channel.channel().send(MessageBuilder.<RAFConversionRequest>withPayload(request).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}