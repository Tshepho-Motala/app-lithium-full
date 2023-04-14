package lithium.service.games.data.specifications;

import java.util.ArrayList;
import java.util.List;
import lithium.service.games.data.entities.Domain;
import lithium.service.games.data.entities.Domain_;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.Game_;
import lithium.specification.JoinableSpecification;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class GamesSpecification {
	public static Specification<Game> searchAll(
		final Boolean enabled,
		final String domainName,
		final String providerGuid,
		final String gameName
	) {
		return new JoinableSpecification<Game>() {
			@Override
			public Predicate toPredicate(Root<Game> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Game, Domain> domainJoin = root.join(Game_.domain, JoinType.INNER);
				Predicate p = cb.like(cb.upper(root.get(Game_.providerGuid)), "%"+providerGuid+"%");
				p = cb.or(cb.like(cb.upper(root.get(Game_.name)), "%"+gameName.toUpperCase()+"%"));
				p = cb.and(
					cb.equal(root.get(Game_.enabled), enabled),
					cb.equal(domainJoin.get(Domain_.name), domainName)
				);
				return p;
			}
		};
	}
	
	public static Specification<Game> searchAll(
		final String domainName,
		final String providerGuid,
		final String gameName
	) {
		return new JoinableSpecification<Game>() {
			@Override
			public Predicate toPredicate(Root<Game> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<Game, Domain> domainJoin = root.join(Game_.domain, JoinType.INNER);
				Predicate p = cb.like(cb.upper(root.get(Game_.providerGuid)), "%"+providerGuid+"%");
				p = cb.or(cb.like(cb.upper(root.get(Game_.name)), "%"+gameName.toUpperCase()+"%"));
				p = cb.and(
					cb.equal(domainJoin.get(Domain_.name), domainName)
				);
				return p;
			}
		};
	}

	public static Specification<Game> domain(final String id) {
		return (root, query, cb) -> {
			Predicate p = cb.like(root.get(Game_.domain).as(String.class), id + "%");
			return p;
		};
	}

	public static Specification<Game> domainIn(final List<Domain> domains) {
		return (root, query, cb) -> {
			List<Long> domainIds = new ArrayList<>();
			for (Domain domain : domains) {
				domainIds.add(domain.getId());
			}

			if (domainIds.isEmpty()) domainIds.add(0L);

			return root.get(Game_.domain).in(domainIds);
		};
	}

	public static Specification<Game> gameSupplier(final String id) {
		return (root, query, cb) -> {
			Predicate p = cb.like(root.get(Game_.gameSupplier).as(String.class), id + "%");
			return p;
		};
	}

	public static Specification<Game> providerGuid(final String id) {
		return (root, query, cb) -> {
			Predicate p = cb.like(root.get(Game_.providerGuid).as(String.class), id + "%");
			return p;
		};
	}

	public static Specification<Game> idStartsWith(final String id) {
		return (root, query, cb) -> {
			Predicate p = cb.like(root.get(Game_.id).as(String.class), id + "%");
			return p;
		};
	}

	public static Specification<Game> enabled(final String enabled) {
		return (root, query, cb) -> {
			boolean gameEnabled = Boolean.parseBoolean(enabled);

			if (gameEnabled) {
				return cb.isTrue(root.get(Game_.enabled));
			} else {
				return cb.or(
						cb.isNull(root.get(Game_.enabled)),
						cb.isFalse(root.get(Game_.enabled))
				);
			}
		};
	}

	public static Specification<Game> freeGame(final String freeGame) {
		return (root, query, cb) -> {
			boolean isFreeGame = Boolean.parseBoolean(freeGame);

			if (isFreeGame) {
				return cb.isTrue(root.get(Game_.freeGame));
			} else {
				return cb.or(
						cb.isNull(root.get(Game_.freeGame)),
						cb.isFalse(root.get(Game_.freeGame))
				);
			}
		};
	}

	public static Specification<Game> visible(final String visible) {
		return (root, query, cb) -> {
			boolean gameIsVisible = Boolean.parseBoolean(visible);

			if (gameIsVisible) {
				return cb.isTrue(root.get(Game_.visible));
			} else {
				return cb.or(
						cb.isNull(root.get(Game_.visible)),
						cb.isFalse(root.get(Game_.visible))
				);
			}
		};
	}

	public static Specification<Game> instantRewardEnabled(final String instantRewardEnabled) {
		return (root, query, cb) -> {
			boolean isInstantRewardEnabled = Boolean.parseBoolean(instantRewardEnabled);

			if (isInstantRewardEnabled) {
				return cb.isTrue(root.get(Game_.instantRewardEnabled));
			} else {
				return cb.or(
						cb.isNull(root.get(Game_.instantRewardEnabled)),
						cb.isFalse(root.get(Game_.instantRewardEnabled))
				);
			}
		};
	}

	public static Specification<Game> liveCasinoEnabled(final String liveCasinoEnabled) {
		return (root, query, cb) -> {
			boolean isLiveCasino = Boolean.parseBoolean(liveCasinoEnabled);

			if (isLiveCasino) {
				return cb.isTrue(root.get(Game_.liveCasino));
			} else {
				return cb.or(
						cb.isNull(root.get(Game_.liveCasino)),
						cb.isFalse(root.get(Game_.liveCasino))
				);
			}
		};
	}

	public static Specification<Game> recentlyPlayed(final String recentlyPlayed) {
		return (root, query, cb) -> {
			boolean isExcludeRecentlyPlayed = Boolean.parseBoolean(recentlyPlayed);

			if (isExcludeRecentlyPlayed) {
				return cb.isTrue(root.get(Game_.excludeRecentlyPlayed));
			} else {
				return cb.or(
						cb.isNull(root.get(Game_.excludeRecentlyPlayed)),
						cb.isFalse(root.get(Game_.excludeRecentlyPlayed))
				);
			}
		};
	}

	public static Specification<Game> progressiveJackpot(final String progressiveJackpot) {
		return (root, query, cb) -> {
			boolean isProgressiveJackpot = Boolean.parseBoolean(progressiveJackpot);

			if (isProgressiveJackpot) {
				return cb.isTrue(root.get(Game_.progressiveJackpot));
			} else {
				return cb.or(
						cb.isNull(root.get(Game_.progressiveJackpot)),
						cb.isFalse(root.get(Game_.progressiveJackpot))
				);
			}
		};
	}

	public static Specification<Game> gameSupplierIdsIn(final List<Long> ids) {
		return (root, query, cb) -> {
			if (ids.isEmpty()) {
				ids.add(0L);
			}
			Predicate p = root.get(Game_.gameSupplier).in(ids);
			return p;
		};
	}

	public static Specification<Game> providersIn(final List<String> providers) {
		return (root, query, cb) -> {
			Predicate p = root.get(Game_.providerGuid).in(providers);
			return p;
		};
	}


	public static Specification<Game> any(final String search) {
		return (root, query, cb) -> {
			Predicate p = cb.or(
					cb.like(root.get(Game_.name), search.toUpperCase() + "%"),
					cb.like(root.get(Game_.providerGuid), search.toUpperCase() + "%"),
					cb.like(root.get(Game_.supplierGameGuid), search.toUpperCase() + "%"),
					cb.like(root.get(Game_.providerGameId), search.toUpperCase() + "%")
			);
			try {
				long idSearch = Long.parseLong(search);
				p = cb.or(p, cb.equal(root.get(Game_.id), idSearch));
			} catch (NumberFormatException e) {
				// Not logging anything, if not parsable, then ignore
			}
			return p;
		};
	}
}
