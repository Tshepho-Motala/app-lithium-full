package lithium.service.user.mass.action.data.repositories;

import lithium.service.user.mass.action.data.entities.Action;
import lithium.service.user.mass.action.data.entities.ActionType;
import lithium.service.user.mass.action.data.entities.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ActionRepository extends JpaRepository<Action, Long> {

    List<Action> findAllByMassActionMeta(FileMeta fileMeta);
    Optional<Action> findActionByMassActionMetaAndName(FileMeta fileMeta, ActionType name);
    Optional<Action> findFirstByName(ActionType name);
}