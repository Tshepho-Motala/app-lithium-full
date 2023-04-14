package lithium.service.translate.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.translate.data.entities.ChangeSet;
import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.repositories.ChangeSetRepository;
import lithium.service.translate.data.repositories.LanguageRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ChangeSetService {

    @Autowired ChangeSetRepository changeSetRepository;
    @Autowired LanguageRepository languageRepository;
    @Autowired ModelMapper mapper;

    public List<lithium.service.translate.client.objects.ChangeSet> getChangeSets(String name) {

        List<ChangeSet> byName = changeSetRepository.findByName(name);
        List<lithium.service.translate.client.objects.ChangeSet> changeSets = new ArrayList<>();
        for (ChangeSet changeSet : byName) {
            changeSets.add(lithium.service.translate.client.objects.ChangeSet.builder()
                    .lang(changeSet.getLanguage().getLocale2())
                    .name(changeSet.getName())
                    .changeReference(changeSet.getChangeReference())
                    .checksum(changeSet.getChecksum())
                    .lastUpdated(changeSet.getLastUpdated())
                    .build());
        }
        return changeSets;
    }

    public void registerNewChangeSet(String locale2, String name, String changeReference, String checksum) {
        Language language = languageRepository.findByLocale2(locale2);
        ChangeSet changeSet = changeSetRepository.findByLanguageAndNameAndChangeReference(language, name,
                changeReference);
        if (changeSet == null) {
            changeSet = ChangeSet.builder()
                    .applyDate(new Date())
                    .language(language)
                    .name(name)
                    .changeReference(changeReference)
                    .checksum(checksum)
                    .lastUpdated(new Date())
                    .build();
        } else {
            changeSet.setChecksum(checksum);
            changeSet.setLastUpdated(new Date());
        }
        changeSetRepository.save(changeSet);
    }

    public void removeChangeSet(String locale2, String name, String changeReference) {
        Language language = languageRepository.findByLocale2(locale2);
        ChangeSet reference = changeSetRepository.findByLanguageAndNameAndChangeReference(language, name, changeReference);
        if (reference != null)
            changeSetRepository.delete(reference);
    }

    public void removeAllChangeSetsByName(String name) {
        List<ChangeSet> byName = changeSetRepository.findByName(name);
        for (ChangeSet changeSet : byName) {
            changeSetRepository.delete(changeSet);
        }
    }
}