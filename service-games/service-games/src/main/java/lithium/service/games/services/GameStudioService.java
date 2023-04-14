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
import lithium.service.games.data.entities.GameStudio;
import lithium.service.games.data.repositories.DomainRepository;
import lithium.service.games.data.repositories.GameStudioRepository;
import lithium.service.games.data.specifications.GameStudioSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class GameStudioService {
    @Autowired
    private ChangeLogService changeLogService;
    @Autowired private DomainRepository domainRepository;
    @Autowired private GameStudioRepository repository;
    @Autowired private GameService gameService;

    public GameStudio findOne(Long id) {
        return repository.findById(id).orElse(null);
    }

    public GameStudio findByDomainAndStudioName(String domainName, String gameStudioName) {
        return repository.findByDomainNameAndName(domainName, gameStudioName);
    }

    public List<GameStudio> findByDomain(String domainName) {
        return repository.findByDomainNameAndDeletedFalse(domainName);
    }

    public Page<GameStudio> findByDomain(String domainName, Boolean deleted, String searchValue, Pageable pageable) {
        Specification<GameStudio> spec = null;

        spec = addToSpec(domainName, spec, GameStudioSpecifications::domain);
        spec = addToSpec(deleted, spec, GameStudioSpecifications::deleted);
        spec = addToSpec(searchValue, spec, GameStudioSpecifications::any);

        return repository.findAll(spec, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Retryable(maxAttempts = 5, backoff = @Backoff(random = true, delay = 50, maxDelay = 1000),
            exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
    public GameStudio addGameStudio(String domainName, GameStudio gameStudio, String authorGuid)
            throws Status500InternalServerErrorException, Status400BadRequestException {
        String trace = " [domainName="+domainName+", gameStudio="+gameStudio+", authorGuid="+authorGuid+"] ";
        log.trace("GameStudioService.add " + trace);
        String clType = "create";
        GameStudio existing = repository.findByDomainNameAndName(domainName, gameStudio.getName());
        if (existing != null && !existing.getDeleted()) {
            throw new Status400BadRequestException("A game studio with this name already exists");
        } else if (existing != null && existing.getDeleted()) {
            // Game studio with the same name, but was previously deleted. Undoing delete.
            clType = "edit";
            gameStudio = existing;
            gameStudio.setDeleted(false);
        } else {
            Domain domain = domainRepository.findOrCreateByName(domainName, Domain::new);
            gameStudio.setDomain(domain);
        }
        gameStudio = repository.save(gameStudio);
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(gameStudio, new GameStudio(),
                    new String[]{"domain", "name", "deleted"});
            changeLogService.registerChangesWithDomain("GameStudio", clType, gameStudio.getId(), authorGuid,
                    null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, domainName);
        } catch (Exception e) {
            String msg = "Changelog registration for game studio add failed";
            log.error(msg + trace + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }
        return gameStudio;
    }

    @Transactional(rollbackFor = Exception.class)
    @Retryable(maxAttempts = 5, backoff = @Backoff(random = true, delay = 50, maxDelay = 1000),
            exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
    public GameStudio updateGameStudio(String domainName, GameStudio gameStudio, GameStudio update, String authorGuid)
            throws Status500InternalServerErrorException {
        String trace = " [domainName="+domainName+", gameStudio="+gameStudio+", update="+update
                +", authorGuid="+authorGuid+"] ";
        log.trace("GameStudioService.update " + trace);
        GameStudio gameStudioCopy = new GameStudio();
        copy(gameStudio, gameStudioCopy);
        gameStudio.setName(update.getName());
        gameStudio.setDeleted(update.getDeleted());
        gameStudio = repository.save(gameStudio);
        try {
            boolean deleted = gameStudio.getDeleted().booleanValue();
            String type = ((deleted != gameStudioCopy.getDeleted().booleanValue()) && (deleted))
                    ? "delete"
                    : "edit";
            List<ChangeLogFieldChange> clfc = changeLogService.copy(gameStudio, gameStudioCopy,
                    new String[]{"name", "deleted"});
            changeLogService.registerChangesWithDomain("GameStudio", type, gameStudio.getId(), authorGuid,
                    null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, domainName);
        } catch (Exception e) {
            String msg = "Changelog registration for game studio update failed";
            log.error(msg + trace + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }
        return gameStudio;
    }

    private void copy(GameStudio from, GameStudio to) {
        to.setName(from.getName());
        to.setDeleted(from.getDeleted());
    }

    public Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
        return changeLogService.listLimited(ChangeLogRequest.builder()
                .entityRecordId(id)
                .entities(new String[] { "GameStudio" })
                .page(p)
                .build()
        );
    }

    private Specification<GameStudio> addToSpec(final String aString, Specification<GameStudio> spec,
                                                   Function<String, Specification<GameStudio>> predicateMethod) {
        if (aString != null && !aString.isEmpty()) {
            Specification<GameStudio> localSpec = Specification.where(predicateMethod.apply(aString));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    private Specification<GameStudio> addToSpec(final Boolean aBoolean, Specification<GameStudio> spec,
                                                   Function<Boolean, Specification<GameStudio>> predicateMethod) {
        if (aBoolean != null) {
            Specification<GameStudio> localSpec = Specification.where(predicateMethod.apply(aBoolean));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    @Transactional(rollbackFor = Exception.class)
    @Retryable(maxAttempts = 5, backoff = @Backoff(random = true, delay = 50, maxDelay = 1000),
            exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
    public void deleteGameStudio(String domainName, GameStudio gameStudio, String authorGuid)
            throws Status500InternalServerErrorException {
        String trace = " [domainName="+domainName+", gameStudio="+gameStudio + ", authorGuid="+authorGuid+"] ";
        log.trace("GameStudioService.deleteGameStudio " + trace);

        Game gameUsingGameStudio = gameService.findFirstByGameStudio(gameStudio);
        if (gameUsingGameStudio != null) {
            String msg = "Cannot delete Game Studio that is assigned to a game";
            log.error(msg + trace);
            throw new Status500InternalServerErrorException(msg);
        }

        GameStudio gameStudioCopy = new GameStudio();
        copy(gameStudio, gameStudioCopy);
        gameStudio.setDeleted(true);
        gameStudio = repository.save(gameStudio);
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(gameStudio, gameStudioCopy,
                    new String[]{"deleted"});
            changeLogService.registerChangesWithDomain("GameStudio", "delete", gameStudio.getId(), authorGuid,
                    null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, domainName);
        } catch (Exception e) {
            String msg = "Changelog registration for game studio update failed";
            log.error(msg + trace + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }
    }
}
