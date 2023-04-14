package lithium.service.casino.provider.roxor.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.client.objects.response.EBalanceAdjustmentResponseStatus;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.exceptions.Status475NullVariablesException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.data.GameplayOperationEventRequest;
import lithium.service.casino.provider.roxor.data.GameplayOperationEventType;
import lithium.service.casino.provider.roxor.data.Metadata;
import lithium.service.casino.provider.roxor.data.Payload;
import lithium.service.casino.provider.roxor.storage.entities.DomainGame;
import lithium.service.casino.provider.roxor.storage.entities.User;
import lithium.service.casino.provider.roxor.storage.repositories.DomainRepository;
import lithium.service.casino.provider.roxor.storage.repositories.UserRepository;
import lithium.service.casino.provider.roxor.storage.repositories.DomainGameRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.GamesInternalSystemClient;
import lithium.service.games.client.objects.Game;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.user.client.UserApiInternalSystemClient;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class GamePlayOperationEventService {

    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModuleInfo moduleInfo;
    @Autowired
    private LithiumServiceClientFactory lithiumServiceClientFactory;
    @Autowired
    private CasinoClientService casinoClientService;
    @Autowired
    private CachingDomainClientService cachingDomainClientService;
    @Autowired
    private DomainGameRepository domainGameRepository;

    public String findOrCreateUser(GameplayOperationEventRequest request)
            throws Exception {
        String website = request.getPayload().getWebsite();
        String playerId = request.getPayload().getPlayerId();

        SW.start("user.remote.find-or-create");
        Response<lithium.service.user.client.objects.User> userResponse = getUserApiInternalSystemClient()
                .get()
                .createStub(website, playerId);
        SW.stop();

        if (!userResponse.isSuccessful()) {
            String msg = "Unable to find or create remote user | website: " + website + ", playerId: " + playerId;
            log.error(msg + " | {} | request: {}", userResponse.getMessage(), request);
            throw new Status500InternalServerErrorException(msg);
        }

        lithium.service.user.client.objects.User user = userResponse.getData();
        SW.start("domain.find-or-create");
        lithium.service.casino.provider.roxor.storage.entities.Domain domain = domainRepository.findOrCreateByName(
                user.getDomain().getName(),
                () -> new lithium.service.casino.provider.roxor.storage.entities.Domain());
        SW.stop();

        SW.start("user.local.find-or-create");
        try {
            userRepository.findOrCreateByGuid(user.guid(),
                    () -> User.builder().apiToken(user.guid()).domain(domain).build());
        } catch (ConstraintViolationException e) {
        }
        SW.stop();

        return user.guid();
    }

    @Transactional()
    public void gamePlay(
            GameplayOperationEventRequest gameplayOperationEventRequest, String userGuid) throws
            Status401UnAuthorisedException, Status500RuntimeException,
            Status494DailyWinLimitReachedException, Status495MonthlyWinLimitReachedException,
            Status473DomainBettingDisabledException, Status493MonthlyLossLimitReachedException,
            Status492DailyLossLimitReachedException, Status474DomainProviderDisabledException,
            Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
            Status405UserDisabledException, Status485WeeklyWinLimitReachedException, Status496PlayerCoolingOffException,
            Status471InsufficientFundsException, Status511UpstreamServiceUnavailableException,
            Status550ServiceDomainClientException, Status484WeeklyLossLimitReachedException,
            Status478TimeSlotLimitException, Status438PlayTimeLimitReachedException {

        if ((gameplayOperationEventRequest.getPayload().getType() != GameplayOperationEventType.START_GAME_PLAY)
                && (gameplayOperationEventRequest.getPayload().getType() != GameplayOperationEventType.FINISH_GAME_PLAY)
                && (gameplayOperationEventRequest.getPayload().getType() != GameplayOperationEventType.TRANSFER_DEBIT)
                && (gameplayOperationEventRequest.getPayload().getType() != GameplayOperationEventType.TRANSFER_CREDIT)
        ) {
            log.debug("Skipping unsupported gameplay operation : " + gameplayOperationEventRequest.getPayload().getType().toString());
            return;
        }

        SW.start("Thread ID-" + Thread.currentThread().getId()
                + " -  GamePlayOperationEventService::gamePlay::userRepository.findByGuidAlwaysLock");
        userRepository.findByGuidAlwaysLock(userGuid);
        SW.stop();

        Payload gameplayOperationEventPayload = gameplayOperationEventRequest.getPayload();
        Metadata gameplayOperationEventMetadata = gameplayOperationEventRequest.getMetadata();

        String website = gameplayOperationEventPayload.getWebsite();
        String playerId = gameplayOperationEventPayload.getPlayerId();

        SW.start("Thread ID-" + Thread.currentThread().getId() + " -  GamePlayOperationEventService::gamePlay::cachingDomainClientService.retrieveDomainFromDomainService");
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(website);
        SW.stop();

        String moduleName = this.moduleInfo.getModuleName();
        String domainName = domain.getName();
        String gameKey = gameplayOperationEventPayload.getGameKey();

        try {
            SW.start("Thread ID-" + Thread.currentThread().getId() + " -  GamePlayOperationEventService::gamePlay::domainGameRepository.findByDomainNameAndGameKey");
            Optional<DomainGame> existingDomainGame = domainGameRepository.findByDomainNameAndGameKey(domainName, gameKey);
            SW.stop();

            if (!existingDomainGame.isPresent()) {
                GamesInternalSystemClient gamesInternalSystemClient = lithiumServiceClientFactory.target(GamesInternalSystemClient.class, "service-games", true);

                SW.start("Thread ID-" + Thread.currentThread().getId() + " -  GamePlayOperationEventService::gamePlay::gamesInternalSystemClient.findOrCreateGame");
                Response<Game> gameResponse = gamesInternalSystemClient.findOrCreateGame(domainName, moduleName,
                        gameKey, gameKey, gameKey, gameKey, gameKey, null, null, null, null, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
                SW.stop();

                if (gameResponse.isSuccessful() && gameResponse.getData() != null) {
                    domainGameRepository.findOrCreateByDomainNameAndGameKey(domainName, gameKey);
                } else {
                    log.error("Error occurred while finding or creating game.");
                    throw new Exception(gameResponse.getMessage());
                }
            }

        } catch (Exception exception) {
            log.error("Error occurred while finding or creating game.");
            throw new Status500RuntimeException("Could not finding or create game", exception);
        }

        Long externalTimestamp = gameplayOperationEventMetadata.getEventTime();
        String gameplayId = gameplayOperationEventPayload.getGameplayId();

        BalanceAdjustmentRequest casinoRequest = new BalanceAdjustmentRequest();

        casinoRequest.setRoundId(gameplayId);
        casinoRequest.setExternalTimestamp(externalTimestamp);
        casinoRequest.setGameGuid(domain.getName() + "/" + moduleName + "_" + gameKey);



        switch (gameplayOperationEventPayload.getType()) {
            case FINISH_GAME_PLAY:
                casinoRequest.setRoundFinished(Boolean.TRUE);
                break;
            default:
                casinoRequest.setRoundFinished(Boolean.FALSE);
                break;
        }

        casinoRequest.setBonusTran(false);
        casinoRequest.setBonusId(-1);
        casinoRequest.setDomainName(domain.getName());
        casinoRequest.setCurrencyCode(domain.getCurrency());
        casinoRequest.setProviderGuid(domain.getName() + "/" + moduleName);

        casinoRequest.setUserGuid(userGuid);
        casinoRequest.setTransactionTiebackId(null);
        casinoRequest.setRealMoneyOnly(true);
        casinoRequest.setAllowNegativeBalanceAdjustment(Boolean.TRUE);

        String sessionKey = gameplayOperationEventRequest.getPayload().getSessionId();

        casinoRequest.setGameSessionId(sessionKey);
        casinoRequest.setPerformAccessChecks(false);
        casinoRequest.setPersistRound(true);
        String gameGuid = casinoRequest.getGameGuid();
        userGuid = casinoRequest.getUserGuid();

        ArrayList<BalanceAdjustmentComponent> adjustmentComponents = new ArrayList<>();

        String additionalReference = gameplayOperationEventMetadata.getEventId();
        String eventId = gameplayOperationEventMetadata.getEventId();

        switch (gameplayOperationEventPayload.getType()) {
            case TRANSFER_DEBIT: {
                adjustmentComponents.add(
                        BalanceAdjustmentComponent.builder()
                                .betTransactionId(eventId)
                                .adjustmentType(EBalanceAdjustmentComponentType.CASINO_BET)

                                .amount(CurrencyAmount.fromAmount(gameplayOperationEventPayload
                                        .getAmount())
                                        .toCents())
                                .transactionIdLabelOverride(eventId + "_DEBIT")
                                .additionalReference(additionalReference) // gamePlayOperation.getTransferId()
                                .build()
                );
                break;
            }
            case TRANSFER_CREDIT: {
                adjustmentComponents.add(
                        BalanceAdjustmentComponent.builder()
                                .betTransactionId(eventId)
                                .adjustmentType(EBalanceAdjustmentComponentType.CASINO_WIN)
                                .amount(CurrencyAmount.fromAmount(gameplayOperationEventPayload
                                        .getAmount())
                                        .toCents())
                                .transactionIdLabelOverride(eventId + "_CREDIT")
                                .additionalReference(additionalReference)
                                .build()
                );
                break;
            }

        }

        casinoRequest.setAdjustmentComponentList(adjustmentComponents);

        if (!adjustmentComponents.isEmpty()) {
            SW.start("Thread ID-" + Thread.currentThread().getId() + " -  GamePlayOperationEventService::gamePlay::casinoClientService.multiBetV1");
            BalanceAdjustmentResponse balanceAdjustmentResponse = casinoClientService.multiBetV1(casinoRequest, domain.getDefaultLocale());
            SW.stop();

            if (!(balanceAdjustmentResponse.getResult() == EBalanceAdjustmentResponseStatus.SUCCESS)) {
                log.error("Internal error received from Casino : " + balanceAdjustmentResponse.getResult().name());
                throw new Status500RuntimeException("Internal error received from Casino " + balanceAdjustmentResponse.getResult().name());
            }

        } else {
      if (casinoRequest.getRoundFinished()) {
        // Roxor side got  a FINISH_GAME_PLAY and adjustment is empty at this stage hence calling
        // method to complete betRound
        try {
          casinoClientService.completeBetRound(
                casinoRequest.getDomainName(),
              casinoRequest.getProviderGuid(),
              casinoRequest.getRoundId(), Optional.ofNullable(gameGuid), Optional.ofNullable(userGuid));
        } catch (Status475NullVariablesException | Status500UnhandledCasinoClientException e) {
          log.error(
              "Could not complete bet round [roundId="
                  + casinoRequest.getRoundId()
                  + "] "
                  + e.getMessage(),
              e);
        }
      }
    }
  }

  private Optional<UserApiInternalSystemClient> getUserApiInternalSystemClient() {
    return getClient(UserApiInternalSystemClient.class, "service-user");
  }

    private <E> Optional<E> getClient(Class<E> theClass, String url) {
        E clientInstance = null;

        try {
            clientInstance = lithiumServiceClientFactory.target(theClass, url, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
        }

        return Optional.ofNullable(clientInstance);
    }
}
