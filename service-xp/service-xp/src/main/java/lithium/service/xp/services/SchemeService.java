package lithium.service.xp.services;

import lithium.service.xp.data.entities.Domain;
import lithium.service.xp.data.entities.Level;
import lithium.service.xp.data.entities.LevelBonus;
import lithium.service.xp.data.entities.LevelNotification;
import lithium.service.xp.data.entities.Scheme;
import lithium.service.xp.data.entities.Status;
import lithium.service.xp.data.repositories.LevelBonusRepository;
import lithium.service.xp.data.repositories.LevelNotificationRepository;
import lithium.service.xp.data.repositories.LevelRepository;
import lithium.service.xp.data.repositories.SchemeRepository;
import lithium.service.xp.data.specifications.SchemeSpecification;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SchemeService {
	@Autowired DomainService domainService;
	@Autowired SchemeRepository repository;
	@Autowired StatusService statusService;
	@Autowired LevelRepository levelRepository;
	@Autowired LevelBonusRepository levelBonusRepository;
	@Autowired LevelNotificationRepository levelNotificationRepository;
	@Autowired MessageSource messageSource;
	
	public Scheme findActiveScheme(String domainName) {
		return repository.findByDomainNameAndStatus(domainName, statusService.findByName("ACTIVE"));
	}
	
	public Page<Scheme> findByDomains(List<String> domains, String searchValue, Pageable pageable, LithiumTokenUtil tokenUtil) {
		Specification<Scheme> spec = Specification.where(SchemeSpecification.domains(domains));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Scheme> s = Specification.where(SchemeSpecification.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Scheme> result = repository.findAll(spec, pageable);
		return result;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Scheme create(lithium.service.xp.client.objects.Scheme schemePost, Locale locale) throws Exception {
		Domain domain = domainService.findOrCreate(schemePost.getDomain().getName());
		
		Scheme scheme = repository.findByDomainNameAndNameIgnoreCase(domain.getName(), schemePost.getName());
		if (scheme != null) throw new Exception(messageSource.getMessage("UI_NETWORK_ADMIN.XP.SCHEME.NAME.EXISTS", null, locale));
		
		Status status = statusService.findByName(schemePost.getStatus().getName());
		
		if (status.getName().equalsIgnoreCase("ACTIVE")) {
			Scheme activeScheme = findActiveScheme(domain.getName());
			if (activeScheme != null) {
				activeScheme.setStatus(statusService.findByName("INACTIVE"));
				activeScheme = repository.save(activeScheme);
			}
		}
		
		scheme = Scheme.builder()
		.domain(domain)
		.name(schemePost.getName())
		.description(schemePost.getDescription())
		.status(status)
		.wagerPercentage(schemePost.getWagerPercentage())
		.build();
		scheme = repository.save(scheme);

		addLevels(scheme, schemePost);

		return repository.save(scheme);
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Scheme edit(Long id, lithium.service.xp.client.objects.Scheme schemePost) {
		Scheme scheme = repository.findOne(id);
		Status status = statusService.findByName(schemePost.getStatus().getName());
		if (!scheme.getStatus().getName().equalsIgnoreCase("ACTIVE") && status.getName().equalsIgnoreCase("ACTIVE")) {
			Scheme activeScheme = findActiveScheme(scheme.getDomain().getName());
			if (activeScheme != null && activeScheme.getId() != id) {
				activeScheme.setStatus(statusService.findByName("INACTIVE"));
				activeScheme = repository.save(activeScheme);
			}
		}
		scheme.setDescription(schemePost.getDescription());
		scheme.setStatus(status);
		scheme.setWagerPercentage(schemePost.getWagerPercentage());
		if (schemePost.getLevels() != null && !schemePost.getLevels().isEmpty()) {
			deleteAllLevels(scheme);
			addLevels(scheme, schemePost);
		} else if ((schemePost.getLevels() == null || schemePost.getLevels().isEmpty()) && (!scheme.getLevels().isEmpty())) {
			deleteAllLevels(scheme);
		}
		scheme = repository.save(scheme);
		return scheme;
	}

	private void deleteAllLevels(Scheme scheme) {
		List<Level> levels = levelRepository.findLevelsByScheme(scheme);
		for (Level level: levels) {
			scheme.getLevels().remove(level);
			scheme = repository.save(scheme);
			levelBonusRepository.deleteByLevel(level);
			levelNotificationRepository.deleteByLevel(level);
			levelRepository.delete(level);
		}
		scheme.setLevels(levelRepository.findLevelsByScheme(scheme));
	}

	private void addLevels(Scheme scheme, lithium.service.xp.client.objects.Scheme schemePost) {
		List<lithium.service.xp.client.objects.Level> sortedLevels = schemePost.getLevels().stream()
				.sorted((l1, l2) -> Integer.compare(l1.getNumber(), l2.getNumber()))
				.collect(Collectors.toList());
		for (int i = 0; i < sortedLevels.size(); i++) {
			lithium.service.xp.client.objects.Level l = sortedLevels.get(i);
			l.setNumber(i + 1);
		}

		List<Level> levels = new ArrayList<>();
		for (lithium.service.xp.client.objects.Level l: sortedLevels) {
			LevelBonus levelBonus = null;
			if (l.getBonus() != null && l.getBonus().getBonusCode() != null && !l.getBonus().getBonusCode().isEmpty()) {
				levelBonus = LevelBonus.builder().bonusCode(l.getBonus().getBonusCode()).build();
				levelBonus = levelBonusRepository.save(levelBonus);
			}

			Level level = Level.builder()
			.scheme(scheme)
			.number(l.getNumber())
			.requiredXp(l.getRequiredXp())
			.description(l.getDescription())
			.milestone(l.getMilestone())
			.bonus(levelBonus)
			.build();
			level = levelRepository.save(level);

			if (l.getNotifications() != null) {
				List<LevelNotification> notifications = new ArrayList<>();

				for (lithium.service.xp.client.objects.LevelNotification ln : l.getNotifications()) {
					LevelNotification levelNotification = LevelNotification.builder()
					.level(level)
					.triggerPercentage(ln.getTriggerPercentage())
					.notificationName(ln.getNotificationName())
					.build();
					levelNotification = levelNotificationRepository.save(levelNotification);
					notifications.add(levelNotification);
				}

				level.setNotifications(notifications);
			}

			levels.add(level);
		}

		scheme.setLevels(levels);
	}
}
