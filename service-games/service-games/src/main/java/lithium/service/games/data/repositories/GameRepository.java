package lithium.service.games.data.repositories;

import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameStudio;
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.data.entities.GameType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface GameRepository extends PagingAndSortingRepository<Game, Long>, JpaSpecificationExecutor<Game> {
	Game findById(long id);
	Game findByGuidAndDomainName(String guid, String domainName);
	List<Game> findAllByDomainNameAndEnabledTrue(String domainName);
	List<Game> findAllByDomainName(String domainName);
	List<Game> findAllByDomainNameAndFreeGameTrue(String domainName);
	List<Game> findBySupplierGameGuidNotNullAndDomainName(String domainName);
	Game findFirstByPrimaryGameTypeOrSecondaryGameType(GameType primaryGameType, GameType secondaryGameType);
	List<Game> findAllByPrimaryGameType(GameType gameType);
	List<Game> findAllBySecondaryGameType(GameType gameType);
	List<Game> findAllByGuidInAndDomainId(Set<String> guids, Long domainId);
	Game findFirstByGameStudio(GameStudio gameStudio);
	List<Game> findAllByDomainNameAndProviderGuidAndEnabledTrue(String domainName, String providerGuid);
	List<Game> findByGuidNotAndSupplierGameGuidAndDomainNameAndEnabledTrue(String guid, String supplierGameGuid, String domainName);

	@Override
	@CacheEvict(cacheNames={"lithium.service.games.services.getEffectiveLabels",
	"lithium.service.games.services.getResponseGame", "lithium.service.games.services.getDomainGameList"}, allEntries = true)
	<S extends Game> S save(S entity);

	@Query(
		"SELECT game FROM Game game " +
		"INNER JOIN game.domain d " +
		"WHERE d.name = :domainName " +
		"AND game.id IN (SELECT glv.gameId " +
		"           FROM GameLabelValue glv  " +
		"           INNER JOIN glv.labelValue lv " +
		"           INNER JOIN lv.label l " +
		"           WHERE UPPER(l.name) = UPPER(:labelName) " +
		"           AND lv.value IN (:labelValues)  AND glv.deleted = :gameLabelValuesDeleted )")
	List<Game> findDomainGamesForLabelWithLabelValues(@Param("domainName") String domainName, @Param("labelName") String labelName,
		@Param("labelValues") List<String> labelValues,@Param("gameLabelValuesDeleted")boolean gameLabelValuesDeleted);

	default Page<Game> findAllBy(Specification<Game> spec, Pageable p) {

		if (p.getSort() == null) {
			// Default sort order
			return findAll(spec, PageRequest.of(p.getPageNumber(), p.getPageSize(), Sort.Direction.DESC, "id"));
		}

		Sort.Order order = p.getSort().iterator().next();
		String property = order.getProperty();
		Sort.Direction direction = order.getDirection();

		if (property.equalsIgnoreCase("id")) {
			property = "id";
		} else if (property.equalsIgnoreCase("freeGame")) {
			property = "freeGame";
		} else if (property.equalsIgnoreCase("visible")) {
			property = "visible";
		} else if (property.equalsIgnoreCase("progressiveJackpot")) {
			property = "progressiveJackpot";
		} else if (property.equalsIgnoreCase("localJackpotPool")) {
			property = "localJackpotPool";
		} else if (property.equalsIgnoreCase("gameSupplier")) {
			property = "gameSupplier";
		}

		Page<Game> games = findAll(spec, PageRequest.of(p.getPageNumber(), p.getPageSize(), direction, property));
		return games;
	}


	default Game findOne(Long id) {
		return findById(id).orElse(null);
	}

}
