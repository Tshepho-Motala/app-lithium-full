package lithium.service.settlement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.settlement.data.entities.Entity;
import lithium.service.settlement.data.repositories.EntityRepository;

@Service
public class EntityService {
	@Autowired
	private EntityRepository repository;
	
	@Retryable
	public Entity findOrCreate(String uuid) {
		Entity entity = repository.findByUuid(uuid);
		if (entity == null) {
			entity = Entity.builder().uuid(uuid).build();
			repository.save(entity);
		}
		return entity;
	}
	
	public Entity save(Entity entity) {
		return repository.save(entity);
	}
}
