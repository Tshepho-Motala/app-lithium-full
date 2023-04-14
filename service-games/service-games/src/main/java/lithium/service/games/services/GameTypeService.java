package lithium.service.games.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.games.data.entities.Domain;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameType;
import lithium.service.games.data.entities.GameTypeEnum;
import lithium.service.games.data.repositories.DomainRepository;
import lithium.service.games.data.repositories.GameTypeRepository;
import lithium.service.games.data.specifications.GameTypeSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class GameTypeService {

    @Autowired private ChangeLogService changeLogService;
    @Autowired private DomainRepository domainRepository;
    @Autowired private GameTypeRepository repository;
    @Autowired private GameService gameService;

    public GameType findOne(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<GameType> findByDomain(String domainName) {
        return repository.findByDomainNameAndDeletedFalse(domainName);
    }

    public List<GameType> findByDomainAndType(String domainName, GameTypeEnum type) {
        return repository.findByDomainNameAndTypeAndDeletedFalse(domainName, type);
    }

    public Page<GameType> findByDomain(String domainName, Boolean deleted, String searchValue, Pageable pageable) {
        Specification<GameType> spec = null;

        spec = addToSpec(domainName, spec, GameTypeSpecification::domain);
        spec = addToSpec(deleted, spec, GameTypeSpecification::deleted);
        spec = addToSpec(searchValue, spec, GameTypeSpecification::any);

        return repository.findAll(spec, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Retryable(maxAttempts = 5, backoff = @Backoff(random = true, delay = 50, maxDelay = 1000),
            exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
    public GameType add(String domainName, GameType gameType, String authorGuid)
            throws Status500InternalServerErrorException, Status400BadRequestException {
        String trace = " [domainName="+domainName+", gameType="+gameType+", authorGuid="+authorGuid+"] ";
        log.trace("gameTypeService.add " + trace);
        log.info("gameTypeService.add " + trace);
        String clType = "create";
        GameType existing = repository.findByDomainNameAndNameAndType(domainName, gameType.getName(), gameType.getType());
        if (existing != null && !existing.getDeleted()) {
            throw new Status400BadRequestException("A game type with this name already exists");
        } else if (existing != null && existing.getDeleted()) {
            // Game type with the same name, but was previously deleted. Undoing delete.
            clType = "edit";
            gameType = existing;
            gameType.setDeleted(false);
        } else {
            Domain domain = domainRepository.findOrCreateByName(domainName, Domain::new);
            gameType.setDomain(domain);
        }
        gameType = repository.save(gameType);
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(gameType, new GameType(),
                    new String[]{"domain", "name", "deleted", "type"});
            changeLogService.registerChangesWithDomain("GameType", clType, gameType.getId(), authorGuid,
                    null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, domainName);
        } catch (Exception e) {
            String msg = "Changelog registration for game type add failed";
            log.error(msg + trace + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }
        return gameType;
    }

    @Transactional(rollbackFor = Exception.class)
    @Retryable(maxAttempts = 5, backoff = @Backoff(random = true, delay = 50, maxDelay = 1000),
            exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
    public GameType updateGameType(String domainName, GameType gameType, GameType update, String authorGuid)
            throws Status500InternalServerErrorException {
        String trace = " [domainName="+domainName+", gameType="+gameType+", update="+update
                +", authorGuid="+authorGuid+"] ";
        log.trace("GameTypeService.updateGameType " + trace);
        if (update.getType() == null) {
            update.setType(gameType.getType());
        }
        if (!gameType.getType().getValue().equals(update.getType().value())) {
            Game gameUsingGameType = gameService.findFirstByGameType(gameType);
            if (gameUsingGameType != null) {
                String msg = "Cannot change type on a Game Type that is assigned to a game";
                log.error(msg + trace);
                throw new Status500InternalServerErrorException(msg);
            }
        }
        GameType gameTypeCopy = new GameType();
        copy(gameType, gameTypeCopy);
        gameType.setName(update.getName());
        gameType.setType(update.getType());
        gameType = repository.save(gameType);
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(gameType, gameTypeCopy,
                    new String[]{"name", "type"});
            changeLogService.registerChangesWithDomain("GameType", "edit", gameType.getId(), authorGuid,
                    null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, domainName);
        } catch (Exception e) {
            String msg = "Changelog registration for game type update failed";
            log.error(msg + trace + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }
        return gameType;
    }

    private void copy(GameType from, GameType to) {
        to.setName(from.getName());
        to.setType(from.getType());
        to.setDeleted(from.getDeleted());
    }

    public Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
        return changeLogService.listLimited(ChangeLogRequest.builder()
                .entityRecordId(id)
                .entities(new String[] { "GameType" })
                .page(p)
                .build()
        );
    }

    private Specification<GameType> addToSpec(final String aString, Specification<GameType> spec,
                                                   Function<String, Specification<GameType>> predicateMethod) {
        if (aString != null && !aString.isEmpty()) {
            Specification<GameType> localSpec = Specification.where(predicateMethod.apply(aString));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    private Specification<GameType> addToSpec(final Boolean aBoolean, Specification<GameType> spec,
                                                   Function<Boolean, Specification<GameType>> predicateMethod) {
        if (aBoolean != null) {
            Specification<GameType> localSpec = Specification.where(predicateMethod.apply(aBoolean));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    @Transactional(rollbackFor = Exception.class)
    @Retryable(maxAttempts = 5, backoff = @Backoff(random = true, delay = 50, maxDelay = 1000),
            exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
    public void deleteGameType(String domainName, GameType gameType, String authorGuid)
         throws Status500InternalServerErrorException {
            String trace = " [domainName="+domainName+", gameType="+gameType + ", authorGuid="+authorGuid+"] ";
            log.trace("GameTypeService.deleteGameType " + trace);

            Game gameUsingGameType = gameService.findFirstByGameType(gameType);
            if (gameUsingGameType != null) {
                String msg = "Cannot delete Game Type that is assigned to a game";
                log.error(msg + trace);
                throw new Status500InternalServerErrorException(msg);
            }

            GameType gameTypeCopy = new GameType();
            copy(gameType, gameTypeCopy);
            gameType.setDeleted(true);
            gameType = repository.save(gameType);
            try {
                List<ChangeLogFieldChange> clfc = changeLogService.copy(gameType, gameTypeCopy,
                        new String[]{"deleted"});
                changeLogService.registerChangesWithDomain("GameType", "delete", gameType.getId(), authorGuid,
                        null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, domainName);
            } catch (Exception e) {
                String msg = "Changelog registration for game type update failed";
                log.error(msg + trace + e.getMessage(), e);
                throw new Status500InternalServerErrorException(msg);
            }
    }
}
