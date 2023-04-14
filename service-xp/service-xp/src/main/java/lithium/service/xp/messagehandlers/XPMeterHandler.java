package lithium.service.xp.messagehandlers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.xp.data.entities.Scheme;
import lithium.service.xp.messagehandlers.api.IXPMeterRequest;
import lithium.service.xp.services.XPService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class XPMeterHandler {
	@Autowired XPService xpService;
	
	@RabbitListener(
		bindings = @QueueBinding(
			value = @Queue(value = "service-xp-xpmeter", durable = "false"),
			exchange = @Exchange(value = "service-xp-xpmeter"),
			key = "service-xp-xpmeter"
		)
	)
	public Response<Void> xp(IXPMeterRequest request) {
		log.info("Request: " + request.toString());
		Response<Void> response = new Response<>();
		try {
			Long pb = xpService.xp(request.domainName(), request.getPlayerGuid());
			Scheme scheme = xpService.getActiveScheme(request.domainName());
			xpService.streamPlayerXP(request.getPlayerGuid(), scheme, pb);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response.setStatus(INTERNAL_SERVER_ERROR);
			response.setMessage(e.getMessage());
		}
		log.info("Response: " + response.toString());
		return response;
	}
}
