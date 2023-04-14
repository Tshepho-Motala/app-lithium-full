package lithium.service.leader.services;

import lithium.service.leader.storage.entities.Module;
import lithium.service.leader.storage.repositories.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ModuleService {
	private Map<String, Module> cache = new ConcurrentHashMap<>(1000);

	@Autowired private ModuleRepository repository;

	@Transactional(propagation = Propagation.REQUIRED)
	public Module findForUpdate(Long id) {
		return repository.findForUpdate(id);
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 10, maxDelay = 50, random = true))
	public Module findOrCreate(String name) {
		Module m = cacheGet(name);
		if (m != null) return m;
		m = repository.findByName(name);
		if (m != null) return cachePut(m);
		m = repository.save(
				Module.builder()
						.name(name)
						.build()
		);
		return cachePut(m);
	}

	private Module cachePut(Module object) {
		cache.put(object.getName(), object);
		return object;
	}

	private Module cacheGet(String name) {
		return cache.get(name);
	}
}
