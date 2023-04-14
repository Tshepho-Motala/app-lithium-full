package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.accounting.client.AccountingPeriodClient;
import lithium.service.accounting.client.AccountingPlayerClient;
import lithium.service.accounting.client.AccountingSummaryAccountClient;
import lithium.service.accounting.objects.Period;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitType;
import lithium.service.limit.client.exceptions.Status475AnnualLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.limit.client.objects.PlayerLimitV2Dto;
import lithium.service.limit.data.entities.DomainLimit;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.data.entities.PlayerLimitHistory;
import lithium.service.limit.data.entities.User;
import lithium.service.limit.data.objects.ChangeLogLimit;
import lithium.service.limit.data.repositories.DomainLimitRepository;
import lithium.service.limit.data.repositories.PlayerLimitHistoryRepository;
import lithium.service.limit.data.repositories.PlayerLimitRepository;
import lithium.service.limit.data.repositories.UserRepository;
import lithium.service.limit.enums.ModifyType;
import lithium.service.user.threshold.client.enums.LossLimitVisibilityMessageType;
import lithium.service.user.threshold.client.LossLimitVisibilitySystemClient;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static lithium.client.changelog.objects.ChangeLogFieldChange.formatCurrencyFields;

/**
 * The functionality in this service deals with player and domain loss/win limits.
 */
@Service
@Slf4j
public class PlayerLimitService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DomainLimitRepository domainLimitRepo;
    @Autowired
    private PlayerLimitRepository playerLimitRepo;
    @Autowired
    private PlayerLimitHistoryRepository historyRepository;
    @Autowired
    private LithiumServiceClientFactory services;
    @Autowired
    private ChangeLogService changeLogService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CachingDomainClientService domainService;
    @Autowired
    private PubSubUserAccountChangeProxy pubSubUserAccountChangeProxy;
    @Autowired
    private LithiumTokenUtilService tokenService;
    @Autowired
    private PlayerTimeSlotLimitService timeSlotLimitService;
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    public Long netLossToHouse(String domainName, String playerGuid, String currency, Integer granularity) {
        try {
            //TODO: maybe split this out and do caching to save some time on period lookups and player limit lookups
            AccountingPeriodClient periodClient = getAccountingPeriodClient().get();
            Period period = periodClient.findByOffset(domainName, granularity, 0).getData();
            AccountingPlayerClient playerClient = getAccountingPlayerClient().get();
            Long netLoss = playerClient.findNetLossToHouse(domainName, period.getId(), currency, playerGuid).getData(); // bet - win (positive value indicates net loss)
            return netLoss;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void checkLimits(final String playerGuid, final String domainName, final String currency,
                            final Long betAmountCents, final Locale locale)
            throws Status492DailyLossLimitReachedException, Status494DailyWinLimitReachedException,
            Status493MonthlyLossLimitReachedException, Status495MonthlyWinLimitReachedException,
            Status484WeeklyLossLimitReachedException, Status485WeeklyWinLimitReachedException,
            Status478TimeSlotLimitException {

        log.debug("LimitsService.checkLimits [playerGuid=" + playerGuid + ", domainName=" + domainName
                + ", currency=" + currency + ", betAmountCents=" + betAmountCents + ", locale=" + locale + "]");

        timeSlotLimitService.checkLimits(playerGuid, domainName, "bet");

        Long netLossDay = netLossToHouse(domainName, playerGuid, currency, Granularity.GRANULARITY_DAY.granularity());
        Long netLossWeek = netLossToHouse(domainName, playerGuid, currency, Granularity.GRANULARITY_WEEK.granularity());
        Long netLossMonth = netLossToHouse(domainName, playerGuid, currency, Granularity.GRANULARITY_MONTH.granularity());
        Long netLossYear = netLossToHouse(domainName, playerGuid, currency, Granularity.GRANULARITY_YEAR.granularity());

        verifyLossLimits(playerGuid, domainName, currency, locale, betAmountCents, netLossDay, netLossWeek, netLossMonth, netLossYear);

        verifyWinLimits(playerGuid, domainName, currency, locale, netLossDay, netLossWeek, netLossMonth);
    }

    private void verifyWinLimits(String playerGuid, String domainName, String currency, Locale locale, Long netLossDay,
                                 Long netLossWeek, Long netLossMonth)
            throws Status494DailyWinLimitReachedException,
            Status485WeeklyWinLimitReachedException,
            Status495MonthlyWinLimitReachedException {

        PlayerLimit playerWinLimitDay = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_WIN_LIMIT.type());
        PlayerLimit playerWinLimitWeek = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_WIN_LIMIT.type());
        PlayerLimit playerWinLimitMonth = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_WIN_LIMIT.type());

        DomainLimit domainWinLimitDay = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName,
                Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_WIN_LIMIT.type());
        DomainLimit domainWinLimitWeek = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName,
                Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_WIN_LIMIT.type());
        DomainLimit domainWinLimitMonth = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName,
                Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_WIN_LIMIT.type());

        // Win limit (day)
        if (playerWinLimitDay != null && netLossDay <= (playerWinLimitDay.getAmount() * -1)) {
            throw new Status494DailyWinLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.DAILY_WIN_LIMIT_REACHED", new Object[]{
                            toCurrency(playerWinLimitDay.getAmount(), currency)}, locale)
            );
        } else if (domainWinLimitDay != null && playerWinLimitDay == null
                && netLossDay <= (domainWinLimitDay.getAmount() * -1)) {
            throw new Status494DailyWinLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.DAILY_WIN_LIMIT_REACHED", new Object[]{
                            toCurrency(domainWinLimitDay.getAmount(), currency)}, locale)
            );
        }

        // Win limit (week)
        if (playerWinLimitWeek != null && netLossWeek <= (playerWinLimitWeek.getAmount() * -1)) {
            throw new Status485WeeklyWinLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.WEEKLY_WIN_LIMIT_REACHED", new Object[]{
                            toCurrency(playerWinLimitWeek.getAmount(), currency)}, locale)
            );
        } else if (domainWinLimitWeek != null && playerWinLimitWeek == null
                && netLossWeek <= (domainWinLimitWeek.getAmount() * -1)) {
            throw new Status485WeeklyWinLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.WEEKLY_WIN_LIMIT_REACHED", new Object[]{
                            toCurrency(domainWinLimitWeek.getAmount(), currency)}, locale)
            );
        }

        // Win limit (month)
        if (playerWinLimitMonth != null && netLossMonth <= (playerWinLimitMonth.getAmount() * -1)) {
            throw new Status495MonthlyWinLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.MONTHLY_WIN_LIMIT_REACHED", new Object[]{
                            toCurrency(playerWinLimitMonth.getAmount(), currency)}, locale)
            );
        } else if (domainWinLimitMonth != null && playerWinLimitMonth == null
                && netLossMonth <= (domainWinLimitMonth.getAmount() * -1)) {
            throw new Status495MonthlyWinLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.MONTHLY_WIN_LIMIT_REACHED", new Object[]{
                            toCurrency(domainWinLimitMonth.getAmount(), currency)}, locale)
            );
        }
    }

    private void verifyLossLimits(String playerGuid, String domainName, String currency, Locale locale,
                                  Long betAmountCents, Long netLossDay, Long netLossWeek, Long netLossMonth, Long netLossYear)
            throws Status492DailyLossLimitReachedException,
            Status484WeeklyLossLimitReachedException,
            Status493MonthlyLossLimitReachedException,
            Status475AnnualLossLimitReachedException {

        long currentLoss = 0;
        if (betAmountCents != null) {
            // Assume current bet is going to be a loss
            currentLoss = betAmountCents;
        }
        // No need to check loss limits if there is no currentLoss
        if (currentLoss <= 0) return;

        PlayerLimit playerLossLimitDay = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_LOSS_LIMIT.type());
        PlayerLimit playerLossLimitWeek = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_LOSS_LIMIT.type());
        PlayerLimit playerLossLimitMonth = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_LOSS_LIMIT.type());
        PlayerLimit playerLossLimitYear = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_YEAR.granularity(), LimitType.TYPE_LOSS_LIMIT.type());

        DomainLimit domainLossLimitDay = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName,
                Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_LOSS_LIMIT.type());
        DomainLimit domainLossLimitWeek = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName,
                Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_LOSS_LIMIT.type());
        DomainLimit domainLossLimitMonth = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName,
                Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_LOSS_LIMIT.type());
        DomainLimit domainLossLimitYear = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName,
                Granularity.GRANULARITY_YEAR.granularity(), LimitType.TYPE_LOSS_LIMIT.type());

        // Loss limit (day)
            if (playerLossLimitDay != null
                && (netLossDay + currentLoss) > playerLossLimitDay.getAmount()) {
                setLossLimitVisibility(playerGuid);
                sendLossLimitVisibilityNotifications(playerGuid,LossLimitVisibilityMessageType.LOSS_LIMIT_REACHED);
                throw new Status492DailyLossLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.DAILY_LOSS_LIMIT_REACHED", new Object[]{
                        toCurrency(playerLossLimitDay.getAmount(), currency)}, locale)
                );
            } else if (domainLossLimitDay != null && playerLossLimitDay == null
                && (netLossDay + currentLoss) > domainLossLimitDay.getAmount()) {
                setLossLimitVisibility(playerGuid);
                sendLossLimitVisibilityNotifications(playerGuid,LossLimitVisibilityMessageType.LOSS_LIMIT_REACHED);
                throw new Status492DailyLossLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.DAILY_LOSS_LIMIT_REACHED", new Object[]{
                            toCurrency(domainLossLimitDay.getAmount(), currency)}, locale)
            );
        }

            // Loss limit (week)
            if (playerLossLimitWeek != null
                && (netLossWeek + currentLoss) > playerLossLimitWeek.getAmount()) {
                setLossLimitVisibility(playerGuid);
                sendLossLimitVisibilityNotifications(playerGuid,LossLimitVisibilityMessageType.LOSS_LIMIT_REACHED);
                throw new Status484WeeklyLossLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.WEEKLY_LOSS_LIMIT_REACHED",
                        new Object[]{
                            toCurrency(playerLossLimitWeek.getAmount(), currency)}, locale)
                );
            } else if (domainLossLimitWeek != null && playerLossLimitWeek == null
                && (netLossWeek + currentLoss) > domainLossLimitWeek.getAmount()) {
                setLossLimitVisibility(playerGuid);
                sendLossLimitVisibilityNotifications(playerGuid,LossLimitVisibilityMessageType.LOSS_LIMIT_REACHED);
                throw new Status484WeeklyLossLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.WEEKLY_LOSS_LIMIT_REACHED",
                        new Object[]{
                            toCurrency(domainLossLimitWeek.getAmount(), currency)}, locale)
                );
            }

            // Loss limit (month)
            if (playerLossLimitMonth != null
                && (netLossMonth + currentLoss) > playerLossLimitMonth.getAmount()) {
                setLossLimitVisibility(playerGuid);
                sendLossLimitVisibilityNotifications(playerGuid,LossLimitVisibilityMessageType.LOSS_LIMIT_REACHED);
                throw new Status493MonthlyLossLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.MONTHLY_LOSS_LIMIT_REACHED",
                        new Object[]{
                            toCurrency(playerLossLimitMonth.getAmount(), currency)}, locale)
                );
            } else if (domainLossLimitMonth != null && playerLossLimitMonth == null
                && (netLossMonth + currentLoss) > domainLossLimitMonth.getAmount()) {
                setLossLimitVisibility(playerGuid);
                sendLossLimitVisibilityNotifications(playerGuid,LossLimitVisibilityMessageType.LOSS_LIMIT_REACHED);
                throw new Status493MonthlyLossLimitReachedException(
                    messageSource.getMessage("SERVICE-LIMIT.MONTHLY_LOSS_LIMIT_REACHED",
                        new Object[]{
                            toCurrency(domainLossLimitMonth.getAmount(), currency)}, locale)
                );
            }
    }
    private void setLossLimitVisibility(String playerGuid){
        LossLimitsVisibility losslimitsvisibility = getLossLimitVisibility(playerGuid)
            .getLossLimitsVisibility();
        if (losslimitsvisibility == LossLimitsVisibility.DISABLED) {
            setLossLimitVisibility(playerGuid,LossLimitsVisibility.ENABLED);
        }
    }

    private void sendLossLimitVisibilityNotifications(String playerGuid, LossLimitVisibilityMessageType messageType) {
        LossLimitVisibilitySystemClient client = getLossLimitVisibilitySystemClient();
        try {
            client.sendLossLimitVisibilityNotification(playerGuid, messageType.name());
        } catch (Status500InternalServerErrorException e) {
            throw new RuntimeException(e);
        }
    }

    public DomainLimit findDomainLimit(String domainName, int granularity, int type) {
        return domainLimitRepo.findByDomainNameAndGranularityAndType(domainName, granularity, type);
    }

    public DomainLimit saveDomainLimit(final String domainName, final int granularity, final long amount, final int type, Principal principal) throws Exception {
        Domain domain = domainService.retrieveDomainFromDomainService(domainName);

        DomainLimit domainLimit = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName, granularity, type);
	    LithiumTokenUtil util = tokenService.getUtil(principal);

        if (domainLimit != null && amount <= 0) {
            String[] fields = {"domainName", "granularity", "amount", "type"};
            List<ChangeLogFieldChange> clfc = changeLogService.copy(new DomainLimit(), domainLimit, fields);
            domainLimitRepo.delete(domainLimit);
            changeLogService.registerChangesForNotesWithFullNameAndDomain("domain.limit", "delete", domain.getId(), util.guid(),
                    util, null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 0, domainName);
            return null;
        }

        if (domainLimit == null && amount <= 0) {
            return null;
        }

        if (domainLimit != null && amount > 0) {
            DomainLimit oldDomainLimit = new DomainLimit();
            oldDomainLimit.setAmount(domainLimit.getAmount());
            domainLimit.setAmount(amount);
            domainLimit = domainLimitRepo.save(domainLimit);
            String[] fields = {"amount"};
            List<ChangeLogFieldChange> clfc = changeLogService.copy(domainLimit, oldDomainLimit, fields);
	        formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());
	        changeLogService.registerChangesForNotesWithFullNameAndDomain("domain.limit", "edit", domain.getId(), util.guid(),
			        util, null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 0, domainName);
            return domainLimit;
        }

        if (domainLimit == null && amount > 0) {
            domainLimit = DomainLimit.builder()
                    .domainName(domainName)
                    .granularity(granularity)
                    .amount(amount)
                    .type(type)
                    .build();
            domainLimit = domainLimitRepo.save(domainLimit);
            String[] fields = {"domainName", "granularity", "amount", "type"};
            List<ChangeLogFieldChange> clfc = changeLogService.copy(domainLimit, new DomainLimit(), fields);
	        formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());
	        changeLogService.registerChangesForNotesWithFullNameAndDomain("domain.limit", "create", domain.getId(), util.guid(),
			        util, null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 0, domainName);
            return domainLimit;
        }
        log.error("Domain limit save failure. domainName: " + domainName + " granularity: " + granularity + " amount: " + amount + " type: " + type);
        return null;
    }

    public void removeDomainLimit(String domainName, int granularity, int type, Principal principal) throws Exception {
        Domain domain = domainService.retrieveDomainFromDomainService(domainName);
        DomainLimit domainLimit = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName, granularity, type);
        List<ChangeLogFieldChange> clfc = changeLogService.compare(new DomainLimit(), domainLimit, new String[]{"domainName", "granularity", "amount", "type"});
	    formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());
        changeLogService.registerChangesForNotesWithFullNameAndDomain("domain.limit", "delete", domain.getId(), principal.getName(),
                tokenService.getUtil(principal), null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 0, domainName);
        domainLimitRepo.deleteByDomainNameAndGranularityAndType(domainName, granularity, type);
    }

    public PlayerLimit findPlayerLimit(String playerGuid, int granularity, int type) {
        log.debug(playerGuid + " :: " + granularity + " :: " + type);
        PlayerLimit findPlayerLimit = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid, granularity, type);
        log.debug("PlayerLimit : " + findPlayerLimit);
        return findPlayerLimit;
    }

    public PlayerLimitV2Dto findPlayerLimitV2WithNetLoss(String domainName, String playerGuid, int granularity, int type) {
        log.debug("findPlayerLimitV2WithNetLoss for {} - g:{}, t:{}", playerGuid, granularity, type);
        String currency = domainService.getDefaultDomainCurrency(domainName);

        Long limitAmountCents = null;
        Long netLossToHouse = netLossToHouse(domainName, playerGuid, currency, granularity);

        PlayerLimit playerLimit = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid, granularity, type);
        if (playerLimit == null) {
            DomainLimit domainLimit = domainLimitRepo.findByDomainNameAndGranularityAndType(domainName, granularity, type);
            if (domainLimit != null) {
                limitAmountCents = domainLimit.getAmount();
            }
        } else {
            limitAmountCents = playerLimit.getAmount();
        }
        PlayerLimitV2Dto dto = PlayerLimitV2Dto.builder()
            .playerGuid(playerGuid)
            .domainName(domainName)
            .type(type)
            .granularity(granularity)
            .limitAmount((limitAmountCents!=null)?CurrencyAmount.fromCents(limitAmountCents).toAmount():null)
            .netLossAmount((netLossToHouse!=null)?CurrencyAmount.fromCents(netLossToHouse).toAmount():null)
            .build();
        log.debug("PlayerLimitV2Dto: {}", dto);
        return dto;
    }

    public PlayerLimit savePlayerLimit(final String playerGuid, final Long playerId, final int granularity,
                                       final long amount, final int type, final String domainName,
                                       LithiumTokenUtil tokenUtil, boolean isMigration, String authorGuid) throws Exception {
        Domain domain = domainService.retrieveDomainFromDomainService(domainName);
        PlayerLimit playerLimit = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid, granularity, type);
        if (playerLimit != null && amount <= 0) {
            String[] fields = {"playerGuid", "domainName", "granularity", "amount", "type"};
            ChangeLogLimit changeLogLimit = convertToCLLimit(playerLimit);
            List<ChangeLogFieldChange> clfc = changeLogService.copy(new ChangeLogLimit(), changeLogLimit, fields);
            formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());
            playerLimitRepo.delete(playerLimit);

            addHistory(playerLimit, ModifyType.REMOVED, authorGuid);

            if(!isMigration){
                changeLogService.registerChangesForNotesWithFullNameAndDomain("user.limit", "delete", playerId, authorGuid, tokenUtil,
                        null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 40, domainName);
                pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
            }
            return null;
        }

        if (playerLimit == null && amount <= 0) {
            return null;
        }

        if (playerLimit != null && amount > 0) {
            PlayerLimit oldPlayerLimit = new PlayerLimit();
            oldPlayerLimit.setAmount(playerLimit.getAmount());
            playerLimit.setAmount(amount);
            playerLimit = playerLimitRepo.save(playerLimit);

            addHistory(playerLimit, ModifyType.UPDATED, authorGuid);

            ChangeLogLimit oldChangeLogLimit = convertToCLLimit(oldPlayerLimit);
            ChangeLogLimit changeLogLimit = convertToCLLimit(playerLimit);

            String[] fields = {"granularity", "amount", "type"};
            List<ChangeLogFieldChange> clfc = changeLogService.copy(changeLogLimit, oldChangeLogLimit, fields);
            formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());

            if(!isMigration){
                changeLogService.registerChangesForNotesWithFullNameAndDomain("user.limit", "edit", playerId, authorGuid, tokenUtil,
                        null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 40, domainName);
                pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
            }
            return playerLimit;
        }

        if (playerLimit == null && amount > 0) {
            playerLimit = PlayerLimit.builder()
                    .playerGuid(playerGuid)
                    .granularity(granularity)
                    .amount(amount)
                    .type(type)
                    .domainName(domainName)
                    .build();
            playerLimit = playerLimitRepo.save(playerLimit);

            addHistory(playerLimit, ModifyType.CREATED, authorGuid);

            ChangeLogLimit changeLogLimit = convertToCLLimit(playerLimit);
            String[] fields = {"playerGuid", "domainName", "granularity", "amount", "type"};
            List<ChangeLogFieldChange> clfc = changeLogService.copy(changeLogLimit, new ChangeLogLimit(), fields);
            formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());

            if(!isMigration){
                changeLogService.registerChangesForNotesWithFullNameAndDomain("user.limit", "create", playerId, authorGuid, tokenUtil,
                        null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 40, domainName);
                pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
            }
            return playerLimit;
        }
        log.error("Player limit save failure. playerGuid: " + playerGuid + " granularity: " + granularity + " amount: " + amount + " type: " + type);
        return null;
    }

    public ChangeLogLimit convertToCLLimit(PlayerLimit playerLimit) {

        BigDecimal amount = BigDecimal.valueOf(playerLimit.getAmount(), 2);

        ChangeLogLimit changeLogLimit = ChangeLogLimit.builder()
                .playerGuid(playerLimit.getPlayerGuid() == null ? "" : playerLimit.getPlayerGuid())
                .domainName(playerLimit.getDomainName() == null ? "" : playerLimit.getDomainName())
                .granularity(playerLimit.getGranularity() == 0 ? "" : Granularity.fromGranularity(playerLimit.getGranularity()).type())
                .type(playerLimit.getType() == 0 ? "" : LimitType.fromType(playerLimit.getType()).name())
                .amount(String.valueOf(amount))
                .build();
        return changeLogLimit;
    }

    public void removePlayerLimit(String playerGuid, Long playerId, int granularity, int type,
                                  LithiumTokenUtil tokenUtil) throws Exception {
        PlayerLimit playerLimit = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid, granularity, type);
	    Domain domain = domainService.retrieveDomainFromDomainService(playerLimit.getDomainName());
        String[] fields = {"playerGuid", "domainName", "granularity", "amount", "type"};
        ChangeLogLimit changeLogLimit = convertToCLLimit(playerLimit);

        List<ChangeLogFieldChange> clfc = changeLogService.compare(new ChangeLogLimit(), changeLogLimit, fields);
	    formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());
        playerLimitRepo.deleteByPlayerGuidAndGranularityAndType(playerGuid, granularity, type);

        addHistory(playerLimit, ModifyType.REMOVED, tokenUtil.guid());

        SubCategory sub = SubCategory.DEPOSIT_LIMITS;
        if (changeLogLimit.getType().equals(LimitType.TYPE_DEPOSIT_LIMIT.name())) {
            sub = SubCategory.DEPOSIT_LIMITS;
        } else if (changeLogLimit.getType().equals(LimitType.TYPE_LOSS_LIMIT.name())) {
            sub = SubCategory.LOSS_LIMITS;
        }

        changeLogService.registerChangesForNotesWithFullNameAndDomain("user.limit", "delete", playerId, tokenUtil.guid(), tokenUtil,
                null, null, clfc, Category.RESPONSIBLE_GAMING, sub, 40, playerLimit.getDomainName());
        pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
    }

    private String toCurrency(Long amountCents, String currency) {
        BigDecimal bd = new BigDecimal(amountCents);
        bd = bd.movePointLeft(2);

        return currency + " " + bd.toPlainString();
    }

    public void addHistory(PlayerLimit playerLimit, ModifyType modifyType, String authorGuid) {
        PlayerLimitHistory plh = historyRepository.save(
                PlayerLimitHistory.builder()
                        .amount(playerLimit.getAmount())
                        .granularity(playerLimit.getGranularity())
                        .playerGuid(playerLimit.getPlayerGuid())
                        .type(playerLimit.getType())
                        .modifyType(modifyType)
                        .modifyAuthorGuid(authorGuid)
                        .build()
        );
        log.debug("Saved player limit history:: " + plh);
    }

    private Optional<AccountingPeriodClient> getAccountingPeriodClient() {
        return getClient(AccountingPeriodClient.class, "service-accounting-provider-internal");
    }

    private Optional<AccountingSummaryAccountClient> getAccountingSummaryAccountClient() {
        return getClient(AccountingSummaryAccountClient.class, "service-accounting-provider-internal");
    }

    private Optional<AccountingPlayerClient> getAccountingPlayerClient() {
        return getClient(AccountingPlayerClient.class, "service-accounting-provider-internal");
    }

    private <E> Optional<E> getClient(Class<E> theClass, String url) {
        E clientInstance = null;

        try {
            clientInstance = services.target(theClass, url, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
        }
        return Optional.ofNullable(clientInstance);

    }

    public List<PlayerLimit> fetchPlayerLossLimits(final String playerGuid) {
        List<PlayerLimit> playerLimits = new ArrayList<>();

        PlayerLimit playerLossLimitDay = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_LOSS_LIMIT.type());
        PlayerLimit playerLossLimitWeek = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_LOSS_LIMIT.type());
        PlayerLimit playerLossLimitMonth = playerLimitRepo.findByPlayerGuidAndGranularityAndType(playerGuid,
                Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_LOSS_LIMIT.type());

        if (playerLossLimitDay != null) playerLimits.add(playerLossLimitDay);
        if (playerLossLimitWeek != null) playerLimits.add(playerLossLimitWeek);
        if (playerLossLimitMonth != null) playerLimits.add(playerLossLimitMonth);

        return playerLimits;
    }

    public lithium.service.limit.client.objects.User getLossLimitVisibility(String playerGuid) {
        lithium.service.limit.data.entities.User player = userRepository.findByGuid(playerGuid);
        if(player == null){
            player = userRepository.findOrCreateByGuid(playerGuid, () -> new User());
        }
        return mapper.map(player, lithium.service.limit.client.objects.User.class);
    }
    public lithium.service.limit.client.objects.User setLossLimitVisibility(String playerGuid, LossLimitsVisibility visibility) {
        lithium.service.limit.data.entities.User player = userRepository.findByGuid(playerGuid);
        player.setLossLimitsVisibility(visibility);
        if (visibility == LossLimitsVisibility.ENABLED) {
            sendLossLimitVisibilityNotifications(playerGuid, LossLimitVisibilityMessageType.VISIBILITY_ACTIVATED);
        }
        return mapper.map(userRepository.save(player), lithium.service.limit.client.objects.User.class);
    }

    private LossLimitVisibilitySystemClient getLossLimitVisibilitySystemClient() {
        LossLimitVisibilitySystemClient cl = null;
        try {
            cl = lithiumServiceClientFactory.target(LossLimitVisibilitySystemClient.class, "service-user-threshold", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting player limit system service", e);
        }
        return cl;
    }

    public PlayerLimit savePlayerLimitMigration(final String playerGuid, final Long playerId, final int granularity,
                                                final long amount, final int type, final String domainName)
            throws Exception{
        return savePlayerLimit(playerGuid, playerId,
                granularity, amount, type, domainName, null, true, "system");
    }


}
