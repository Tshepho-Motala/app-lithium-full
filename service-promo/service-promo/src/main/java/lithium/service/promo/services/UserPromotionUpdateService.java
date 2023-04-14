package lithium.service.promo.services;

import lithium.service.promo.data.entities.UserPromotion;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.promo.data.repositories.UserPromotionRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserPromotionUpdateService {
	@Autowired GatewayExchangeStream gatewayExchangeStream;
	@Autowired
	UserPromotionRepository repository;
	
	@Async
	public void stream(Long userMissionId) {
		UserPromotion userPromotion = userMissionUpdate(userMissionId);
		log.debug("Sending update request for user: "+ userPromotion.getUser().getGuid());
		final ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(userPromotion);
			log.debug("json: "+json);
			gatewayExchangeStream.process("playerroom/"+ userPromotion.getUser().getGuid(), "usermissionupdate", json);
		} catch (Exception e) {
			log.error("Error streaming new content to playerroom/"+ userPromotion.getUser().getGuid(), e);
		}
	}
	
	@RabbitListener(
		bindings = @QueueBinding(
			value = @Queue(
				value = "usermissionupdate",
				durable = "false"
			),
			exchange = @Exchange(
				value = "usermissionupdate"
			),
			key = "usermissionupdate"
		),
		group="usermissionupdate"
	)
	public UserPromotion userMissionUpdate(Long userMissionId) {
		log.debug("UserPromotion update request: "+userMissionId);
		UserPromotion userPromotion = repository.findOne(userMissionId);
		log.debug("Sending UserPromotion: "+ userPromotion);
		return userPromotion;
	}
}
