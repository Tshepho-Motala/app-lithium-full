package lithium.service.user.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.UserApiToken;

public interface UserApiTokenRepository extends PagingAndSortingRepository<UserApiToken, Long> {
	
	@Cacheable(cacheNames="lithium.service.user.data.entities.UserApiToken", key="#root.args[0]", unless="#result == null")
	public UserApiToken findByGuid(String guid);
	
	//TODO: Will need to have historical token cleanup in schedule or something. We have providers requiring tokens that expite for users.
	// So thinking about keeping a token history for a limited time or maybe having a historical table the cache will only be kept for latest tokens
	@Cacheable(cacheNames="lithium.service.user.data.entities.UserApiToken.token", key="#root.args[0]", unless="#result == null")
	public UserApiToken findByToken(String token);	
	
	@Override
	@CacheEvict(cacheNames="lithium.service.user.data.entities.UserApiToken", key="#result.guid")
	<S extends UserApiToken> S save(S entity);

	UserApiToken findByShortGuid(String shortGuid);
}
