package lithium.service.product.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.product.data.entities.GraphicFunction;

public interface GraphicFunctionRepository extends PagingAndSortingRepository<GraphicFunction, Long> {
	@CacheEvict(value="lithium.service.product.data.entities.GraphicFunction.findByName")
	@Override
	public <S extends GraphicFunction> S save(S arg0);
	
	@Cacheable(value="lithium.service.product.data.entities.GraphicFunction.findByName", unless="#result == null")
	GraphicFunction findByName(String name);
}
