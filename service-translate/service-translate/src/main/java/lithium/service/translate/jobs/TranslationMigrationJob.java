package lithium.service.translate.jobs;

import lithium.leader.LeaderCandidate;
import lithium.metrics.TimeThisMethod;
import lithium.service.translate.data.entities.Domain;
import lithium.service.translate.data.entities.Namespace;
import lithium.service.translate.data.entities.TranslationKey;
import lithium.service.translate.data.entities.TranslationKeyV2;
import lithium.service.translate.data.entities.TranslationValue;
import lithium.service.translate.data.entities.TranslationValueV2;
import lithium.service.translate.data.repositories.DomainRepository;
import lithium.service.translate.data.repositories.LanguageRepository;
import lithium.service.translate.data.repositories.NamespaceRepository;
import lithium.service.translate.data.repositories.TranslationKeyRepository;
import lithium.service.translate.data.repositories.TranslationKeyV2Repository;
import lithium.service.translate.data.repositories.TranslationValueRepository;
import lithium.service.translate.data.repositories.TranslationValueV2Repository;
import lithium.service.translate.services.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TranslationMigrationJob {

    @Autowired
    LeaderCandidate leaderCandidate;

    @Autowired
    NamespaceRepository namespaceRepository;

    @Autowired
    TranslationKeyRepository translationKeyRepository;

    @Autowired
    TranslationValueRepository translationValueRepository;

    @Autowired
    TranslationService translationService;

    @Autowired
    LanguageRepository languageRepository;

    @Autowired
    TranslationKeyV2Repository translationKeyV2Repository;

    @Autowired
    TranslationValueV2Repository translationValueV2Repository;

    @Autowired
    DomainRepository domainRepository;

    @Scheduled(cron="${lithium.services.translate.process-translation-migration.cron}")
    @TimeThisMethod
    public void processTranslationMigrations() {

        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }
        Map<String, TranslationKey> namespacesMap = new HashMap<>();
        List<TranslationKey> translationKeys = translationKeyRepository.findAllByValuesMigratedFalse();
        translationKeys.forEach(translationKey -> {
            String namespaces = translationKey.getKeyCode();
            long namespaceId = translationKey.getNamespace().getId();
            do {
                Namespace namespace = namespaceRepository.findOne(namespaceId);
                namespaces = namespace.getCode() + "." + namespaces;
                namespaceId = namespace.getParent() == null ? -1 : namespace.getParent().getId();
            } while (namespaceId != -1);
            namespacesMap.put(namespaces, translationKey);
            log.info(namespaces);
        });
        log.debug("Found " + namespacesMap.size() + " namespaces!");

        //Stores all migrated translations under the default domain
        Domain defaultDomain = translationService.findOrCreateDomain("default");
        for (Map.Entry<String, TranslationKey> pair : namespacesMap.entrySet()) {
            String namespaces = pair.getKey();
            final TranslationKey translationKey = pair.getValue();

            //Find or Create new translationKeyV2
            TranslationKeyV2 keyV2 = translationService.findOrCreateKeyV2(namespaces);

            //Ensures that all languages are being translated for a given translation key
            List<TranslationValue> allByKey = translationValueRepository.findAllByKeyAndMigratedFalse(translationKey);
            for (TranslationValue translationValue: allByKey) {

                String value = "";
                if (translationValue.getCurrent() != null) {
                    value = translationValue.getCurrent().getValue();
                }
                if (translationValue.getDefaultValue() != null) {
                    value = translationValue.getDefaultValue().getValue();
                }
                TranslationValueV2 translationValueV2 = translationService.findOrCreateValueV2(keyV2, defaultDomain, translationValue.getLanguage(), value);

                translationValueV2Repository.save(translationValueV2);
                //Setting the translated value as migrated, so it won't be picked up on the next migration run
                translationValue.setMigrated(true);
                translationValueRepository.save(translationValue);
            }
        }
        if (translationKeys.size() > 0) {
            log.debug("Migration complete and cache cleared");
        }
    }
}