package lithium.service.translate.data.repositories;

import lithium.service.translate.data.entities.ChangeSet;
import lithium.service.translate.data.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChangeSetRepository extends JpaRepository<ChangeSet, Long> {
    List<ChangeSet> findByName(String name);
    ChangeSet findByLanguageAndNameAndChangeReference(Language language, String name, String changeSet);
}