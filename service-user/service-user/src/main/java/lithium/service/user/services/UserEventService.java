package lithium.service.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.user.client.objects.AutoRegistration;
import lithium.service.user.client.objects.UserEventBasic;
import lithium.service.user.client.stream.UserEventsStream;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserEvent;
import lithium.service.user.data.entities.UserEventProjection;
import lithium.service.user.data.repositories.UserEventRepository;
import lithium.service.user.messagehandlers.objects.UserEventRequest;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserEventService {
	@Autowired UserEventRepository userEventRepository;
	@Autowired ServiceUserConfigurationProperties properties;
	@Autowired UserService userService;
	@Autowired UserEventsStream userEventsStream;
	@Autowired GatewayExchangeStream gatewayExchangeStream;
	@Autowired ObjectMapper mapper;

	public void streamToGatewayExchange(String playerGuid, lithium.service.user.client.objects.UserEvent userEvent) {
		log.info("sending userevent for user: "+playerGuid);
		try {
			String json = mapper.writeValueAsString(userEvent);
			log.debug("streamToGatewayExchange json : "+json);
			gatewayExchangeStream.process("playerroom/"+playerGuid, "userevent", json);
		} catch (Exception e) {
			log.error("Error streaming new content to playerroom/"+playerGuid, e);
		}
	}
	
	@RabbitListener(
		bindings = @QueueBinding(
			value = @Queue(
				value = "userevent",
				durable = "false"
			),
			exchange = @Exchange(
				value = "userevent"
			),
			key = "userevent"
		),
		group="userevent"
	)
	public lithium.service.user.client.objects.UserEvent userEvent(UserEventRequest request) {
		log.debug("UserEvent [request="+request+"]");
		return new lithium.service.user.client.objects.UserEvent();
	}

	public void streamUserEvent(String guid, Long userEventId, String type, String message, String data) {
		lithium.service.user.client.objects.User user = userService.findAndConvert(guid);
		user.clearPassword();
		
		lithium.service.user.client.objects.UserEvent userEvent = lithium.service.user.client.objects.UserEvent.builder()
		.id(userEventId)
		.type(type)
		.message(message)
		.data(data)
		.user(user)
		.build();
		
		userEventsStream.processUserEvent(userEvent);
		
		streamToGatewayExchange(user.guid(), userEvent);
	}

	private void streamUserEvent(User user, Long userEventId, String type, String message, String data) {
		lithium.service.user.client.objects.User userObject = userService.convert(user);
		userObject.clearPassword();

		lithium.service.user.client.objects.UserEvent userEvent = lithium.service.user.client.objects.UserEvent.builder()
			.id(userEventId)
			.type(type)
			.message(message)
			.data(data)
			.user(userObject)
			.build();

		userEventsStream.processUserEvent(userEvent);

		streamToGatewayExchange(user.guid(), userEvent);
	}
	
	public UserEvent registerEvent(String guid, UserEventBasic userEventBasic) {
		User user = userService.findFromGuid(guid);

		UserEvent userEvent = UserEvent.builder()
			.user(user)
			.type(userEventBasic.getType())
			.message(userEventBasic.getMessage())
			.data(userEventBasic.getData())
			.createdOn(new Date())
			.received(false)
			.build();
		
		userEvent = userEventRepository.save(userEvent);
		
		streamUserEvent(user, userEvent.getId(), userEvent.getType(), userEvent.getMessage(), userEvent.getData());
		
		return userEvent;
	}
	
	public List<UserEventProjection> getEvents(String domainName, String userName, String type) {
		User user = userService.get(domainName, userName);
		
		List<UserEventProjection> userEvents =
			(type != null)
				?userEventRepository.findByUserAndReceivedFalseAndTypeIgnoreCase(user, type)
				:userEventRepository.findByUserAndReceivedFalse(user);
		
		Iterator<UserEventProjection> iterator = userEvents.iterator();
		while (iterator.hasNext()) {
			UserEventProjection e = iterator.next();
			if (isExpired(e.getCreatedOn(), new Date())) {
				iterator.remove();
				break;
			}
		}
		
		return userEvents;
	}
	
	public UserEvent markReceived(String domainName, String userName, Long id) throws Exception {
		UserEvent userEvent = userEvent(domainName, userName, id);

		if (userEvent.getReceived())
			throw new Exception("UserEvent with id (" + id + ") is already marked as received");
		
		userEvent.setReceived(true);
		userEvent = userEventRepository.save(userEvent);
		
		userEvent.getUser().clearPassword();

		return userEvent;
	}

	public UserEvent getUserEvent(String domainName, String userName, Long id) throws Exception {
		UserEvent userEvent = userEvent(domainName, userName, id);
		userEvent.getUser().clearPassword();
		return userEvent;
	}

	private UserEvent userEvent(String domainName, String userName, Long id) {
		User user = userService.get(domainName, userName);

		UserEvent userEvent = userEventRepository.findOne(id);
		if (userEvent == null)
			throw new IllegalArgumentException("UserEvent with id (" + id + ") does not exist");

		if (!userEvent.getUser().getId().equals(user.getId()))
			throw new IllegalArgumentException("UserEvent with id (" + id + ") is not registered to User with id (" + user.getId() + ")");

		return userEvent;
	}
	
	private Boolean isExpired(Date first, Date second) {
		Minutes minutes = Minutes.minutesBetween(new DateTime(first), new DateTime(second));
		return minutes.getMinutes() >= properties.getUserEvent().getKeepAlive();
	}

  public void streamUserRegistrationSuccessEvent(Long userId, String ipAddress, String userAgent, String deviceId, String password) {
	  streamUserRegistrationSuccessEvent(userId, ipAddress, userAgent, deviceId, password, false);
  }

  public void streamUserRegistrationSuccessEvent(Long userId, String ipAddress, String userAgent, String deviceId, String password, boolean channelOptOut) {
    userEventsStream.processUserRegistrationSuccessEvent(
        AutoRegistration.builder()
            .userId(userId)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .deviceId(deviceId)
            .password(password)
            .channelOptOut(channelOptOut)
            .build());
  }
}
