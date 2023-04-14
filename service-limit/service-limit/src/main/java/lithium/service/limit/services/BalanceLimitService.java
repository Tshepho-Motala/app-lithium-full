package lithium.service.limit.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.cashier.client.CashierInternalClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitType;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.PlayerLimitFE;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.data.entities.PlayerLimitHistory;
import lithium.service.limit.data.objects.PlayerBalanceLimit;
import lithium.service.limit.data.repositories.PlayerLimitHistoryRepository;
import lithium.service.limit.data.repositories.PlayerLimitRepository;
import lithium.service.limit.enums.ModifyType;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lithium.service.domain.client.DomainSettings.PENDING_BALANCE_LIMIT_UPDATE_DELAY;

@Slf4j
@Service
public class BalanceLimitService {

    @Autowired
    private PlayerLimitRepository playerLimitRepository;
    @Autowired
    private PlayerLimitHistoryRepository historyRepository;
    @Autowired
    private CachingDomainClientService cachingDomainClientService;
    @Autowired
    private ChangeLogService changeLogService;
    @Autowired
    private UserApiInternalClientService userApiInternalClientService;
    @Autowired
    private PubSubUserAccountChangeProxy pubSubUserAccountChangeProxy;
    @Autowired
    private LithiumServiceClientFactory serviceClientFactory;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private AccountingClientService accountingClientService;

    public PlayerLimit findCurrent(String playerGuid) throws Status550ServiceDomainClientException, Status500LimitInternalSystemClientException {

        PlayerLimit playerLimit = findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_TOTAL, LimitType.TYPE_BALANCE_LIMIT);
        if (nonNull(playerLimit)) {
            String domainName = playerGuid.split("/")[0];
            Domain domain = domainFromServiceDomain(domainName);
            Long balance = getCustomerBalance(domain.getCurrency(), domainName, playerGuid);
            Long pendingDepositAmount = Optional.ofNullable(getPendingAmount(playerGuid)).orElse(0L);
            Long currentAmountUsedFromBalanceLimit = balance + pendingDepositAmount;
            if(currentAmountUsedFromBalanceLimit >= playerLimit.getAmount()) {
                currentAmountUsedFromBalanceLimit = playerLimit.getAmount();
            }
            playerLimit.setAmountUsed(currentAmountUsedFromBalanceLimit);
        }
        return playerLimit;
    }

    public PlayerLimit findPending(String playerGuid) {
        return findByPlayerGuidAndGranularityAndType(playerGuid, Granularity.GRANULARITY_TOTAL, LimitType.TYPE_BALANCE_LIMIT_PENDING);
    }

    private PlayerLimit findByPlayerGuidAndGranularityAndType(String playerGuid, Granularity granularity, LimitType limitType) {
        return playerLimitRepository.findByPlayerGuidAndGranularityAndType(playerGuid, granularity.granularity(), limitType.type());
    }


    private void domainPlayerBalanceLimitEnabled(String domainName) throws Status550ServiceDomainClientException, Status476DomainBalanceLimitDisabledException {
        Domain domain = domainFromServiceDomain(domainName);
        if (!domain.getPlayerBalanceLimit()) {
            throw new Status476DomainBalanceLimitDisabledException("Possibility to define balance limit disabled for current domain");
        }
    }

    private Domain domainFromServiceDomain(String domainName) throws Status550ServiceDomainClientException {
        return cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    }

    public PlayerBalanceLimit checkAndFindPlayerBalanceLimit(String playerGuid) throws Status550ServiceDomainClientException, Status476DomainBalanceLimitDisabledException, Status500LimitInternalSystemClientException {
        domainPlayerBalanceLimitEnabled(playerGuid.split("/")[0]);

        PlayerBalanceLimit balanceLimit = getPlayerBalanceLimit(playerGuid);

        return balanceLimit;
    }

    private PlayerBalanceLimit getPlayerBalanceLimit(String playerGuid) throws Status550ServiceDomainClientException, Status500LimitInternalSystemClientException {
        PlayerBalanceLimit balanceLimit = new PlayerBalanceLimit();
        balanceLimit.setDisabled(false);

        lithium.service.limit.client.objects.PlayerLimit current = Optional.ofNullable(findCurrent(playerGuid))
                .map(this::convertPlayerLimitEntity)
                .orElse(null);
        lithium.service.limit.client.objects.PlayerLimit pending = Optional.ofNullable(findPending(playerGuid))
                .map(this::convertPlayerLimitEntity)
                .orElse(null);

        balanceLimit.setCurrent(current);
        balanceLimit.setPending(pending);
        return balanceLimit;
    }

    public PlayerBalanceLimit save(String playerGuid, BigDecimal amount, String authorGuid, LithiumTokenUtil util) throws Status100InvalidInputDataException, Status550ServiceDomainClientException, Status476DomainBalanceLimitDisabledException, Status500LimitInternalSystemClientException, Status510AccountingProviderUnavailableException {
        Long newAmount = Optional.ofNullable(amount)
                .filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) >= 0 && bigDecimal.compareTo(BigDecimal.valueOf(10000000000000L)) < 0)
                .map(bigDecimal -> bigDecimal.movePointRight(2).longValue())
                .orElseThrow(() -> new Status100InvalidInputDataException("Balance limit value out of range"));

        String domainName = playerGuid.split("/")[0];
        //domainPlayerBalanceLimitEnabled(domainName);
        if (isNull(playerGuid) || isNull(newAmount)) {
            log.error("Required data is null, playerGuid: " + playerGuid + ", newAmount: " + newAmount);
            throw new Status100InvalidInputDataException();
        }

        PlayerLimit current = findCurrent(playerGuid);

        if (isNull(current)) {
            createAndSaveBalanceLimit(domainName, playerGuid, newAmount, authorGuid, util,false);
            return getPlayerBalanceLimit(playerGuid);
        }

        if (current.getAmount() == newAmount) {
            log.info("Requested balance limit the same as current balance limit (" + playerGuid + "), update skipped");
            return getPlayerBalanceLimit(playerGuid);
        }

        PlayerLimit pending = findPending(playerGuid);
        if (nonNull(pending) && pending.getAmount() == newAmount) {
            log.info("Requested balance limit the same as pending balance limit (" + playerGuid + "), update skipped");
            return getPlayerBalanceLimit(playerGuid);
        }

        if (newAmount < current.getAmount()) {
            PlayerLimit updatedCurrentPlayerLimit = updateAndSaveBalanceLimit(current, newAmount, authorGuid, util);
            if (nonNull(pending)) {
                log.info("Deleting pending balance limit (" + playerGuid + ", " + pending.getId() + ") due current balance limit decreased");
                deleteBalanceLimit(pending, authorGuid);
            }
            return PlayerBalanceLimit.builder()
                    .current(convertPlayerLimitEntity(updatedCurrentPlayerLimit))
                    .pending(null)
                    .disabled(false)
                    .build();
        }

        if (newAmount > current.getAmount()) {
            if (nonNull(pending)) {
                log.info("Deleting pending balance limit (" + playerGuid + ", " + pending.getId() + ") due creating new one pending balance limit");
                deleteBalanceLimit(pending, authorGuid);
            }
            PlayerLimit updatedPendingPlayerLimit = createAndSaveBalanceLimit(domainName, playerGuid, newAmount, authorGuid, util,true);
            return PlayerBalanceLimit.builder()
                    .current(convertPlayerLimitEntity(current))
                    .pending(convertPlayerLimitEntity(updatedPendingPlayerLimit))
                    .disabled(false)
                    .build();
        }

        log.error("Can't update player(" + playerGuid + ") balance limit due to unexpected issue, playerGuid: "
                + playerGuid + ", newAmount: " + newAmount + ", authorGuid: " + authorGuid);
        throw new Status100InvalidInputDataException("Can't update player balance limit due to unexpected issue");
    }

    private lithium.service.limit.client.objects.PlayerLimit convertPlayerLimitEntity(PlayerLimit playerLimit) {
        if (isNull(playerLimit)) {
            return null;
        }
        return mapper.convertValue(playerLimit, lithium.service.limit.client.objects.PlayerLimit.class);
    }

    private PlayerLimit createAndSaveBalanceLimit(String domainName, String playerGuid, Long amount, String authorGuid, LithiumTokenUtil util, boolean pending) throws Status550ServiceDomainClientException, Status510AccountingProviderUnavailableException {
        PlayerLimit currentPlayerLimit = findCurrent(playerGuid);

        PlayerLimit newPlayerLimit = PlayerLimit.builder()
                .amount(amount)
                .playerGuid(playerGuid)
                .domainName(domainName)
                .granularity(Granularity.GRANULARITY_TOTAL.granularity())
                .type(pending ? LimitType.TYPE_BALANCE_LIMIT_PENDING.type() : LimitType.TYPE_BALANCE_LIMIT.type())
                .build();
        PlayerLimit updatedLimit = savePlayerBalanceLimit(authorGuid, newPlayerLimit);

        if (!pending) {
            pubSubUserAccountChangeProxy.listenAccountChanges(playerGuid);
        }
        addHistory(updatedLimit, ModifyType.CREATED, authorGuid);

        try {
            String currentAmountWithCurrency = CurrencyAmount.fromCents(currentPlayerLimit != null ? currentPlayerLimit.getAmount(): 0L).toAmount().setScale(2).toPlainString();
            String amountWithCurrency = CurrencyAmount.fromCents(amount).toAmount().setScale(2).toPlainString();
            String comment = "Created " + (pending ? "pending" : "") + " balance limit with amount " + amountWithCurrency;
            List<ChangeLogFieldChange> clfc = Arrays.asList(ChangeLogFieldChange.builder().field("amount").fromValue(currentAmountWithCurrency).toValue(amountWithCurrency).build());
            User player = userApiInternalClientService.getUserByGuid(updatedLimit.getPlayerGuid());
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.balancelimit", "create", player.getId(), authorGuid, util, comment,
                    null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.BALANCE_LIMIT, 40, domainName);
        } catch (Throwable e) {
            log.warn("Problem adding changelog on create balance limit: PlayerLimit -> {}, exception -> {}", updatedLimit, e);
        }
        return updatedLimit;
    }

    private PlayerLimit updateAndSaveBalanceLimit(PlayerLimit playerLimit, Long newAmount, String authorGuid, LithiumTokenUtil util) throws Status550ServiceDomainClientException, Status510AccountingProviderUnavailableException {
        Long previousAmount = playerLimit.getAmount();//clf
        playerLimit.setAmount(newAmount);
        PlayerLimit updatedLimit = savePlayerBalanceLimit(authorGuid, playerLimit);
        pubSubUserAccountChangeProxy.listenAccountChanges(playerLimit.getPlayerGuid());
        addHistory(updatedLimit, ModifyType.UPDATED, authorGuid);

        try {
            String amountFromWithCurrency = CurrencyAmount.fromCents(previousAmount).toAmount().setScale(2).toPlainString();
            String amountToWithCurrency = CurrencyAmount.fromCents(newAmount).toAmount().setScale(2).toPlainString();
            String comment = "Updated player balance limit from " + amountFromWithCurrency + " to " + amountToWithCurrency;
            List<ChangeLogFieldChange> clfc = Collections.singletonList(ChangeLogFieldChange.builder().field("amount").fromValue(amountFromWithCurrency).toValue(amountToWithCurrency).build());
            User player = userApiInternalClientService.getUserByGuid(updatedLimit.getPlayerGuid());
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.balancelimit", "edit", player.getId(), authorGuid, util, comment,
                    null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.BALANCE_LIMIT, 40, playerLimit.getDomainName());
        } catch (Throwable e) {
            log.warn("Problem adding changelog on update balance limit: PlayerLimit -> {}, exception -> {}", updatedLimit, e);
        }
        updatedLimit.setAmountUsed(playerLimit.getAmountUsed());
        return updatedLimit;
    }

    private PlayerLimit savePlayerBalanceLimit(String authorGuid, PlayerLimit newPlayerLimit) throws Status550ServiceDomainClientException, Status510AccountingProviderUnavailableException {
        PlayerLimit updatedCurrentLimit = savePLayerBalanceLimitWithReplication(newPlayerLimit);
        log.info("Player balance limit (" + newPlayerLimit.getPlayerGuid() + ") updated by " + authorGuid + ": " + updatedCurrentLimit);
        return updatedCurrentLimit;
    }

    public void deleteBalanceLimit(PlayerLimit playerLimit, String authorGuid) {
        playerLimitRepository.deleteByPlayerGuidAndGranularityAndType(playerLimit.getPlayerGuid(), playerLimit.getGranularity(), playerLimit.getType());
        log.info("Player balance limit (" + playerLimit.getPlayerGuid() + ") removed by " + authorGuid + ": " + playerLimit);
        addHistory(playerLimit, ModifyType.REMOVED, authorGuid);
    }

    public void removePending(String playerGuid, String authorGuid, LithiumTokenUtil tokenUtil) {
        PlayerLimit playerLimit = findPending(playerGuid);
        if (isNull(playerLimit)) {
            log.warn("Can't delete pending balance limit (" + playerGuid + ") due not found");
            return;
        }
        deleteBalanceLimit(playerLimit, authorGuid);
        try {
            String amountFromWithCurrency = CurrencyAmount.fromCents(playerLimit.getAmount()).toAmount().setScale(2).toPlainString();
            List<ChangeLogFieldChange> clfc = Collections.singletonList(ChangeLogFieldChange.builder().field("amount").fromValue(amountFromWithCurrency).build());
            String comment = "Pending balance limit (" + amountFromWithCurrency + ") removed";
            User player = userApiInternalClientService.getUserByGuid(playerLimit.getPlayerGuid());
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.balancelimit", "delete", player.getId(), authorGuid, tokenUtil, comment,
                    null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.BALANCE_LIMIT, 40, playerLimit.getDomainName());
        } catch (Throwable e) {
            log.warn("Problem adding changelog on delete balance limit: PlayerLimit -> {}, exception -> {}", playerLimit, e);
        }
    }

    public Page<PlayerLimit> findAllPending(PageRequest pageRequest) {
        return playerLimitRepository.findByTypeAndGranularity(LimitType.TYPE_BALANCE_LIMIT_PENDING.type(), Granularity.GRANULARITY_TOTAL.granularity(), pageRequest);
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

    public void movePendingLimitToCurrent(PlayerLimit pendingPlayerLimit, String authorGuid) throws Status550ServiceDomainClientException,
            Status500LimitInternalSystemClientException, Status510AccountingProviderUnavailableException {
        PlayerLimit current = findCurrent(pendingPlayerLimit.getPlayerGuid());
        PlayerLimit currentLimit = findCurrent(pendingPlayerLimit.getPlayerGuid());
        PlayerLimit newCurrentLimit = Optional.ofNullable(current)
                .map(playerLimit -> {
                    playerLimit.setAmount(pendingPlayerLimit.getAmount());
                    return playerLimit;
                })
                .orElse(pendingPlayerLimit.toBuilder()
                        .id(null)
                        .type(LimitType.TYPE_BALANCE_LIMIT.type())
                        .build());

        PlayerLimit updatedLimit = savePlayerBalanceLimit(authorGuid, newCurrentLimit);
        pubSubUserAccountChangeProxy.listenAccountChanges(pendingPlayerLimit.getPlayerGuid());
        addHistory(updatedLimit, ModifyType.UPDATED, authorGuid);
        deleteBalanceLimit(pendingPlayerLimit, authorGuid);

        try {
            String amountFromWithCurrency = Optional.of(currentLimit).map(limit -> CurrencyAmount.fromCents(limit.getAmount()).toAmount().setScale(2).toPlainString()).orElse(null);
            String amountToWithCurrency = CurrencyAmount.fromCents(updatedLimit.getAmount()).toAmount().setScale(2).toPlainString();
            String comment = "Applied pending player balance limit to current " + amountToWithCurrency;
            List<ChangeLogFieldChange> clfc = Collections.singletonList(ChangeLogFieldChange.builder().field("amount").fromValue(amountFromWithCurrency).toValue(amountToWithCurrency).build());
            User player = userApiInternalClientService.getUserByGuid(updatedLimit.getPlayerGuid());
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.balancelimit", "edit", player.getId(), authorGuid, null, comment,
                    null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.BALANCE_LIMIT, 40, pendingPlayerLimit.getDomainName());
        } catch (Throwable e) {
            log.warn("Problem adding changelog on update balance limit: PlayerLimit -> {}, exception -> {}", updatedLimit, e);
        }
    }

    private Long getCustomerBalance(String currency, String domainName, String userGuid) throws Status500LimitInternalSystemClientException {
        try {
            Response<Long> bal = getAccountingClient().get().get(currency, domainName, userGuid);
            if (bal != null && bal.getStatus() == Response.Status.OK) {
                return bal.getData();
            } else {
                throw new Status500LimitInternalSystemClientException("Accounting service returned an unhealthy response.");
            }
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
    }

    private Long getPendingAmount(String playerGuid) {
        return getCashierInternalClient().get().pendingAmountCents(playerGuid).getData();
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


    private Optional<AccountingClient> getAccountingClient() {
        return getClient(AccountingClient.class, "service-accounting");
    }

    private PlayerLimitFE convert(lithium.service.limit.client.objects.PlayerLimit playerLimit) {
        return PlayerLimitFE.builder()
                .playerGuid(playerLimit.getPlayerGuid())
                .granularity(Granularity.fromGranularity(playerLimit.getGranularity()).type())
                .amount(CurrencyAmount.fromCents(playerLimit.getAmount()).toAmount())
                .amountUsed(CurrencyAmount.fromCents(playerLimit.getAmountUsed()).toAmount())
                .type(LimitType.fromType(playerLimit.getType()).name())
                .createdDate(new DateTime(playerLimit.getCreatedDate(), DateTimeZone.getDefault()))
                .modifiedDate(new DateTime(playerLimit.getModifiedDate(), DateTimeZone.getDefault()))
                .build();
    }

    public List<PlayerLimitFE> findAllFE(String playerGuid) throws Status500LimitInternalSystemClientException, Status476DomainBalanceLimitDisabledException {
        try {
            PlayerBalanceLimit balanceLimit = checkAndFindPlayerBalanceLimit(playerGuid);
            return convertPlayerBalanceLimitToFE(balanceLimit);
        } catch (Status476DomainBalanceLimitDisabledException e) {
            throw e;
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }

    }

    public List<PlayerLimitFE> saveFE(String playerGuid, BigDecimal amount, LithiumTokenUtil util) throws Status476DomainBalanceLimitDisabledException, Status500LimitInternalSystemClientException, Status100InvalidInputDataException {
        try {
            PlayerBalanceLimit saveLimit = save(playerGuid, amount, playerGuid, util);
            return convertPlayerBalanceLimitToFE(saveLimit);
        } catch (Status476DomainBalanceLimitDisabledException | Status100InvalidInputDataException e) {
            throw e;
        } catch (Exception e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public PlayerLimit savePLayerBalanceLimitWithReplication(PlayerLimit playerLimit) throws Status550ServiceDomainClientException, Status510AccountingProviderUnavailableException {
        PlayerLimit savedLimit = playerLimitRepository.save(playerLimit);
        if (LimitType.TYPE_BALANCE_LIMIT.type() == playerLimit.getType()) {
            Domain domain = domainFromServiceDomain(savedLimit.getDomainName());
            accountingClientService.setBalanceLimit(savedLimit.getDomainName(), playerLimit.getPlayerGuid(), playerLimit.getAmount(),
                    domain.getCurrency(), "PLAYER_BALANCE", "PLAYER_BALANCE", "TRANSFER_TO_BALANCE_LIMIT_ESCROW",
                    "PLAYER_BALANCE_LIMIT_ESCROW", "PLAYER_BALANCE");
        }
        return savedLimit;
    }

    private List<PlayerLimitFE> convertPlayerBalanceLimitToFE(PlayerBalanceLimit balanceLimit) throws Status550ServiceDomainClientException {
        List<PlayerLimitFE> result = new ArrayList<>();
        if (nonNull(balanceLimit.getCurrent())) {
            result.add(convert(balanceLimit.getCurrent()));
        }
        lithium.service.limit.client.objects.PlayerLimit balanceLimitPending = balanceLimit.getPending();
        if (nonNull(balanceLimitPending)) {
            PlayerLimitFE convert = convert(balanceLimitPending);
            long appliedAt = balanceLimitPending.getCreatedDate() + getPendingBalanceLimitUpdateDelay(balanceLimitPending.getDomainName()) * 3600_000;
            convert.setAppliedAt(new DateTime(appliedAt, DateTimeZone.getDefault()));
            result.add(convert);
        }
        return result;
    }

    public Integer getPendingBalanceLimitUpdateDelay(String domainName) throws Status550ServiceDomainClientException {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        String result = domain.findDomainSettingByName(PENDING_BALANCE_LIMIT_UPDATE_DELAY.key())
                .orElse(PENDING_BALANCE_LIMIT_UPDATE_DELAY.defaultValue());
        return Integer.valueOf(result);
    }
}
