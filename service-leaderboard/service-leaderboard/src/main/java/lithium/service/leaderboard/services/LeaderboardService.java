package lithium.service.leaderboard.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.metrics.LithiumMetricsService;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingSummaryAccountClient;
import lithium.service.accounting.objects.SummaryAccount;
import lithium.service.casino.client.CasinoBonusClient;
import lithium.service.casino.client.data.BonusRevision;
import lithium.service.casino.client.objects.BonusRevisionRequest;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.leaderboard.client.objects.Granularity;
import lithium.service.leaderboard.client.objects.LeaderboardBasic;
import lithium.service.leaderboard.client.objects.LeaderboardConversionBasic;
import lithium.service.leaderboard.client.objects.Type;
import lithium.service.leaderboard.data.dto.LeaderboardEntries;
import lithium.service.leaderboard.data.dto.LeaderboardEntryBasic;
import lithium.service.leaderboard.data.entities.Domain;
import lithium.service.leaderboard.data.entities.Entry;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardConversion;
import lithium.service.leaderboard.data.entities.LeaderboardHistory;
import lithium.service.leaderboard.data.entities.LeaderboardPlaceNotification;
import lithium.service.leaderboard.data.entities.User;
import lithium.service.leaderboard.data.projections.EntryProjection;
import lithium.service.leaderboard.data.repositories.EntryRepository;
import lithium.service.leaderboard.data.repositories.LeaderboardConversionRepository;
import lithium.service.leaderboard.data.repositories.LeaderboardPlaceNotificationRepository;
import lithium.service.leaderboard.data.repositories.LeaderboardRepository;
import lithium.service.leaderboard.data.specifications.EntrySpecifications;
import lithium.service.leaderboard.data.specifications.LeaderboardConversionSpecifications;
import lithium.service.leaderboard.data.specifications.LeaderboardPlaceNotificationSpecifications;
import lithium.service.leaderboard.data.specifications.LeaderboardSpecifications;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.xp.client.XPClient;
import lithium.service.xp.client.objects.Level;
import lithium.service.xp.client.objects.Scheme;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LeaderboardService {
	private static final String CURRENCY_CODE_XP = "XP";
	private static final String PLAYER_BALANCE = "PLAYER_BALANCE";

	@Autowired EntryRepository entryRepository;
	@Autowired LeaderboardRepository leaderboardRepository;

	@Autowired LeaderboardConversionRepository leaderboardConversionRepository;
	@Autowired LeaderboardPlaceNotificationRepository leaderboardPlaceNotificationRepository;
	@Autowired DomainService domainService;
	@Autowired UserService userService;
	@Autowired LithiumServiceClientFactory services;
	@Autowired GatewayExchangeStream gatewayExchangeStream;
	@Autowired LeaderboardHistoryService leaderboardHistoryService;
	@Autowired LithiumMetricsService metrics;
	@Autowired ModelMapper modelMapper;

	public Leaderboard edit(Leaderboard leaderboard, LeaderboardBasic leaderboardBasic) {
		leaderboard = basicToFull(leaderboard, leaderboardBasic);
		return save(leaderboard);
	}

	private Leaderboard basicToFull(LeaderboardBasic lbb) {
		return basicToFull(Leaderboard.builder().build(), lbb);
	}
	private Leaderboard basicToFull(Leaderboard leaderboard, LeaderboardBasic lbb) {
		leaderboard = leaderboard.toBuilder()
		.name(lbb.getName())
		.description(lbb.getDescription())
		.xpLevelMin(lbb.getXpLevelMin())
		.xpLevelMax(lbb.getXpLevelMax())
		.xpPointsMin(lbb.getXpPointsMin())
		.xpPointsMax(lbb.getXpPointsMax())
		.xpPointsPeriod(lbb.getXpPointsPeriod())
		.xpPointsGranularity(lbb.getXpPointsGranularity())
		.startDate(lbb.getStartDate())
		.durationPeriod(lbb.getDurationPeriod())
		.durationGranularity(lbb.getDurationGranularity())
		.recurrencePattern(lbb.getRecurrencePattern())
		.amount(lbb.getAmount())
		.scoreToPoints(lbb.getScoreToPoints())
		.notification(lbb.getNotification())
		.notificationNonTop(lbb.getNotificationNonTop())
		.build();

		return leaderboard;
	}

	public Leaderboard add(LeaderboardBasic lb) {
		Domain domain = domainService.findOrCreate(lb.getDomainName());

		lb.setStartDate(lb.getStartDate().withTimeAtStartOfDay());

		//recurrencePattern: "DTSTART:20190929T000000Z↵RRULE:FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,TH;COUNT=15"
//		String[] recurrence = lb.getRecurrencePattern().split("\n");
//		log.info("" + recurrence);
//
//		DateTime startDate = new DateTime(recurrence[0].split(":")[1].trim());
//		lb.setStartDate(startDate);

		Leaderboard leaderboard = leaderboardRepository.findByDomainAndStartDateAndRecurrencePatternAndXpLevelMinAndXpLevelMaxAndXpPointsMinAndXpPointsMaxAndXpPointsPeriodAndXpPointsGranularity(domain, lb.getStartDate(), lb.getRecurrencePattern(), lb.getXpLevelMin(), lb.getXpLevelMax(), lb.getXpPointsMin(), lb.getXpPointsMax(), lb.getXpPointsPeriod(), lb.getXpPointsGranularity());
		if (leaderboard == null) {
			leaderboard = basicToFull(lb);
			leaderboard.setDomain(domain);
			leaderboard = save(leaderboard);
			leaderboardHistoryService.add(leaderboard);
			return leaderboard;
		}
		return null;
	}

	public LeaderboardConversion addConversion(LeaderboardConversionBasic lbc) {
		Leaderboard leaderboard = findLeaderboard(lbc.getLeaderboardId());
		LeaderboardConversion leaderboardConversion = leaderboardConversionRepository.findByLeaderboardAndType(leaderboard, Type.fromId(lbc.getTypeId()));
		if (leaderboardConversion == null) {
			leaderboardConversion = leaderboardConversionRepository.save(
				LeaderboardConversion.builder()
				.leaderboard(leaderboard)
				.type(Type.fromId(lbc.getTypeId()))
				.conversion(lbc.getConversion())
				.build()
			);
		}
		return leaderboardConversion;
	}
	public LeaderboardConversion editConversion(LeaderboardConversionBasic lbc) {
		LeaderboardConversion leaderboardConversion = leaderboardConversionRepository.findOne(lbc.getId());
		leaderboardConversion.setConversion(lbc.getConversion());
		leaderboardConversion = leaderboardConversionRepository.save(leaderboardConversion);
		return leaderboardConversion;
	}

	public LeaderboardPlaceNotification findByLeaderboardAndRank(Leaderboard leaderboard, Integer rank) {
		return leaderboardPlaceNotificationRepository.findByLeaderboardAndRank(leaderboard, rank);
	}
	public LeaderboardPlaceNotification addNotification(LeaderboardPlaceNotification lpn) {
		LeaderboardPlaceNotification leaderboardPlaceNotification = findByLeaderboardAndRank(lpn.getLeaderboard(), lpn.getRank());
		if (leaderboardPlaceNotification == null) {
			leaderboardPlaceNotification = leaderboardPlaceNotificationRepository.save(
				LeaderboardPlaceNotification.builder()
				.leaderboard(lpn.getLeaderboard())
				.bonusCode(lpn.getBonusCode())
				.notification(lpn.getNotification())
				.rank(lpn.getRank())
				.build()
			);
		}
		return leaderboardPlaceNotification;
	}
	public LeaderboardPlaceNotification editNotification(LeaderboardPlaceNotification lpn) {
		LeaderboardPlaceNotification leaderboardPlaceNotification = leaderboardPlaceNotificationRepository.findOne(lpn.getId());

		leaderboardPlaceNotification.setBonusCode(lpn.getBonusCode());
		leaderboardPlaceNotification.setNotification(lpn.getNotification());
		leaderboardPlaceNotification.setRank(lpn.getRank());
		leaderboardPlaceNotification = leaderboardPlaceNotificationRepository.save(leaderboardPlaceNotification);
		return leaderboardPlaceNotification;
	}

	public Leaderboard save(Leaderboard leaderboard) {
		return leaderboardRepository.save(leaderboard);
	}

	public Page<Leaderboard> findByDomains(String username, List<String> domains, String searchValue, Boolean visible, Boolean enabled, Pageable pageable) {
		Specification<Leaderboard> spec = Specification.where(LeaderboardSpecifications.domains(domains));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Leaderboard> s = Specification.where(LeaderboardSpecifications.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		if (visible != null) spec = spec.and(LeaderboardSpecifications.visible(visible));
		if (enabled != null) spec = spec.and(LeaderboardSpecifications.enabled(enabled));
		Page<Leaderboard> result = leaderboardRepository.findAll(spec, pageable);
		return result;
	}

	public LeaderboardEntries findEntriesFromLeaderboard(Leaderboard leaderboard) {
		List<EntryProjection> entries = entryRepository.findTopEntries(leaderboard);
		List<LeaderboardEntryBasic> basicEntries = new ArrayList<>();
		for (EntryProjection entry: entries) {
			LeaderboardEntryBasic basicEntry = new LeaderboardEntryBasic();
			modelMapper.map(entry, basicEntry);
			basicEntries.add(basicEntry);
		}
		LeaderboardEntries lbe = LeaderboardEntries.builder()
		.leaderboard(leaderboardRepository.findOne(leaderboard.getId()))
		.entries(basicEntries) //.stream().distinct().collect(Collectors.toList()))
		.domain(leaderboard.getDomain())
		.leaderboardPlaceNotifications(leaderboard.getLeaderboardPlaceNotifications())
		.build();

		return lbe;
	}

	public Page<LeaderboardConversion> findConversions(Leaderboard leaderboard, String searchValue, Pageable pageable) {
		Specification<LeaderboardConversion> spec = Specification.where(LeaderboardConversionSpecifications.leaderboard(leaderboard.getId()));
//		if ((searchValue != null) && (searchValue.length() > 0)) {
//			Specifications<Leaderboard> s = Specifications.where(LeaderboardSpecifications.any(searchValue));
//			spec = (spec == null)? s: spec.and(s);
//		}
		Page<LeaderboardConversion> result = leaderboardConversionRepository.findAll(spec, pageable);
		return result;
	}

	public Page<LeaderboardPlaceNotification> findNotifications(Leaderboard leaderboard, String searchValue, Pageable pageable) {
		Specification<LeaderboardPlaceNotification> spec = Specification.where(LeaderboardPlaceNotificationSpecifications.leaderboard(leaderboard.getId()));
//		if ((searchValue != null) && (searchValue.length() > 0)) {
//			Specifications<Leaderboard> s = Specifications.where(LeaderboardSpecifications.any(searchValue));
//			spec = (spec == null)? s: spec.and(s);
//		}
		Page<LeaderboardPlaceNotification> result = leaderboardPlaceNotificationRepository.findAll(spec, pageable);
		return result;
	}

	public Page<Entry> findHistoryPlaces(LeaderboardHistory leaderboardHistory, String searchValue, PageRequest pageable) {
		Specification<Entry> spec = Specification.where(EntrySpecifications.leaderboardHistory(leaderboardHistory.getId()));
//		if ((searchValue != null) && (searchValue.length() > 0)) {
//			Specifications<Leaderboard> s = Specifications.where(LeaderboardSpecifications.any(searchValue));
//			spec = (spec == null)? s: spec.and(s);
//		}
		Page<Entry> result = entryRepository.findAll(spec, pageable);
		return result;
	}

	public BigDecimal xpSummary(User user, Granularity xpPointsGranularity, int xpPointsPeriod) {
		Response<List<SummaryAccount>> response = getAccountingSummaryAccountClient().findLastByOwnerGuid(
			user.domainName(),
			user.guid(),
			xpPointsPeriod,
			xpPointsGranularity.granularity(),
			PLAYER_BALANCE,
			CURRENCY_CODE_XP
		);

		long cents = 0L;
		if (response.isSuccessful()) {
			if (!response.getData().isEmpty()) {
				for (SummaryAccount sa:response.getData()) {
					cents += ((sa.getDebitCents() - sa.getCreditCents())*-1);
				}
			}
		}
		return new BigDecimal(Math.abs(cents)).movePointLeft(2);
	}

	public SummaryAccount xpSummary(String ownerGuid, int granularity) {
		Response<SummaryAccount> summaryAccount = getAccountingSummaryAccountClient().findGranular(
			PLAYER_BALANCE,
			PLAYER_BALANCE,
			CURRENCY_CODE_XP,
			ownerGuid,
			granularity,
			0
		);
		if (summaryAccount.isSuccessful()) return summaryAccount.getData();
		return null;
	}

	public AccountingSummaryAccountClient getAccountingSummaryAccountClient() {
		AccountingSummaryAccountClient client = null;
		try {
			client = services.target(AccountingSummaryAccountClient.class, "service-accounting-provider-internal", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return client;
	}

	private XPClient getXPClient() {
		XPClient client = null;
		try {
			client = services.target(XPClient.class, "service-xp", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting XPClient, " + e.getMessage(), e);
		}
		return client;
	}

	private AccountingClient getAccountingClient() {
		AccountingClient client = null;
		try {
			client = services.target(AccountingClient.class, "service-accounting-provider-internal", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting AccountingClient, " + e.getMessage(), e);
		}
		return client;
	}

	private UserApiInternalClient getUserApiInternalClient() {
		UserApiInternalClient client = null;
		try {
			client = services.target(UserApiInternalClient.class, "service-user", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting UserApiInternalClient, " + e.getMessage(), e);
		}
		return client;
	}

	private CasinoBonusClient getCasinoBonusClient() {
		CasinoBonusClient client = null;
		try {
			client = services.target(CasinoBonusClient.class, "service-casino", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting CasinoBonusClient, " + e.getMessage(), e);
		}
		return client;
	}

	private Integer xpLevel(String domainName, String playerGuid) throws Exception {
		Integer level = 0;
		Long xp = getAccountingClient().get("XP", domainName, playerGuid).getData();
		if (xp != null && xp > 0) {
			Scheme scheme = getXPClient().getActiveScheme(domainName).getData();
			List<Level> sortedLevels = scheme.getLevels().stream()
			.sorted((l1, l2) -> Integer.compare(l1.getNumber(), l2.getNumber()))
			.collect(Collectors.toList());
			for (Level l: sortedLevels) {
				if (xp >= l.getRequiredXp()) level = l.getNumber();
				else break;
			}
		}
		return level;
	}
	private List<Leaderboard> findLeaderboards(Domain domain, Integer xpLevel) {
		List<Leaderboard> leaderboards = leaderboardRepository.findByDomainAndEnabledTrueAndVisibleIsTrueAndXpLevelMinLessThanEqualAndXpLevelMaxGreaterThanEqual(domain, xpLevel, xpLevel);
		log.trace("Leaderboards :: "+leaderboards);
		return leaderboards;
	}
	private Leaderboard findLeaderboard(Long leaderboardId) {
		Leaderboard leaderboard = leaderboardRepository.findOne(leaderboardId);
		log.trace("Leaderboard :: "+leaderboard);
		return leaderboard;
	}

	private EntryProjection findEntryProjection(User user, LeaderboardHistory leaderboardHistory) {
		EntryProjection entryProjection = entryRepository.findByLeaderboardHistoryAndUser(leaderboardHistory, user);
		log.info("entryProjection :: "+entryProjection);
		return entryProjection;
	}
	private Entry findEntry(User user, LeaderboardHistory leaderboardHistory) {
		Entry entry = entryRepository.findByUserAndLeaderboardHistory(user, leaderboardHistory);
		log.info("entry :: "+entry);
		return entry;
	}

	private boolean validLeaderboardHistoryDateRange(LeaderboardHistory leaderboardHistory) {
		return (leaderboardHistory.getStartDate().isBeforeNow() && leaderboardHistory.getEndDate().isAfterNow());
	}

	private Entry findOrCreateEntry(User user, LeaderboardHistory leaderboardHistory, Type type, Long amountCents) throws Exception {
		return metrics.timer(log).time("findOrCreateEntry", (StopWatch sw) -> {
			log.info("--------------"+leaderboardHistory.getLeaderboard().getName()+"--------------");
			sw.start("findEntry");
			Entry entry = findEntry(user, leaderboardHistory);
			sw.stop();
			if (entry == null) {
				entry = Entry.builder()
				.leaderboardHistory(leaderboardHistory)
				.user(user)
				.build();
			}
			log.debug("existing entry found :: "+entry);

			BigDecimal s2p = leaderboardHistory.getLeaderboard().getScoreToPoints();
			BigDecimal add = BigDecimal.ZERO;
			LeaderboardConversion leaderboardConversion = null;
			switch (type) {
				case TYPE_WAGERED:
					sw.start("leaderboardConversion:TYPE_WAGERED");
					leaderboardConversion = leaderboardConversionRepository.findByLeaderboardAndType(leaderboardHistory.getLeaderboard(), Type.TYPE_WAGERED);
					if (leaderboardConversion != null) {
						log.debug("leaderboardConversion(bet) :: "+leaderboardConversion.getType().name()+" : "+leaderboardConversion.getConversion());
						add = leaderboardConversion.getConversion().multiply(new BigDecimal(amountCents).abs());
					}
					sw.stop();
					sw.start("leaderboardConversion:TYPE_HANDS");
					leaderboardConversion = leaderboardConversionRepository.findByLeaderboardAndType(leaderboardHistory.getLeaderboard(), Type.TYPE_HANDS);
					if (leaderboardConversion != null) {
						log.debug("leaderboardConversion(hands) :: "+leaderboardConversion.getType().name()+" : "+leaderboardConversion.getConversion());
						log.debug("Add1 :: "+add);
						add = add.add(leaderboardConversion.getConversion().multiply(new BigDecimal(100)));
						log.debug("Add2 :: "+add);
					}
					sw.stop();
					break;
				case TYPE_WIN:
					sw.start("leaderboardConversion:TYPE_WIN");
					leaderboardConversion = leaderboardConversionRepository.findByLeaderboardAndType(leaderboardHistory.getLeaderboard(), Type.TYPE_WIN);
					if (leaderboardConversion!=null) {
						log.debug("leaderboardConversion(win) :: "+leaderboardConversion.getType().name()+" : "+leaderboardConversion.getConversion());
						add = leaderboardConversion.getConversion().multiply(new BigDecimal(amountCents).abs());
					}
					sw.stop();
					break;
				default:
					break;
			}
			sw.start("addScore");
			log.debug("Score1 : "+entry.getScore());
			entry.addScore(add);
			log.debug("Score2 : "+entry.getScore());

			BigDecimal[] div = entry.getScore().divideAndRemainder(s2p);
			entry.setPoints(div[0].add(entry.getPoints()));
			entry.setScore(div[1]);
			log.debug("points : "+entry.getPoints()+"  score : "+entry.getScore());
			sw.stop();
			sw.start("save");
			entry = entryRepository.save(entry);
			sw.stop();
			log.debug("Entry :: "+entry);
			return entry;
		});
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public void registerTran(String domainName, String userGuid, String transactionType, Long amountCents) throws Exception {
		Domain domain = domainService.findOrCreate(domainName);
		User user = userService.findOrCreate(userGuid);
		if (user.getOptOut()) return;
		switch (transactionType) {
		case "CASINO_BET":
			registerBet(domain, user, amountCents);
			break;
		case "CASINO_WIN":
			registerWin(domain, user, amountCents);
			break;
		}
	}
	private void registerBet(Domain domain, User user, Long amountCents) throws Exception {
		registerEntry(domain, Type.TYPE_WAGERED, user, amountCents);
	}
	private void registerWin(Domain domain, User user, Long amountCents) throws Exception {
		registerEntry(domain, Type.TYPE_WIN, user, amountCents);
	}

	public Leaderboard playerCurrentActiveLeaderboard(String userGuid) {
		return playerCurrentActiveLeaderboard(userService.findOrCreate(userGuid));
	}
	private Leaderboard playerCurrentActiveLeaderboard(User user) {
		Entry entry = activeEntry(user);
		if (entry != null) return entry.getLeaderboardHistory().getLeaderboard();
		return null;
	}
	private LeaderboardHistory playerCurrentActiveLeaderboardHistory(User user) {
		Entry entry = activeEntry(user);
		if (entry != null) return entry.getLeaderboardHistory();
		return null;
	}
	private Entry activeEntry(User user) {
		Entry entry = entryRepository.findByUserAndLeaderboardHistoryClosedFalseAndLeaderboardHistoryLeaderboardVisibleTrueAndLeaderboardHistoryLeaderboardEnabledTrue(user);
		return entry;
	}
	public EntryProjection activeEntry(String userGuid) {
		return entryRepository.findByUserGuidAndLeaderboardHistoryClosedFalseAndLeaderboardHistoryLeaderboardVisibleTrueAndLeaderboardHistoryLeaderboardEnabledTrue(userGuid);
	}

	private void registerEntry(Domain domain, Type type, User user, Long amountCents) throws Exception {
		metrics.timer(log).time("registerEntry", (StopWatch sw) -> {
			sw.start("playerCurrentActiveLeaderboardHistory");
			LeaderboardHistory albh = playerCurrentActiveLeaderboardHistory(user);
			Leaderboard playerActiveLeaderboard = (albh!=null)?albh.getLeaderboard():null;
			sw.stop();

			if (playerActiveLeaderboard != null) {
				log.info("Player ("+user.guid()+") currently on active leaderboard :"+playerActiveLeaderboard);
	//			long xpCents = xpSummary(user, playerActiveLeaderboard.getXpPointsGranularity(), playerActiveLeaderboard.getXpPointsPeriod());
	//			log.info("xpCents :: "+xpCents+" min: "+playerActiveLeaderboard.getXpPointsMin()+" max: "+playerActiveLeaderboard.getXpPointsMax());

	//			LeaderboardHistory leaderboardHistory = playerCurrentLeaderboardHistory(playerActiveLeaderboard);
	//			if (leaderboardHistory == null) return;
	//			if (!validLeaderboardHistoryDateRange(albh)) return;
				sw.start("findOrCreateEntry");
				findOrCreateEntry(user, albh, type, amountCents);
				sw.stop();
			} else {
	//			List<Leaderboard> leaderboards = findLeaderboards(domain);
				log.info("Player ("+user+") not on active leaderboard, need to check requirements");
				// not on active leaderboard, need to check requirements.
				sw.start("xpLevel");
				Integer xpLevel = xpLevel(user.domainName(), user.guid());
				sw.stop();
				sw.start("findLeaderboards");
				List<Leaderboard> leaderboards = findLeaderboards(domain, xpLevel);
				sw.stop();
				log.info("Player ("+user.guid()+") currently xpLevel :"+xpLevel+" potential leaderboards found: "+leaderboards.size());

				for (Leaderboard leaderboard:leaderboards) {
					log.info("leaderboard ("+user.guid()+"):: "+leaderboard);

					sw.start("xpSummary");
					BigDecimal xpCents = xpSummary(user, leaderboard.getXpPointsGranularity(), leaderboard.getXpPointsPeriod());
					sw.stop();
					log.info("xpCents :: "+xpCents);
					if ((xpCents.compareTo(leaderboard.getXpPointsMin()) >= 0) && (xpCents.compareTo(leaderboard.getXpPointsMax()) <= 0)) {
						//requirements met. have to add player to leaderboard.
						log.info("Earned required xp ("+xpCents+") : ("+leaderboard.getXpPointsMin()+"-"+leaderboard.getXpPointsMax()+")");
						sw.start("leaderboardHistory");
						LeaderboardHistory leaderboardHistory = leaderboardHistoryService.findCurrentOpen(leaderboard);
						sw.stop();
						if (leaderboardHistory == null) continue;
						if (!validLeaderboardHistoryDateRange(leaderboardHistory)) {
							log.info("Leaderboard daterange invalid. ("+leaderboardHistory.getStartDate()+"-"+leaderboardHistory.getEndDate()+")");
							continue;
						}
						sw.start("findOrCreateEntry");
						findOrCreateEntry(user, leaderboardHistory, type, amountCents);
						sw.stop();
						playerActiveLeaderboard = leaderboard;
						break;
					}
				}
			}

			if (playerActiveLeaderboard != null) {
				sw.start("stream");
				streamUpdate(domain, playerActiveLeaderboard);
				streamVision(domain, playerActiveLeaderboard, user);
				sw.stop();
			}
		});
	}

	public void streamVision(String domainName, Leaderboard leaderboard, String playerGuid) {
		streamVision(domainService.findOrCreate(domainName), leaderboard, userService.findOrCreate(playerGuid));
	}
	public void streamVision(Domain domain, Leaderboard leaderboard, User user) {
		log.info("Streaming User Visions");
		try {
			LeaderboardHistory leaderboardHistory = playerCurrentActiveLeaderboardHistory(user);
			EntryProjection entry = findEntryProjection(user, leaderboardHistory);
			log.info("Streaming update to entry. "+entry);
			String leaderboardStr = new ObjectMapper().writeValueAsString(entry);
			log.info("Streaming :: "+leaderboardStr);
//			"playerroom/"+u
			gatewayExchangeStream.process("playerroom/"+user.guid(), "visions", leaderboardStr);
		} catch (JsonProcessingException e) {
			log.error("Could not stream update to leaderboard. "+leaderboard, e);
		}
	}

	public void enrichUserData(LeaderboardEntries leaderboardEntries) {
		try {
			List<String> userGuids = new ArrayList<>();
			for (LeaderboardEntryBasic entry: leaderboardEntries.getEntries()) {
				userGuids.add(entry.getUserGuid());
			}
			if (!userGuids.isEmpty()) {
				UserApiInternalClient client = getUserApiInternalClient();
				List<lithium.service.user.client.objects.User> users = client.getUsers(userGuids).getData();
				for (lithium.service.user.client.objects.User u: users) {
					List<LeaderboardEntryBasic> entries = leaderboardEntries.getEntries()
					.stream()
					.filter((entry) -> entry.getUserGuid().equalsIgnoreCase(u.guid()))
					.collect(Collectors.toList());
					for (LeaderboardEntryBasic entry: entries) {
						entry.setUserName(u.getUsername());
						entry.setFirstName(u.getFirstName());
					}
				}
			}
		} catch (Exception e) {
			log.warn("Could not enrich leaderboard with user data. " + leaderboardEntries, e);
		}
	}

	public void enrichBonusData(String domainName, LeaderboardEntries leaderboardEntries) {
		try {
			List<BonusRevisionRequest> requests = new ArrayList<>();
			List<Integer> ranksInCurrentEntries = new ArrayList<>();
			for (LeaderboardEntryBasic entry: leaderboardEntries.getEntries()) {
				ranksInCurrentEntries.add(entry.getRank());
			}
			for (LeaderboardPlaceNotification leaderboardPlaceNotification: leaderboardEntries.getLeaderboardPlaceNotifications()) {
				if (leaderboardPlaceNotification.getBonusCode() != null &&
						!leaderboardPlaceNotification.getBonusCode().isEmpty()) {
					if (ranksInCurrentEntries.contains(leaderboardPlaceNotification.getRank())) {
						requests.add(
							BonusRevisionRequest.builder()
							.bonusCode(leaderboardPlaceNotification.getBonusCode())
							.bonusType(BonusRevision.BONUS_TYPE_TRIGGER)
							.domainName(domainName)
							.build()
						);
					}
				}
			}
			if (!requests.isEmpty()) {
				CasinoBonusClient client = getCasinoBonusClient();
				List<BonusRevision> bonusRevisions = client.findRevisions(requests).getData();
				for (BonusRevision bonusRevision: bonusRevisions) {
					List<LeaderboardPlaceNotification> leaderboardPlaceNotifications = leaderboardEntries.getLeaderboardPlaceNotifications()
					.stream()
					.filter((lpn) -> lpn.getBonusCode() != null && lpn.getBonusCode().equalsIgnoreCase(bonusRevision.getBonusCode()) &&
						bonusRevision.getBonusType().compareTo(BonusRevision.BONUS_TYPE_TRIGGER) == 0)
					.collect(Collectors.toList());
					for (LeaderboardPlaceNotification leaderboardPlaceNotification: leaderboardPlaceNotifications) {
						leaderboardPlaceNotification.setBonusRevision(bonusRevision);
						for (LeaderboardEntryBasic entry: leaderboardEntries.getEntries()) {
							if (entry.getRank().compareTo(leaderboardPlaceNotification.getRank()) == 0) {
								entry.setPrizeDescription(bonusRevision.getBonusDescription());
								entry.setPrizeImageBase64(new String(Base64.getEncoder().encode(bonusRevision.getGraphic().getImage())));
							}
						}
					}
				}
			}
			leaderboardEntries.setLeaderboardPlaceNotifications(null);
		} catch (Exception e) {
			log.warn("Could not enrich leaderboard with bonus data. " + leaderboardEntries, e);
		}
	}

	public void streamUpdate(Domain domain, Leaderboard leaderboard) {
		log.info("Streaming Domain Leaderboard");
		try {
			LeaderboardEntries leaderboardEntries = findEntriesFromLeaderboard(leaderboard);
			enrichUserData(leaderboardEntries);
			enrichBonusData(domain.getName(), leaderboardEntries);
			log.info("Streaming update to leaderboardEntries. "+leaderboardEntries);
			String leaderboardStr = new ObjectMapper().writeValueAsString(leaderboardEntries);
			log.info("Streaming :: "+leaderboardStr);
			gatewayExchangeStream.process(domain.getName()+"/public/"+"leaderboard/"+leaderboard.getId(), "leaderboard", leaderboardStr);
		} catch (JsonProcessingException e) {
			log.error("Could not stream update to leaderboard. "+leaderboard, e);
		}
	}

	public void optout(String guid) {
		userService.optout(guid);
	}
}