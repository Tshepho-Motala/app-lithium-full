package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.GameType;
import lithium.service.games.data.entities.GameTypeEnum;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GameTypeRepository extends PagingAndSortingRepository<GameType, Long>, JpaSpecificationExecutor<GameType> {

    List<GameType> findByDomainNameAndDeletedFalse(String domainName);

    List<GameType> findByDomainNameAndTypeAndDeletedFalse(String domainName, GameTypeEnum type);

    GameType findByDomainNameAndName(String domainName, String typeName);

    GameType findByDomainNameAndNameAndType(String domainName, String typeName, GameTypeEnum type);
}
