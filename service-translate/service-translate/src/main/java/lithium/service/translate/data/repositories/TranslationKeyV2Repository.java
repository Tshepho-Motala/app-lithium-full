package lithium.service.translate.data.repositories;

import lithium.service.translate.data.entities.TranslationKeyV2;
import lithium.service.translate.data.entities.TranslationValueV2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Optional;

public interface
TranslationKeyV2Repository extends PagingAndSortingRepository<TranslationKeyV2, Long>, JpaSpecificationExecutor<TranslationKeyV2>  {

    Optional<TranslationKeyV2> findByCode(String code);

    Optional<TranslationKeyV2> findById(Long id);

    @Override
    @Caching(evict = {
            @CacheEvict(value = "lithium.service.translate.services.translate2", allEntries = true), //Gets erased here, but the object type stored is the client version of the key value string
            @CacheEvict(value = "lithium.service.translate.services.translationsV2.json", allEntries = true)
    })
    <S extends TranslationKeyV2> S save(S arg0);

    @Override
    @Caching(evict = {
            @CacheEvict(value = "lithium.service.translate.services.translate2", allEntries = true), //Gets erased here, but the object type stored is the client version of the key value string
            @CacheEvict(value = "lithium.service.translate.services.translationsV2.json", allEntries = true)
    })
    void delete(TranslationKeyV2 translationKeyV2);

    default TranslationKeyV2 findOne(Long id) {
        return findById(id).orElse(null);
    }
}
