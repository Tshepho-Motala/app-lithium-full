package lithium.service.domain.messagehandlers;

import lithium.service.Response;
import lithium.service.domain.data.entities.DomainRevisionLabelValue;
import lithium.service.domain.data.objects.DomainSettingsRequest;
import lithium.service.domain.services.DomainSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@Slf4j
@Service
public class DomainSettingsMessageHandler {
	@Autowired DomainSettingsService service;
	
	// Mr. Abdol wants this over a socket, so...
	// Has to be sendandreceive
	@RabbitListener(bindings=@QueueBinding(
		value = @Queue(value="service-domain-settings", durable="false"),
		exchange = @Exchange(value="service-domain-settings"),
		key = "service-domain-settings"))
	public Response<List<DomainRevisionLabelValue>> settings(DomainSettingsRequest request) {
		log.info("Settings requested for " + request.getDomainName());
		try {
			List<DomainRevisionLabelValue> settings = service.findCurrentDomainSettings(request.getDomainName());
			return Response.<List<DomainRevisionLabelValue>>builder().data(settings).status(OK).build();
		} catch (Exception e) {
			String errorMsg = "Unable to retrieve domain settings for " + request.getDomainName() + " | " + e.getMessage();
			log.error(errorMsg, e);
			return Response.<List<DomainRevisionLabelValue>>builder().status(INTERNAL_SERVER_ERROR).message(errorMsg).build();
		}
	}
}
