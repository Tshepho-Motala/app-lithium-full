package lithium.service.accounting.provider.internal.services;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.repositories.DomainRepository;

@Service
public class DomainService {

	// Maintain a near-cache of domains in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	ConcurrentHashMap<String, Domain> cache = new ConcurrentHashMap<>(1000);

	@Autowired
	private DomainRepository domainRepository;
	
	public Domain findOrCreate(String name) {
		Domain domain = cacheGet(name);
		if (domain != null) return domain;
		domain = domainRepository.findByName(name);
		if (domain != null) return cachePut(domain);
		domain = Domain.builder().name(name).build();
		domainRepository.save(domain);
		return cachePut(domain);
	}

	public List<Domain> findAll() {
		return domainRepository.findAll();
	}
	
	private Domain cachePut(Domain object) {
		String cacheKey = object.getName(); 
		cache.put(cacheKey, object);
		return object;
	}
	
	private Domain cacheGet(String code) {
		String cacheKey = code;
		return cache.get(cacheKey);
	}

}