/**
 * The cache in this class is not evicted,as a cache record of domain-game tuple prevents us from making excessive downstream calls for creating games on the fly.
 *  Thus we need those tuples for performance optimisations. It's also ideal to store them in the cache forever as to prevent excessive checks in the local database
 * */

package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.service.casino.provider.roxor.storage.entities.DomainGame;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DomainGameRepository extends JpaRepository<DomainGame, Long> {

    @Cacheable(value = "lithium.service.casino.provider.roxor.storage.entities.domaingame.byDomainNameAndGameKey", unless = "#result == null")
    Optional<DomainGame> findByDomainNameAndGameKey(String domainName, String gameKey);

    default DomainGame findOrCreateByDomainNameAndGameKey(String domainName, String gameKey) {

        Optional<DomainGame> domainGame = findByDomainNameAndGameKey(domainName, gameKey);

        if (!domainGame.isPresent()) {
            DomainGame domainGameToSave = new DomainGame();
            domainGameToSave.setDomainName(domainName);
            domainGameToSave.setGameKey(gameKey);
            save(domainGameToSave);
            return domainGameToSave;
        }

        return domainGame.get();
    }
}
