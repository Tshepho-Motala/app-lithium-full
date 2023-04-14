package lithium.service.access;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.access.client.AccessService;
import lithium.service.access.client.AccessService.ListType;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.data.repositories.AccessRuleStatusOptionsRepository;
import lithium.service.access.data.repositories.ListTypeRepository;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@LithiumService
@EnableLeaderCandidate
@EnableDomainClient
@EnableProviderClient
@EnableChangeLogService
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
public class ServiceAccessApplication extends LithiumServiceApplication {
  @Autowired ListTypeRepository listTypeRepository;
	@Autowired AccessRuleStatusOptionsRepository accessRuleStatusOptionsRepository;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAccessApplication.class, args);
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
		// Add default status options
		accessRuleStatusOptionsRepository.findOrCreate(EAuthorizationOutcome.ACCEPT.name(), true, true);
		accessRuleStatusOptionsRepository.findOrCreate(EAuthorizationOutcome.REJECT.name(), true, true);
		accessRuleStatusOptionsRepository.findOrCreate(EAuthorizationOutcome.REVIEW.name(), false, true);
		accessRuleStatusOptionsRepository.findOrCreate(EAuthorizationOutcome.TIMEOUT.name(), false, true);

		//default list types
    for (ListType listType:AccessService.ListType.values()) {
      log.info("Checking ListType: "+listType);
      listTypeRepository.findOrCreate(listType.type(), listType.displayName(), listType.description());
    }
  }

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

  public static enum ConfigProperties {
    HASH_PASSWORD("HashPassword");

    @Getter
    private final String name;

    ConfigProperties(String valueParam) {
      name = valueParam;
    }
  }

  @Bean
  public ObjectMapper mapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }
}
