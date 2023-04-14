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
import lithium.service.games.data.entities.GameSupplier;
import lithium.service.games.data.repositories.DomainRepository;
import lithium.service.games.data.repositories.GameSupplierRepository;
import lithium.service.games.data.specifications.GameSupplierSpecifications;
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
public class GameSupplierService {
	@Autowired private ChangeLogService changeLogService;
	@Autowired private DomainRepository domainRepository;
	@Autowired private GameSupplierRepository repository;

	public GameSupplier findOne(Long id) {
		return repository.findOne(id);
	}

	public GameSupplier findByDomainAndSupplierName(String domainName, String supplierNamae) {
		return repository.findByDomainNameAndName(domainName, supplierNamae);
	}

	public List<GameSupplier> findByDomain(String domainName) {
		return repository.findByDomainNameAndDeletedFalse(domainName);
	}

	public Page<GameSupplier> findByDomain(String domainName, Boolean deleted, String searchValue, Pageable pageable) {
		Specification<GameSupplier> spec = null;

		spec = addToSpec(domainName, spec, GameSupplierSpecifications::domain);
		spec = addToSpec(deleted, spec, GameSupplierSpecifications::deleted);
		spec = addToSpec(searchValue, spec, GameSupplierSpecifications::any);

		return repository.findAll(spec, pageable);
	}

	@Transactional(rollbackFor = Exception.class)
	@Retryable(maxAttempts = 5, backoff = @Backoff(random = true, delay = 50, maxDelay = 1000),
			exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
	public GameSupplier add(String domainName, GameSupplier gameSupplier, String authorGuid)
			throws Status500InternalServerErrorException, Status400BadRequestException {
		String trace = " [domainName="+domainName+", gameSupplier="+gameSupplier+", authorGuid="+authorGuid+"] ";
		log.trace("GameSupplierService.add " + trace);
		String clType = "create";
		GameSupplier existing = repository.findByDomainNameAndName(domainName, gameSupplier.getName());
		if (existing != null && !existing.getDeleted()) {
			throw new Status400BadRequestException("A game supplier with this name already exists");
		} else if (existing != null && existing.getDeleted()) {
			// Game supplier with the same name, but was previously deleted. Undoing delete.
			clType = "edit";
			gameSupplier = existing;
			gameSupplier.setDeleted(false);
		} else {
			Domain domain = domainRepository.findOrCreateByName(domainName, Domain::new);
			gameSupplier.setDomain(domain);
		}
		gameSupplier = repository.save(gameSupplier);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.copy(gameSupplier, new GameSupplier(),
					new String[]{"domain", "name", "deleted"});
			changeLogService.registerChangesWithDomain("GameSupplier", clType, gameSupplier.getId(), authorGuid,
					null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, domainName);
		} catch (Exception e) {
			String msg = "Changelog registration for game supplier add failed";
			log.error(msg + trace + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return gameSupplier;
	}

	@Transactional(rollbackFor = Exception.class)
	@Retryable(maxAttempts = 5, backoff = @Backoff(random = true, delay = 50, maxDelay = 1000),
			exclude = { NotRetryableErrorCodeException.class }, include = Exception.class)
	public GameSupplier update(String domainName, GameSupplier gameSupplier, GameSupplier update, String authorGuid)
			throws Status500InternalServerErrorException {
		String trace = " [domainName="+domainName+", gameSupplier="+gameSupplier+", update="+update
				+", authorGuid="+authorGuid+"] ";
		log.trace("GameSupplierService.update " + trace);
		GameSupplier gameSupplierCopy = new GameSupplier();
		copy(gameSupplier, gameSupplierCopy);
		gameSupplier.setName(update.getName());
		gameSupplier.setDeleted(update.getDeleted());
		gameSupplier = repository.save(gameSupplier);
		try {
			boolean deleted = gameSupplier.getDeleted().booleanValue();
			String type = ((deleted != gameSupplierCopy.getDeleted().booleanValue()) && (deleted))
						? "delete"
						: "edit";
			List<ChangeLogFieldChange> clfc = changeLogService.copy(gameSupplier, gameSupplierCopy,
					new String[]{"name", "deleted"});
			changeLogService.registerChangesWithDomain("GameSupplier", type, gameSupplier.getId(), authorGuid,
					null, null, clfc, Category.SUPPORT, SubCategory.GAMES, 0, domainName);
		} catch (Exception e) {
			String msg = "Changelog registration for game supplier update failed";
			log.error(msg + trace + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return gameSupplier;
	}

	private void copy(GameSupplier from, GameSupplier to) {
		to.setName(from.getName());
		to.setDeleted(from.getDeleted());
	}

	public Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
		return changeLogService.listLimited(ChangeLogRequest.builder()
				.entityRecordId(id)
				.entities(new String[] { "GameSupplier" })
				.page(p)
				.build()
		);
	}

	private Specification<GameSupplier> addToSpec(final String aString, Specification<GameSupplier> spec,
			Function<String, Specification<GameSupplier>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<GameSupplier> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<GameSupplier> addToSpec(final Boolean aBoolean, Specification<GameSupplier> spec,
	        Function<Boolean, Specification<GameSupplier>> predicateMethod) {
		if (aBoolean != null) {
			Specification<GameSupplier> localSpec = Specification.where(predicateMethod.apply(aBoolean));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}
}
