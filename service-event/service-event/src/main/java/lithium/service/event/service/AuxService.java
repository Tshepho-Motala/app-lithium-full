package lithium.service.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.event.entities.Currency;
import lithium.service.event.entities.Domain;
import lithium.service.event.entities.EventType;
import lithium.service.event.entities.User;
import lithium.service.event.repositories.CurrencyRepository;
import lithium.service.event.repositories.DomainRepository;
import lithium.service.event.repositories.EventTypeRepository;
import lithium.service.event.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuxService {
	@Autowired private DomainRepository domainRepository;
	@Autowired LithiumServiceClientFactory services;
	@Autowired private UserRepository userRepository;
	@Autowired private EventTypeRepository eventTypeRepository;
	@Autowired private CurrencyRepository currencyRepository;
	
	@Retryable
	public User findOrCreateUser(String guid) {
		User owner = userRepository.findByGuid(guid);
		if (owner == null) {
			owner = User.builder().guid(guid).build();
			owner = userRepository.save(owner);
		}
		return owner;
	}
	
	@Retryable
	public Domain findOrCreateDomain(String name) {
		Domain domain = domainRepository.findByName(name);
		if (domain == null) {
			domain = Domain.builder().name(name).build();
			domain = domainRepository.save(domain);
		}
		return domain;
	}
	
	@Retryable
	public EventType findOrCreateEventType(String code) {
		EventType eventType = eventTypeRepository.findByCode(code);
		if (eventType == null) {
			eventType = EventType.builder().code(code).build();
			eventType = eventTypeRepository.save(eventType);
		}
		return eventType;
	}
	
	@Retryable
	public Currency findOrCreateCurrency(String code) {
		Currency currency = currencyRepository.findByCode(code);
		if (currency == null) {
			currency = Currency.builder().code(code).build();
			currency = currencyRepository.save(currency);
		}
		return currency;
	}
}