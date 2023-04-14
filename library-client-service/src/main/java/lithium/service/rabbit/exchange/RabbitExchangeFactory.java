package lithium.service.rabbit.exchange;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix="lithium.service.rabbit.exchange")
@Slf4j
public class RabbitExchangeFactory {
	@Getter
	private List<String> fanoutExchangeList = new ArrayList<>();
	@Getter
	private List<String> delayedExchangeList = new ArrayList<>();
	
	@Bean
	public List<FanoutExchange> fanoutExchangeList() {
		List<FanoutExchange> exchangeInstances = new ArrayList<FanoutExchange>();
		log.info("Rabbit fanout exchange creation: " + fanoutExchangeList.toString());
		for (String exchangeName: fanoutExchangeList) {
			try {
				FanoutExchange exchange = new FanoutExchange(exchangeName, true, false);
				exchangeInstances.add(exchange);
			} catch (Exception ex) {
				log.error("Unable to create fanout exchange: " + exchangeName, ex);
			}
		}
		return exchangeInstances;
	}

	@Bean
	public List<CustomExchange> delayedExchangeList() {
		List<CustomExchange> delayed = new ArrayList<CustomExchange>();
		log.info("Rabbit delayed exchange creation: " + delayedExchangeList.toString());
		for (String exchangeName: delayedExchangeList) {
			try {
				Map<String, Object> args = new HashMap<>();
				args.put("x-delayed-type", "direct");
				CustomExchange exchange = new CustomExchange(exchangeName,"x-delayed-message", true, false, args);
				delayed.add(exchange);
			} catch (Exception ex) {
				log.error("Unable to create delayed exchange: " + exchangeName, ex);
			}
		}
		return delayed;
	}
}
