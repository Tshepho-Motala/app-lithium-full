package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.GameStudio;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GameStudioRepository extends PagingAndSortingRepository<GameStudio, Long>, JpaSpecificationExecutor<GameStudio> {
    GameStudio findByDomainNameAndName(String domainName, String name);
    List<GameStudio> findByDomainNameAndDeletedFalse(String domainName);
}
