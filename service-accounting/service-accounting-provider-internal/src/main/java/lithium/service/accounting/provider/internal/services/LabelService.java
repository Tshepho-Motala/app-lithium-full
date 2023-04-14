package lithium.service.accounting.provider.internal.services;

import lithium.service.accounting.provider.internal.data.entities.Label;
import lithium.service.accounting.provider.internal.data.repositories.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LabelService {
	// Maintain a near-cache of account codes in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.
	private ConcurrentHashMap<String, Label> cache = new ConcurrentHashMap<>(1000);
	
	@Autowired private LabelRepository repository;

	@Retryable
	public Label findOrCreate(String name) {
		Label l = cacheGet(name);
		if (l != null) return l;
		l = repository.findByName(name);
		if (l != null) return cachePut(l);
		l = repository.save(
				Label.builder()
						.name(name)
						.build()
		);
		return cachePut(l);
	}

	private Label cachePut(Label object) {
		String cacheKey = object.getName();
		cache.put(cacheKey, object);
		return object;
	}

	private Label cacheGet(String name) {
		String cacheKey = name;
		return cache.get(cacheKey);
	}
}
