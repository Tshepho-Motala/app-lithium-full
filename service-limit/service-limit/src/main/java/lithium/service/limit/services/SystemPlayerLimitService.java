package lithium.service.limit.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.data.objects.ChangeLogLimit;
import lithium.service.limit.data.repositories.DomainLimitRepository;
import lithium.service.limit.data.repositories.PlayerLimitRepository;
import lithium.service.limit.data.repositories.UserRepository;
import lithium.service.limit.enums.ModifyType;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SystemPlayerLimitService {

    private final AgeLimitService ageLimitService;
    private final PlayerLimitService playerLimitService;
    private final DomainLimitRepository domainLimitRepo;
    private final PlayerLimitRepository playerLimitRepo;
    private final ChangeLogService changeLogService;
    private final PubSubUserAccountChangeProxy pubSubUserAccountChangeProxy;
    private final ModelMapper mapper;
    private final UserRepository userRepository;

    @Autowired
    public SystemPlayerLimitService(
            AgeLimitService ageLimitService,
            PlayerLimitService playerLimitService,
            DomainLimitRepository domainLimitRepo,
            PlayerLimitRepository playerLimitRepo,
            ChangeLogService changeLogService,
            PubSubUserAccountChangeProxy pubSubUserAccountChangeProxy,
            ModelMapper mapper,
            UserRepository userRepository
        ) {
        this.ageLimitService = ageLimitService;
        this.playerLimitService = playerLimitService;
        this.domainLimitRepo = domainLimitRepo;
        this.playerLimitRepo = playerLimitRepo;
        this.changeLogService = changeLogService;
        this.pubSubUserAccountChangeProxy = pubSubUserAccountChangeProxy;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }


    public List<PlayerLimit> setUserLimit(User user) {
        Long id = user.getId();
        String guid = user.getGuid();
        String domain = user.getDomain().getName();
        List<PlayerLimit> playerLimits = new ArrayList<>();
        List<lithium.service.limit.data.entities.DomainAgeLimit> limitsInRange = new ArrayList<>();
        Integer userAge = userAgeCalculator(user);
        List<lithium.service.limit.data.entities.DomainAgeLimit> ageLimits = ageLimitService.isWithinAgeRange(userAge, domain, limitsInRange);

        if (!ageLimits.isEmpty()) {

            ageLimits.forEach(domainAgeLimit -> {
                try {
                    PlayerLimit playerLimit = savePlayerLimit(guid, id, domainAgeLimit.getGranularity(), domainAgeLimit.getAmount(),
                            domainAgeLimit.getType(), domain);

                    playerLimits.add(playerLimit);
                } catch (Exception e) {
                    log.error("Problem setting system limit service", e);
                }
            });
            return playerLimits;
        }

        lithium.service.limit.data.entities.DomainLimit domainLimitDay = domainLimitRepo.findByDomainNameAndGranularityAndType(domain, 3, 2);
        lithium.service.limit.data.entities.DomainLimit domainLimitWeek = domainLimitRepo.findByDomainNameAndGranularityAndType(domain, 4, 2);
        lithium.service.limit.data.entities.DomainLimit domainLimitMonth = domainLimitRepo.findByDomainNameAndGranularityAndType(domain, 2, 2);

        if (domainLimitDay != null) {
            try {
                playerLimits.add(storeDomainLimit(domainLimitDay, guid, id));
            } catch (Exception n) {
                log.error("Problem setting system limit service on daily limit", n);
            }
        }

        if (domainLimitWeek != null) {
            try {
                playerLimits.add(storeDomainLimit(domainLimitWeek, guid, id));
            } catch (Exception n) {
                log.error("Problem setting system limit service on weekly limit", n);
            }
        }

        if (domainLimitMonth != null) {
            try {
                playerLimits.add(storeDomainLimit(domainLimitMonth, guid, id));
            } catch (Exception n) {
                log.error("Problem setting system limit service on monthly limit", n);
            }
        }

        return playerLimits;
    }

    private Integer userAgeCalculator(User user) {

        if (user.getDobDay() == null || user.getDobYear() == null || user.getDobMonth() == null) {
            log.warn("User : {} , age is not set", user.guid());
            return null;
        }

        LocalDate l = LocalDate.of(user.getDobYear(), user.getDobMonth(), user.getDobDay());
        LocalDate now = LocalDate.now();
        Period diff = Period.between(l, now);

        return diff.getYears();
    }

    private PlayerLimit storeDomainLimit(lithium.service.limit.data.entities.DomainLimit domainLimit, String guid, Long id) throws Exception {
        return savePlayerLimit(guid, id, domainLimit.getGranularity(), domainLimit.getAmount(),
                domainLimit.getType(), domainLimit.getDomainName());
    }

    public PlayerLimit savePlayerLimit(final String playerGuid, final Long playerId, final int granularity,
                                       final long amount, final int type, final String domainName) throws Exception {
        lithium.service.limit.data.entities.PlayerLimit playerLimit = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid, granularity, type);

        if (playerLimit == null && amount <= 0) {
            return null;
        }

        if (amount > 0 && playerLimit == null) {
            playerLimit = lithium.service.limit.data.entities.PlayerLimit.builder()
                    .playerGuid(playerGuid)
                    .granularity(granularity)
                    .amount(amount)
                    .type(type)
                    .domainName(domainName)
                    .build();
            playerLimit = playerLimitRepo.save(playerLimit);
            playerLimitService.addHistory(playerLimit, ModifyType.CREATED, User.SYSTEM_GUID);

            ChangeLogLimit changeLogLimit = playerLimitService.convertToCLLimit(playerLimit);

            List<ChangeLogFieldChange> clfc = changeLogService.copy(changeLogLimit, new ChangeLogLimit(), new String[]{"playerGuid", "domainName", "granularity", "amount", "type"});
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.limit", "create", playerId, User.SYSTEM_GUID, null,
                    null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 40, domainName);
            pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
            return mapper.map(playerLimit, PlayerLimit.class);
        }
        log.error("Player limit save failure. playerGuid: " + playerGuid + " granularity: " + granularity + " amount: " + amount + " type: " + type);
        return null;
    }

    public lithium.service.limit.client.objects.User getLossLimitVisibility(String playerGuid) {
        lithium.service.limit.data.entities.User player = userRepository.findByGuid(playerGuid);
        return mapper.map(userRepository.save(player), lithium.service.limit.client.objects.User.class);
    }
    public lithium.service.limit.client.objects.User setLossLimitVisibility(String playerGuid, LossLimitsVisibility visibility) {
        return playerLimitService.setLossLimitVisibility(playerGuid, visibility);
    }
}
