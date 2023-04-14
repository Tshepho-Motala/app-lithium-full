package lithium.service.translate.data.repositories;

import lithium.service.translate.data.entities.Domain;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.entities.TranslationKeyV2;
import lithium.service.translate.data.entities.TranslationValueV2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TranslationValueV2Repository extends PagingAndSortingRepository<TranslationValueV2, Long>, JpaSpecificationExecutor<TranslationValueV2> {
    Optional<TranslationValueV2> findTopByDomainNameAndKeyCodeAndAndLanguage(String domainName, String code, Language language);
    Optional<TranslationValueV2> findByDomainAndKeyAndLanguage(Domain domain, TranslationKeyV2 keyV2, Language language);
    Optional<TranslationValueV2> findById(Long id);
    Optional<TranslationValueV2> findByKey_id(Long id);
    @Query(nativeQuery = true, value = "SELECT d.name as 'domain_name', l.locale2 as 'language', tkey.code as 'key', tvalue.value as 'value' FROM translation_key_v2 tkey INNER JOIN translation_value_v2 tvalue ON tvalue.key_id = tkey.id INNER JOIN domain d ON tvalue.domain_id = d.id INNER JOIN language l ON tvalue.language_id = l.id WHERE tkey.code like :code and d.name = :domainName and l.locale2 = :locale2")
    List<Object> findTranslations(@Param("domainName") String domainName, @Param("locale2") String locale2, @Param("code") String code);

    @Query(nativeQuery = true, value = "SELECT d.name as 'domain_name', l.locale2 as 'language', tkey.code as 'key', tvalue.value as 'value', tvalue.last_updated FROM translation_key_v2 tkey INNER JOIN translation_value_v2 tvalue ON tvalue.key_id = tkey.id INNER JOIN domain d ON tvalue.domain_id = d.id INNER JOIN language l ON tvalue.language_id = l.id WHERE tkey.code like :code and d.name in (:domainNames) and l.locale2 in (:locale2s) and tvalue.last_updated >= :lastUpdated")
    List<Object> findTranslationsLastUpdatedSince(@Param("domainNames") String[] domainNames, @Param("locale2s") String[] locale2s, @Param("code") String code, @Param("lastUpdated") Date lastUpdated);

    @Override
    @Caching(evict = {
            @CacheEvict(value = "lithium.service.translate.services.translate2", allEntries = true), //Gets erased here, but the object type stored is the client version of the key value string
            @CacheEvict(value = "lithium.service.translate.services.translationsV2.json", allEntries = true)
    })
    <S extends TranslationValueV2> S save(S arg0);

    @Override
    @Caching(evict = {
            @CacheEvict(value = "lithium.service.translate.services.translate2", allEntries = true), //Gets erased here, but the object type stored is the client version of the key value string
            @CacheEvict(value = "lithium.service.translate.services.translationsV2.json", allEntries = true)
    })
    void delete(TranslationValueV2 translationValueV2);
}
