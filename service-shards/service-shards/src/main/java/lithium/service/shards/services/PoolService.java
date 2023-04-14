package lithium.service.shards.services;

import lithium.service.shards.storage.entities.Pool;
import lithium.service.shards.storage.repositories.PoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PoolService {
	private Map<String, Pool> cache = new ConcurrentHashMap<>();

	@Autowired private PoolRepository repository;

	@Transactional(propagation = Propagation.REQUIRED)
	public Pool findForUpdate(Long id) {
		return repository.findForUpdate(id);
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 10, maxDelay = 50, random = true))
	public Pool findOrCreate(String name) {
		Pool p = cacheGet(name);
		if (p != null) return p;
		p = repository.findByName(name);
		if (p != null) return cachePut(p);
		p = repository.save(
				Pool.builder()
						.name(name)
						.build()
		);
		return cachePut(p);
	}

	private Pool cachePut(Pool object) {
		String cacheKey = object.getName();
		cache.put(cacheKey, object);
		return object;
	}

	private Pool cacheGet(String accountCode) {
		String cacheKey = accountCode;
		return cache.get(cacheKey);
	}
}
