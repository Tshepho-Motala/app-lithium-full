package lithium.service.gateway.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.gateway.client.response.GatewayExchangeResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GatewayExchangeStream {
	@Autowired
	private GatewayExchangeStreamOutputQueue channel;
	
	public void process(GatewayExchangeResponse response) throws JsonProcessingException {
		try {
			channel.channel().send(MessageBuilder.<String>withPayload((new ObjectMapper()).writeValueAsString(response)).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
	
	public void process(String target, String event, String jsonPayloadString) throws JsonProcessingException {
		try {
			process(GatewayExchangeResponse.builder()
					.target(target)
					.event(event)
					.data(jsonPayloadString)
					.build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}