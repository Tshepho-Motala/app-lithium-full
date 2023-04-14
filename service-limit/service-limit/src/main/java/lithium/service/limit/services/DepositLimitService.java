package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.cashier.client.CashierInternalClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.LimitType;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status477BalanceLimitReachedException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status479DepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status480PendingDepositLimitException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.exceptions.Status486DailyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status487WeeklyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status488MonthlyDepositLimitReachedException;
import lithium.service.limit.client.exceptions.Status498SupposedDepositLimitException;
import lithium.service.limit.client.exceptions.Status499EmptySupposedDepositLimitException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.PlayerLimitFE;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.data.entities.PlayerLimitHistory;
import lithium.service.limit.data.repositories.PlayerLimitHistoryRepository;
import lithium.service.limit.data.repositories.PlayerLimitRepository;
import lithium.service.limit.enums.ModifyType;
import lithium.service.translate.client.objects.RestrictionError;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.util.ObjectUtils;

@Service
public class DepositLimitService {

    private Logger log = LoggerFactory.getLogger(DepositLimitService.class);

    @Autowired
    @Setter
    MessageSource messageSource;
    @Autowired
    @Setter
    PlayerLimitRepository playerLimitRepository;
    @Autowired
    @Setter
    PlayerLimitHistoryRepository historyRepository;
    @Autowired
    CachingDomainClientService cachingDomainClientService;
    @Autowired
    @Setter
    ChangeLogService changeLogService;
    @Autowired
    @Setter
    ExternalUserService externalUserService;
    @Autowired
    @Setter
    LithiumServiceClientFactory serviceClientFactory;
    @Autowired
    @Setter
    PubSubUserAccountChangeProxy pubSubUserAccountChangeProxy;
    @Autowired
    UserRestrictionService userRestrictionService;
    @Autowired
    BalanceLimitService balanceLimitService;
    @Autowired
    PlayerTimeSlotLimitService timeSlotLimitService;
    @Autowired
    LimitInternalSystemService limitInternalSystemService;

    @Autowired
    private CachingDomainClientService domainService;

    public PlayerLimit find(String playerGuid, Granularity granularity) {
        return findByPlayerGuidAndGranularityAndType(playerGuid, granularity, LimitType.TYPE_DEPOSIT_LIMIT);
    }

    public PlayerLimit findPending(String playerGuid, Granularity granularity) {
        return findByPlayerGuidAndGranularityAndType(playerGuid, granularity, LimitType.TYPE_DEPOSIT_LIMIT_PENDING);
    }

    public PlayerLimit findSupposed(String playerGuid, Granularity granularity) {
        return findByPlayerGuidAndGranularityAndType(playerGuid, granularity, LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED);
    }

    private PlayerLimit findByPlayerGuidAndGranularityAndType(String playerGuid, Granularity granularity, LimitType limitType) {
        return playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, granularity.granularity(), limitType.type());
    }

    public List<PlayerLimitFE> findAllFE(
            String playerGuid,
            Locale locale
    ) throws
            Status481DomainDepositLimitDisabledException,
            Status500LimitInternalSystemClientException {
        try {

            List<PlayerLimitFE> result = new ArrayList<>();
            result.addAll(findAll(playerGuid, locale).stream().map(this::convert).collect(Collectors.toList()));
            result.addAll(findAllPending(playerGuid, locale).stream().map(this::convert).collect(Collectors.toList()));
            result.addAll(findAllSupposed(playerGuid, locale).stream().map(this::convert).collect(Collectors.toList()));
            return result;
        } catch (Status481DomainDepositLimitDisabledException e) {
            throw e;
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
    }

    private PlayerLimitFE convert(PlayerLimit playerLimit) {
        PlayerLimitFE result = PlayerLimitFE.builder()
                .playerGuid(playerLimit.getPlayerGuid())
                .granularity(Granularity.fromGranularity(playerLimit.getGranularity()).type())
                .amount(CurrencyAmount.fromCents(playerLimit.getAmount()).toAmount())
                .amountUsed(CurrencyAmount.fromCents(playerLimit.getAmountUsed()).toAmount())
                .type(LimitType.fromType(playerLimit.getType()).name())
                .createdDate(new DateTime(playerLimit.getCreatedDate(), DateTimeZone.getDefault()))
                .modifiedDate(new DateTime(playerLimit.getModifiedDate(), DateTimeZone.getDefault()))
                .build();
        if (playerLimit.getType() == LimitType.TYPE_DEPOSIT_LIMIT_PENDING.type()) {
            try {
                result.setAppliedAt(result.getCreatedDate().plusHours(getPendingPeriodFromSettings(playerLimit.getDomainName())));
            } catch (Status550ServiceDomainClientException e) {
                log.error(e.getMessage());
            }
        }

        return result;
    }

    public Integer getPendingPeriodFromSettings(String domainName) throws Status550ServiceDomainClientException {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        Optional<String> labelValue = domain.findDomainSettingByName(DomainSettings.DEPOSIT_LIMIT_PENDING_PERIODS_IN_HOURS.key());
        String result = labelValue.orElse(DomainSettings.DEPOSIT_LIMIT_PENDING_PERIODS_IN_HOURS.defaultValue());
        return Integer.valueOf(result);
    }

    private void domainPlayerDepositLimitsEnabled(String domainName, Locale locale) throws Status550ServiceDomainClientException, Status481DomainDepositLimitDisabledException {
        Domain domain = domainFromServiceDomain(domainName);
        if (!domain.getPlayerDepositLimits())
            throw new Status481DomainDepositLimitDisabledException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.DOMAIN_DEPOSIT_LIMIT_DISABLED", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Player Deposit Limits for this domain is disabled.", LocaleContextHolder.getLocale()));
    }

    public Page<PlayerLimit> findAllPending(PageRequest pageRequest) {
        return playerLimitRepository.findByType(LimitType.TYPE_DEPOSIT_LIMIT_PENDING.type(), pageRequest);
    }

    public List<PlayerLimit> findAllPending(String playerGuid, Locale locale) throws Status550ServiceDomainClientException, Status481DomainDepositLimitDisabledException {
        domainPlayerDepositLimitsEnabled(playerGuid.split("/")[0], locale);
        return Arrays.asList(
                        playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_PENDING.type()),
                        playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_PENDING.type()),
                        playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_PENDING.type())

                ).parallelStream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<PlayerLimit> findAllSupposed(String playerGuid, Locale locale) throws Status550ServiceDomainClientException, Status481DomainDepositLimitDisabledException {
        domainPlayerDepositLimitsEnabled(playerGuid.split("/")[0], locale);
        return Arrays.asList(
                        playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED.type()),
                        playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED.type()),
                        playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED.type())
                ).parallelStream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean allowedToDeposit(
            String playerGuid,
            Long amountCents,
            String localeStr
    ) throws
            Status477BalanceLimitReachedException,
            Status478TimeSlotLimitException,
            Status486DailyDepositLimitReachedException,
            Status487WeeklyDepositLimitReachedException,
            Status488MonthlyDepositLimitReachedException,
            Status500LimitInternalSystemClientException,
            Status438PlayTimeLimitReachedException {
        try {
            String domainName = playerGuid.split("/")[0];
            Domain domain = domainFromServiceDomain(domainName);
            if (localeStr == null || localeStr.isEmpty()) localeStr = domain.getDefaultLocale();
            timeSlotLimitService.checkLimits(playerGuid, domainName, "deposit");

            localeStr = localeStr.replaceAll("\\_", "-");
            Locale locale = new Locale(localeStr.split("-")[0], localeStr.split("-")[1]);
            List<PlayerLimit> playerLimitsToCheck = new ArrayList<>();
            try {
                playerLimitsToCheck.addAll(findAll(playerGuid, locale));
            } catch (Status481DomainDepositLimitDisabledException e) {
                log.info("Deposit limit disabled for current domain (" + playerGuid + ")");
            }
            if (domain.getPlayerBalanceLimit()) {
                PlayerLimit balanceLimit = balanceLimitService.findCurrent(playerGuid);
                if (Objects.nonNull(balanceLimit)) {
                    playerLimitsToCheck.add(balanceLimit);
                }
            }
            for (PlayerLimit pl : playerLimitsToCheck) {
                if ((pl.getId() != null) && ((pl.getAmount() >= 0) && ((pl.getAmountUsed() + amountCents) > pl.getAmount()))) {
                    BigDecimal preLimitAmount = (BigDecimal.valueOf(pl.getAmount() - pl.getAmountUsed()));
                    if (preLimitAmount.compareTo(BigDecimal.ZERO) <= 0) preLimitAmount = BigDecimal.ZERO;
                    String formattedPreLimitAmount = CurrencyAmount.formatUsingLocale(preLimitAmount.longValue(), locale, domain.getCurrencySymbol(), domain.getCurrency());
                    if (LimitType.TYPE_BALANCE_LIMIT.type() == pl.getType()) {
                        throw new Status477BalanceLimitReachedException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.REACHED_BALANCE_LIMIT", new Object[]{formattedPreLimitAmount, ""}, locale));
                    }
                    switch (Granularity.fromGranularity(pl.getGranularity())) {
                        case GRANULARITY_DAY:
                            throw new Status486DailyDepositLimitReachedException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.REACHED_LIMIT_DAILY", new Object[]{formattedPreLimitAmount, ""}, locale));
                        case GRANULARITY_WEEK:
                            throw new Status487WeeklyDepositLimitReachedException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.REACHED_LIMIT_WEEKLY", new Object[]{formattedPreLimitAmount, ""}, locale));
                        case GRANULARITY_MONTH:
                            throw new Status488MonthlyDepositLimitReachedException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.REACHED_LIMIT_MONTHLY", new Object[]{formattedPreLimitAmount, ""}, locale));
                        default:
                            throw new Status500LimitInternalSystemClientException(new Exception(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.UNKOWN", null, locale)));
                    }
                }
            }
        } catch (Status486DailyDepositLimitReachedException | Status487WeeklyDepositLimitReachedException | Status488MonthlyDepositLimitReachedException | Status477BalanceLimitReachedException | Status478TimeSlotLimitException e) {
            throw e;
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
        limitInternalSystemService.checkPlayTimeLimit(playerGuid);
        return true;
    }

    // return null    - limits not defined
    //   number value - limited amount
    public BigDecimal getAllowedDepositValue(
            String playerGuid,
            String localeStr
    ) throws
            Status500LimitInternalSystemClientException, Status550ServiceDomainClientException, Status479DepositLimitReachedException, Status478TimeSlotLimitException {
        String domainName = playerGuid.split("/")[0];
        timeSlotLimitService.checkLimits(playerGuid, domainName, "deposit");
        try {
            Access access;
            try {
                access = userRestrictionService.checkAccess(playerGuid);
            } catch (Status500InternalServerErrorException e) {
                log.warn("Cant get access value for player=" + playerGuid + ", Internal error=" + e.getMessage());
                throw new Status479DepositLimitReachedException(e);
            }
            if (access != null && !access.isDepositAllowed()) {
                log.warn("Deposit not allowed for player=" + playerGuid);
                throw new Status479DepositLimitReachedException(RestrictionError.DEPOSIT.getResponseMessageLocal(messageSource, domainName, access.getDepositErrorMessage()));
            }

            localeStr = localeStr.replaceAll("\\_", "-");
            Locale locale = new Locale(localeStr.split("-")[0], localeStr.split("-")[1]);
            List<PlayerLimit> playerLimits = new ArrayList<>();
            try {
                playerLimits.addAll(findAll(playerGuid, locale));
            } catch (Status481DomainDepositLimitDisabledException e) {
                log.info("Deposit limit disabled for current domain (" + playerGuid + ")");
            }
            if (domainFromServiceDomain(domainName).getPlayerBalanceLimit()) {
                PlayerLimit balanceLimit = balanceLimitService.findCurrent(playerGuid);
                if (Objects.nonNull(balanceLimit)) {
                    playerLimits.add(balanceLimit);
                }
            }
            log.debug("Checking limits for player=" + playerGuid);
            BigDecimal maxPreLimitAmount = null;
            for (PlayerLimit pl : playerLimits) {
                if ((pl.getId() != null) && ((pl.getAmount() >= 0))) {
                    BigDecimal preLimitAmount = (BigDecimal.valueOf(pl.getAmount() - pl.getAmountUsed()));
                    if (preLimitAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        // Exceeded the limit
                        log.warn("Exceeded the limit of deposits: giud = [" + playerGuid + "] , type = " + LimitType.fromType(pl.getType()).name() +
                                ", limit = " + pl.getAmount() + " used = " + pl.getAmountUsed());
                        throw new Status500LimitInternalSystemClientException(new Exception("Exceeded the limit of deposits"));
                    }
                    log.debug("Applying limit for player=" + playerGuid + ". Limit=" + pl);

                    if (maxPreLimitAmount == null || preLimitAmount.compareTo(maxPreLimitAmount) < 0) {
                        maxPreLimitAmount = preLimitAmount;
                    }
                }
            }
            return maxPreLimitAmount == null ? maxPreLimitAmount : CurrencyAmount.fromCentsAllowNull(maxPreLimitAmount.longValue()).toAmount();
        } catch (Status500LimitInternalSystemClientException e) {
            log.warn("Terminated calculate deposit limit for user=" + playerGuid + ", Internal error=" + e.getErrorCode());
            throw e;
        }
    }


    public List<PlayerLimit> findAll(String playerGuid, Locale locale) throws Status550ServiceDomainClientException, Status481DomainDepositLimitDisabledException {
        domainPlayerDepositLimitsEnabled(playerGuid.split("/")[0], locale);
        PlayerLimit playerLimit = PlayerLimit.builder().playerGuid(playerGuid).type(LimitType.TYPE_DEPOSIT_LIMIT.type()).build();
        Long pendingAmountCents = getPendingAmount(playerGuid);
        return Arrays.asList(
                        Optional.ofNullable(playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_DEPOSIT_LIMIT.type())).orElse(playerLimit.toBuilder().granularity(Granularity.GRANULARITY_DAY.granularity()).build()),
                        Optional.ofNullable(playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_DEPOSIT_LIMIT.type())).orElse(playerLimit.toBuilder().granularity(Granularity.GRANULARITY_WEEK.granularity()).build()),
                        Optional.ofNullable(playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_DEPOSIT_LIMIT.type())).orElse(playerLimit.toBuilder().granularity(Granularity.GRANULARITY_MONTH.granularity()).build())
                ).parallelStream()
                .filter(Objects::nonNull)
                .map(pl -> {
                    SummaryTransactionType sa = null;
                    try {
                        long summaryAmount = pendingAmountCents;
                        sa = accountingTotals(playerGuid, Granularity.fromGranularity(pl.getGranularity()));
                        if (sa != null) {
                            summaryAmount = summaryAmount + sa.getCreditCents();
                        }
                        pl.setAmountUsed(summaryAmount);
                        log.debug("All limits (" + playerGuid + ") :: g: " + pl.getGranularity() + " - " + pl.getAmountUsed() + " / " + pl.getAmount());
                    } catch (Exception e) {
                    }
                    return pl;
                })
                .collect(Collectors.toList());
    }

    private Long getPendingAmount(String playerGuid) {
        return getCashierInternalClient().get().pendingAmountCents(playerGuid).getData();
    }

    public PlayerLimitFE proceedSupposedLimit(String playerGuid, Granularity granularity, boolean action, Locale locale, LithiumTokenUtil util) throws Status500LimitInternalSystemClientException, Status499EmptySupposedDepositLimitException {
        User player = null;
        try {
            player = externalUserService.findByGuid(playerGuid);
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
        PlayerLimit plSupposed = findSupposed(playerGuid, granularity);
        if (plSupposed == null)
            throw new Status499EmptySupposedDepositLimitException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.EMPTY_SUPPOSED_DEPOSIT_LIMIT", null, locale));
        PlayerLimit plCurrent = find(playerGuid, granularity);
        if (action) {
            try {
                //If the supposed limit is zero then we need to remove the existing limit
                if (plSupposed.getAmount() == 0) {
                    deleteDepositLimit(plCurrent, util.guid());
                    buildChangelogForDelete(plCurrent, plSupposed, player.getId(), util.guid(), util, util.guid());
                } else {
                    String toAmount = CurrencyAmount.fromCents(plSupposed.getAmount()).toAmount().toPlainString();
                    updateAndSave(plCurrent, toAmount, player.getId(), util.guid(), util);
                }

                deleteDepositLimit(plSupposed, util.guid());
                pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);

            } catch (Exception e) {
                throw new Status500LimitInternalSystemClientException(e);
            }

            return convert(plSupposed);

        } else {
            deleteDepositLimit(plSupposed, util.guid());
            return convert(plCurrent);
        }

    }

    //Todo : saveFE method renaming to be considered as recommended by peer reviewer so that its easy to understand its purpose
    public List<PlayerLimitFE> saveFE(
            String playerGuid,
            List<Integer> granularity,
            List<BigDecimal> amount,
            Locale locale,
            LithiumTokenUtil lithiumTokenUtil
    ) throws
            Status100InvalidInputDataException,
            Status481DomainDepositLimitDisabledException,
            Status500LimitInternalSystemClientException,
            Status498SupposedDepositLimitException {
        try {
            save(playerGuid, granularity, amount, playerGuid, locale, lithiumTokenUtil);
        } catch (Status550ServiceDomainClientException e) {
            throw new Status500LimitInternalSystemClientException(e);
        }

        return findAllFE(playerGuid, locale);
    }

    public List<PlayerLimit> save(String playerGuid, List<Integer> granularityValues, List<BigDecimal> amounts, String authorGuid, Locale locale, LithiumTokenUtil lithiumTokenUtil) throws Status100InvalidInputDataException, Status500LimitInternalSystemClientException, Status550ServiceDomainClientException, Status481DomainDepositLimitDisabledException, Status498SupposedDepositLimitException {
        domainPlayerDepositLimitsEnabled(playerGuid.split("/")[0], locale);
        if (Stream.of(playerGuid, granularityValues, amounts).anyMatch(Objects::isNull))
            throw new Status100InvalidInputDataException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.INVALIDINPUT", null, locale));

        User player;
        try {
            player = externalUserService.findByGuid(playerGuid);
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
        ArrayList<Granularity> granularityList = parseRequestedGranularities(granularityValues);
        ArrayList<PlayerLimit> result = new ArrayList<>();

        for (int i = 0; i < granularityList.size(); i++) {
            BigDecimal bigDecimalAmount = amounts.get(i);

            Granularity granularity = granularityList.get(i);
            if (amounts.get(i).compareTo(BigDecimal.ZERO) < 0) {
                throw new Status100InvalidInputDataException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.INVALIDINPUT", null, locale));
            }
            String amount = amounts.get(i).toString();

            if (bigDecimalAmount.compareTo(BigDecimal.ZERO) == 0) {
                amount = BigDecimal.ZERO.toString();
            }

            PlayerLimit plPending = findPending(playerGuid, granularity);
            PlayerLimit plSupposed = findSupposed(playerGuid, granularity);
            PlayerLimit plCurrent = find(playerGuid, granularity);

            if (plPending != null && plCurrent != null) {
                BigDecimal receivedAmount = CurrencyAmount.fromAmountString(amount).toAmount();
                BigDecimal currentAmount = CurrencyAmount.fromCents(plCurrent.getAmount()).toAmount();
                if (receivedAmount.compareTo(currentAmount) <= 0 && !receivedAmount.equals(BigDecimal.ZERO)) {
                    deleteDepositLimit(plPending, authorGuid);
                    deleteDepositLimit(plCurrent, authorGuid);
                    result.add(buildAndSave(playerGuid, amount, granularity, player.getId(), authorGuid, false, lithiumTokenUtil));
                    continue;
                }
                deleteDepositLimit(plPending, authorGuid);
                result.add(buildAndSave(playerGuid, amount, granularity, player.getId(), authorGuid, true, lithiumTokenUtil));
                continue;
            } else if (plSupposed != null && plCurrent != null) {
                String errMsg = "";
                if (granularity.equals(Granularity.GRANULARITY_DAY))
                    errMsg = "SERVICE_LIMIT.DEPOSITLIMIT.ERROR.SUPPOSED_DEP_LIMIT_DAILY";
                else if (granularity.equals(Granularity.GRANULARITY_WEEK))
                    errMsg = "SERVICE_LIMIT.DEPOSITLIMIT.ERROR.SUPPOSED_DEP_LIMIT_WEEKLY";
                else if (granularity.equals(Granularity.GRANULARITY_MONTH))
                    errMsg = "SERVICE_LIMIT.DEPOSITLIMIT.ERROR.SUPPOSED_DEP_LIMIT_MONTHLY";
                throw new Status498SupposedDepositLimitException(messageSource.getMessage(
                        errMsg,
                        new Object[]{new DateTime(plSupposed.getCreatedDate(), DateTimeZone.getDefault()).plusDays(1).toString()},
                        locale
                ));
            } else if (plCurrent != null) {
                BigDecimal receivedAmount = CurrencyAmount.fromAmountString(amount).toAmount();
                BigDecimal savedAmount = CurrencyAmount.fromCents(plCurrent.getAmount()).toAmount();
                if (receivedAmount.compareTo(savedAmount) <= 0 && !receivedAmount.equals(BigDecimal.ZERO)) {
                    receivedAmount.setScale(2, BigDecimal.ROUND_DOWN);
                    result.add(updateAndSave(plCurrent, receivedAmount.toString(), player.getId(), authorGuid, lithiumTokenUtil));
                    continue;
                } else if (receivedAmount.equals(BigDecimal.ZERO) || receivedAmount.compareTo(savedAmount) == 1) {
                    {
                        //pending dep limit
                        result.add(buildAndSave(playerGuid, amount, granularity, player.getId(), authorGuid, true, lithiumTokenUtil));
                    }
                    continue;
                }
            }
            if (plCurrent == null && CurrencyAmount.fromAmountString(amount).toAmount().equals(BigDecimal.ZERO)) {
                continue;
            }
            result.add(buildAndSave(playerGuid, amount, granularity, player.getId(), authorGuid, false, lithiumTokenUtil));
        }
        pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
        return result;
    }

    public PlayerLimit saveBO(String playerGuid, Granularity granularity, String amount, String authorGuid, Locale locale, LithiumTokenUtil lithiumTokenUtil) throws Status100InvalidInputDataException, Status500LimitInternalSystemClientException, Status550ServiceDomainClientException, Status481DomainDepositLimitDisabledException, Status480PendingDepositLimitException {
        domainPlayerDepositLimitsEnabled(playerGuid.split("/")[0], locale);
        if (Stream.of(playerGuid, granularity, amount).anyMatch(Objects::isNull))
            throw new Status100InvalidInputDataException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.INVALIDINPUT", null, locale));
        BigDecimal receivedAmount = CurrencyAmount.fromAmountString(amount).toAmount();
        if (receivedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new Status100InvalidInputDataException(messageSource.getMessage("SERVICE_LIMIT.DEPOSITLIMIT.ERROR.INVALIDINPUT", null, locale));
        }
        PlayerLimit plp = findPending(playerGuid, granularity);
        if (plp != null) {
            String errMsg = "";
            if (granularity.equals(Granularity.GRANULARITY_DAY))
                errMsg = "SERVICE_LIMIT.DEPOSITLIMIT.ERROR.PENDING_DEP_LIMIT_DAILY";
            else if (granularity.equals(Granularity.GRANULARITY_WEEK))
                errMsg = "SERVICE_LIMIT.DEPOSITLIMIT.ERROR.PENDING_DEP_LIMIT_WEEKLY";
            else if (granularity.equals(Granularity.GRANULARITY_MONTH))
                errMsg = "SERVICE_LIMIT.DEPOSITLIMIT.ERROR.PENDING_DEP_LIMIT_MONTHLY";
            throw new Status480PendingDepositLimitException(messageSource.getMessage(
                    errMsg,
                    new Object[]{new DateTime(plp.getCreatedDate(), DateTimeZone.getDefault()).plusDays(1).toString()},
                    locale
            ));
        }
        User player = null;
        try {
            player = externalUserService.findByGuid(playerGuid);
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
        PlayerLimit pl = find(playerGuid, granularity);
        if (pl != null) {
            BigDecimal savedAmount = CurrencyAmount.fromCents(pl.getAmount()).toAmount();
            if (receivedAmount.compareTo(savedAmount) < 0 && receivedAmount.compareTo(BigDecimal.ZERO) == 0) {
                boolean pending = savedAmount.compareTo(BigDecimal.ZERO) != 0;
                return buildAndSave(playerGuid, amount, granularity, player.getId(), authorGuid, pending, lithiumTokenUtil);

            } else if (receivedAmount.compareTo(savedAmount) <= 0 || receivedAmount.compareTo(BigDecimal.ZERO) == 0) {
                receivedAmount = receivedAmount.setScale(2, BigDecimal.ROUND_DOWN);
                PlayerLimit result = updateAndSave(pl, receivedAmount.toString(), player.getId(), authorGuid, lithiumTokenUtil);
                pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
                return result;
            } else if (receivedAmount.compareTo(savedAmount) == 1) {
                //pending dep limit
                return buildAndSave(playerGuid, amount, granularity, player.getId(), authorGuid, true, lithiumTokenUtil);
            }
        }

        PlayerLimit result = buildAndSave(playerGuid, amount, granularity, player.getId(), authorGuid, false, lithiumTokenUtil);
        pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
        return result;
    }

    private ArrayList<Granularity> parseRequestedGranularities(List<Integer> granularity) {
        ArrayList<Granularity> result = new ArrayList<>();
        for (Integer gr : granularity) {
            result.add(Granularity.fromGranularity(gr));
        }
        return result;
    }


    public void remove(String playerGuid, Granularity granularity, String authorGuid) {
        deleteDepositLimit(find(playerGuid, granularity), authorGuid);
        pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
    }

    public void removePending(String playerGuid, Granularity granularity, String authorGuid) {
        deleteDepositLimit(findPending(playerGuid, granularity), authorGuid);
    }

    public void removeSupposed(String playerGuid, Granularity granularity, String authorGuid) {
        deleteDepositLimit(findSupposed(playerGuid, granularity), authorGuid);
    }

    public void deleteDepositLimit(PlayerLimit playerLimit, String authorGuid) {
        playerLimitRepository.deleteByPlayerGuidAndGranularityAndType(playerLimit.getPlayerGuid(), playerLimit.getGranularity(), playerLimit.getType());
        addHistory(playerLimit, ModifyType.REMOVED, authorGuid);
    }

    private PlayerLimit updateAndSave(PlayerLimit playerLimit, String amount, Long playerId, String authorGuid, LithiumTokenUtil lithiumTokenUtil) throws Status500LimitInternalSystemClientException {
        try {
            updateAndSaveChangelog(playerLimit, CurrencyAmount.fromCents(playerLimit.getAmount()).toAmount().toPlainString(), CurrencyAmount.fromAmountString(amount).toAmount().toPlainString(), playerId, authorGuid, lithiumTokenUtil);
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
        playerLimit.setAmount(CurrencyAmount.fromAmountString(amount).toCents());
        playerLimit = playerLimitRepository.save(playerLimit);

        addHistory(playerLimit, ModifyType.UPDATED, authorGuid);

        return playerLimit;
    }

    private void updateAndSaveChangelog(PlayerLimit playerLimit, String fromAmount, String toAmount, Long playerId, String authorGuid, LithiumTokenUtil lithiumTokenUtil) throws Exception {
        String granularityStr = "";
        if (playerLimit.getGranularity() == (Granularity.GRANULARITY_DAY.granularity()))
            granularityStr = "SERVICE-LIMIT.DEPOSITLIMIT.COMMENT.UPDATE_AND_SAVE.DAILY";
        else if (playerLimit.getGranularity() == (Granularity.GRANULARITY_WEEK.granularity()))
            granularityStr = "SERVICE-LIMIT.DEPOSITLIMIT.COMMENT.UPDATE_AND_SAVE.WEEKLY";
        else if (playerLimit.getGranularity() == (Granularity.GRANULARITY_MONTH.granularity()))
            granularityStr = "SERVICE-LIMIT.DEPOSITLIMIT.COMMENT.UPDATE_AND_SAVE.MONTHLY";

	    Domain domain = domainService.retrieveDomainFromDomainService(playerLimit.getDomainName());

        List<ChangeLogFieldChange> clfc = Collections.singletonList(ChangeLogFieldChange.builder().field("amount").fromValue(fromAmount).toValue(toAmount).build());
        ChangeLogFieldChange.formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());

        String comment = messageSource.getMessage(granularityStr, new Object[]{ChangeLogFieldChange.getFromAmountValue(clfc), ChangeLogFieldChange.getToAmountValue(clfc)}, Locale.US);
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user.depositlimit", "edit", playerId, authorGuid, lithiumTokenUtil, comment,
            null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.DEPOSIT_LIMITS, 40, playerLimit.getDomainName());
    }

    private void buildChangelogForDelete(PlayerLimit currentLimit, PlayerLimit supposedLimit, Long playerId, String authorGuid, LithiumTokenUtil token, String fullName) throws Exception {
        String granularityStr = "";
        if (currentLimit.getGranularity() == (Granularity.GRANULARITY_DAY.granularity()))
            granularityStr = "SERVICE_LIMIT.DEPOSITLIMIT.COMMENT.UPDATE_AND_DELETE.DAILY";
        else if (currentLimit.getGranularity() == (Granularity.GRANULARITY_WEEK.granularity()))
            granularityStr = "SERVICE_LIMIT.DEPOSITLIMIT.COMMENT.UPDATE_AND_DELETE.WEEKLY";
        else if (currentLimit.getGranularity() == (Granularity.GRANULARITY_MONTH.granularity()))
            granularityStr = "SERVICE_LIMIT.DEPOSITLIMIT.COMMENT.UPDATE_AND_DELETE.MONTHLY";

	    Domain domain = domainService.retrieveDomainFromDomainService(currentLimit.getDomainName());
        List<ChangeLogFieldChange> clfc = Arrays.asList(ChangeLogFieldChange.builder().field("amount").fromValue(CurrencyAmount.fromCents(currentLimit.getAmount()).toAmount().toPlainString()).toValue(CurrencyAmount.fromCents(supposedLimit.getAmount()).toAmount().toPlainString()).build());
        ChangeLogFieldChange.formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());

        String comment = messageSource.getMessage(granularityStr, null, Locale.US);
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user.depositlimit", "delete", playerId, authorGuid, token, comment,
                null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.DEPOSIT_LIMITS, 0, currentLimit.getDomainName());
    }

    private void buildAndSaveChangelog(PlayerLimit playerLimit, Long playerId, String authorGuid, boolean pending, LithiumTokenUtil util) throws Exception {
        String granularityStr = "";
        if (playerLimit.getGranularity() == (Granularity.GRANULARITY_DAY.granularity()))
            granularityStr = "SERVICE-LIMIT.DEPOSITLIMIT.COMMENT.BUILD_AND_SAVE.DAILY";
        else if (playerLimit.getGranularity() == (Granularity.GRANULARITY_WEEK.granularity()))
            granularityStr = "SERVICE-LIMIT.DEPOSITLIMIT.COMMENT.BUILD_AND_SAVE.WEEKLY";
        else if (playerLimit.getGranularity() == (Granularity.GRANULARITY_MONTH.granularity()))
            granularityStr = "SERVICE-LIMIT.DEPOSITLIMIT.COMMENT.BUILD_AND_SAVE.MONTHLY";

	    List<ChangeLogFieldChange> clfc = Collections.singletonList(ChangeLogFieldChange.builder().field("amount").toValue(CurrencyAmount.fromCents(playerLimit.getAmount()).toAmount().toPlainString()).build());
	    Domain domain = domainService.retrieveDomainFromDomainService(playerLimit.getDomainName());
        ChangeLogFieldChange.formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());

	    String comment = messageSource.getMessage(granularityStr, new Object[]{ChangeLogFieldChange.getToAmountValue(clfc)}, Locale.US);
        if (pending)
            comment = messageSource.getMessage("SERVICE-LIMIT.DEPOSITLIMIT.COMMENT.BUILD_AND_SAVE.PENDING", null, Locale.US) + " " + comment;

        changeLogService.registerChangesForNotesWithFullNameAndDomain("user.depositlimit", "create", playerId, authorGuid, util, comment,
            null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.DEPOSIT_LIMITS, 1, playerLimit.getDomainName());
    }

	private PlayerLimit buildAndSave(String playerGuid, String amount, Granularity granularity, Long playerId, String authorGuid, boolean pending, LithiumTokenUtil lithiumTokenUtil) throws Status500LimitInternalSystemClientException {
        PlayerLimit playerLimit = PlayerLimit.builder()
                .amount(CurrencyAmount.fromAmountString(amount).toCents())
                .granularity(granularity.granularity())
                .playerGuid(playerGuid)
                .type((pending) ? LimitType.TYPE_DEPOSIT_LIMIT_PENDING.type() : LimitType.TYPE_DEPOSIT_LIMIT.type())
                .domainName(playerGuid.split("/")[0])
                .build();
        try {
            buildAndSaveChangelog(playerLimit, playerId, authorGuid, pending, lithiumTokenUtil);
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }

        PlayerLimit savedlimit = playerLimitRepository.save(playerLimit);

        addHistory(playerLimit, ModifyType.CREATED, authorGuid);

        return savedlimit;
    }

    public Domain domainFromServiceDomain(String domainName) throws Status550ServiceDomainClientException {
        return cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    }

    private <E> Optional<E> getClient(Class<E> theClass, String url) {
        E clientInstance = null;

        try {
            clientInstance = serviceClientFactory.target(theClass, url, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
        }
        return Optional.ofNullable(clientInstance);

    }
    private Optional<CashierInternalClient> getCashierInternalClient() {
        return getClient(CashierInternalClient.class, "service-cashier");
    }


    private Optional<AccountingSummaryTransactionTypeClient> getAccountingSummaryTransactionTypeClient() {
        return getClient(AccountingSummaryTransactionTypeClient.class, "service-accounting-provider-internal");
    }


    //Accounting side
    public SummaryTransactionType accountingTotals(String playerGuid, Granularity granularity) throws Exception {
        Domain domain = domainFromServiceDomain(playerGuid.split("/")[0]);
        String currency = domain.getCurrency();
        String accountCode = "PLAYER_BALANCE";
        String transactionType = "CASHIER_DEPOSIT";
        switch (granularity) {
            case GRANULARITY_DAY:
                return accountingDay(playerGuid, domain, currency, accountCode, transactionType);
            case GRANULARITY_WEEK:
                return accountingWeek(playerGuid, domain, currency, accountCode, transactionType);
            case GRANULARITY_MONTH:
                return accountingMonth(playerGuid, domain, currency, accountCode, transactionType);
            default:
                throw new Status100InvalidInputDataException();
        }
    }

    public SummaryTransactionType accountingMonth(String playerGuid, Domain domain, String currency, String accountCode, String transactionType) throws Exception {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String transactionTypeMigrated = "PLAYER_BALANCE_OPERATOR_MIGRATION";
        DateTimeZone timeZone = DateTimeZone.getDefault();
        DateTime now = DateTime.now(timeZone);
        DateTime dayStart = now.withTimeAtStartOfDay();


        DateTime monthStart = dayStart.dayOfMonth().withMinimumValue();
        DateTime monthEnd = dayStart.dayOfMonth().withMaximumValue().plusDays(1);

        Response<List<SummaryTransactionType>> domainMonth = getAccountingSummaryTransactionTypeClient().get().findLimitedByOwnerGuid(
                domain.getName(),
                playerGuid,
                Granularity.GRANULARITY_MONTH.granularity(),
                accountCode,
                transactionType,
                currency,
                monthStart.toString(dtfOut),
                monthEnd.toString(dtfOut)
        );

        Response<List<SummaryTransactionType>> domainMonthMigrated = getAccountingSummaryTransactionTypeClient().get().findLimitedByOwnerGuid(
            domain.getName(),
            playerGuid,
            Granularity.GRANULARITY_MONTH.granularity(),
            accountCode,
            transactionTypeMigrated,
            currency,
            monthStart.toString(dtfOut),
            monthEnd.toString(dtfOut)
        );

        if(domainMonth.isSuccessful() && domainMonthMigrated.isSuccessful() && !ObjectUtils.isEmpty(domainMonthMigrated.getData())){
            domainMonth.getData().addAll(domainMonthMigrated.getData());
        }

        log.debug("accountingMonth (" + playerGuid + ")[" + currency + "][" + accountCode + "][" + transactionType + "] :: monthStart: " + monthStart + " monthEnd: " + monthEnd + " :: " + domainMonth);
        if (domainMonth.isSuccessful() && domainMonth.getData().size() > 0) {
            return domainMonth.getData().get(0);
        }
        return null;
    }

    public SummaryTransactionType accountingWeek(String playerGuid, Domain domain, String currency, String accountCode, String transactionType) throws Exception {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String transactionTypeMigrated = "PLAYER_BALANCE_OPERATOR_MIGRATION";
        DateTimeZone timeZone = DateTimeZone.getDefault();
        DateTime now = DateTime.now(timeZone);
        DateTime dayStart = now.withTimeAtStartOfDay();
        DateTime weekStart = dayStart.dayOfWeek().withMinimumValue();
        DateTime weekEnd = dayStart.dayOfWeek().withMaximumValue().plusDays(1);

        Response<List<SummaryTransactionType>> domainWeek = getAccountingSummaryTransactionTypeClient().get().findLimitedByOwnerGuid(
                domain.getName(),
                playerGuid,
                Granularity.GRANULARITY_WEEK.granularity(),
                accountCode,
                transactionType,
                currency,
                weekStart.toString(dtfOut),
                weekEnd.toString(dtfOut)
        );


        Response<List<SummaryTransactionType>> domainWeekMigrated = getAccountingSummaryTransactionTypeClient().get().findLimitedByOwnerGuid(
            domain.getName(),
            playerGuid,
            Granularity.GRANULARITY_MONTH.granularity(),
            accountCode,
            transactionTypeMigrated,
            currency,
            weekStart.toString(dtfOut),
            weekEnd.toString(dtfOut)
        );

        if(domainWeek.isSuccessful() && domainWeekMigrated.isSuccessful() && !ObjectUtils.isEmpty(domainWeekMigrated.getData())){
            domainWeek.getData().addAll(domainWeekMigrated.getData());
        }
        log.debug("accountingWeek (" + playerGuid + ")[" + currency + "][" + accountCode + "][" + transactionType + "] weekStart: " + weekStart + " weekEnd: " + weekEnd + " :: " + domainWeek);
        if (domainWeek.isSuccessful() && domainWeek.getData().size() > 0) {
            return domainWeek.getData().get(0);
        }
        return null;
    }

    public SummaryTransactionType accountingDay(String playerGuid, Domain domain, String currency, String accountCode, String transactionType) throws Exception {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String transactionTypeMigrated = "PLAYER_BALANCE_OPERATOR_MIGRATION";
        DateTimeZone timeZone = DateTimeZone.getDefault();
        DateTime now = DateTime.now(timeZone);
        DateTime dayStart = now.withTimeAtStartOfDay();
        DateTime dayEnd = now.plusDays(1).withTimeAtStartOfDay();


        Response<List<SummaryTransactionType>> domainDay = getAccountingSummaryTransactionTypeClient().get().findLimitedByOwnerGuid(
                domain.getName(),
                playerGuid,
                Granularity.GRANULARITY_DAY.granularity(),
                accountCode,
                transactionType,
                currency,
                dayStart.toString(dtfOut),
                dayEnd.toString(dtfOut)
        );


        Response<List<SummaryTransactionType>> domainDayMigrated = getAccountingSummaryTransactionTypeClient().get().findLimitedByOwnerGuid(
            domain.getName(),
            playerGuid,
            Granularity.GRANULARITY_MONTH.granularity(),
            accountCode,
            transactionTypeMigrated,
            currency,
            dayStart.toString(dtfOut),
            dayEnd.toString(dtfOut)
        );

        if(domainDay.isSuccessful() && domainDayMigrated.isSuccessful() && !ObjectUtils.isEmpty(domainDayMigrated.getData())){
            domainDay.getData().addAll(domainDayMigrated.getData());
        }
        log.debug("accountingDay (" + playerGuid + ")[" + currency + "][" + accountCode + "][" + transactionType + "] dayStart: " + dayStart + " dayEnd: " + dayEnd + " :: " + domainDay);
        if (domainDay.isSuccessful() && domainDay.getData().size() > 0) {
            return domainDay.getData().get(0);
        }
        return null;
    }

    private void addHistory(PlayerLimit playerLimit, ModifyType modifyType, String authorGuid) {
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

}

