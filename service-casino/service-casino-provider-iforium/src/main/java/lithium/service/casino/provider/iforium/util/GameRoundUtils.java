package lithium.service.casino.provider.iforium.util;

import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.client.objects.response.EBalanceAdjustmentResponseStatus;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.exception.CurrencyMismatchException;
import lithium.service.casino.provider.iforium.exception.GatewaySessionTokenExpiredException;
import lithium.service.casino.provider.iforium.exception.InsufficientFundsException;
import lithium.service.casino.provider.iforium.exception.InternalServerErrorException;
import lithium.service.casino.provider.iforium.exception.NotSupportedAccountTransactionTypeException;
import lithium.service.casino.provider.iforium.exception.TransactionNotFoundException;
import lithium.service.casino.provider.iforium.model.request.CreditRequest;
import lithium.service.casino.provider.iforium.model.request.EndRequest;
import lithium.service.casino.provider.iforium.model.request.GameRoundRequest;
import lithium.service.casino.provider.iforium.model.request.PlaceBetRequest;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.GameRoundResponse;
import lithium.service.casino.provider.iforium.model.response.GameRoundResult;
import lithium.service.casino.provider.iforium.model.response.OperatorTransactionSplit;
import lithium.service.casino.provider.iforium.model.response.PlaceBetResponse;
import lithium.service.casino.provider.iforium.model.response.PlaceBetResult;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@UtilityClass
public final class GameRoundUtils {

    private static final String SHADOW_DEPOSIT_TRANSACTION_TYPE = "811";

    public static void checkGatewaySessionTokenExpiration(GameRoundRequest gameRoundRequest, LoginEvent loginEvent,
                                                          Supplier<BigDecimal> balanceSupplier) {
        if (loginEvent.getLogout() != null && loginEvent.getLogout().before(Timestamp.valueOf(LocalDateTime.now()))) {
            String message = String.format("GatewaySessionToken=%s for OperatorAccountId=%s is expired",
                                           gameRoundRequest.getGatewaySessionToken(), gameRoundRequest.getOperatorAccountId());

            BigDecimal balance = balanceSupplier != null ? balanceSupplier.get() : Constants.ZERO_AMOUNT;
            throw new GatewaySessionTokenExpiredException(message, balance, gameRoundRequest.getCurrencyCode());
        }
    }

    public static void checkGatewaySessionTokenExpiration(EndRequest endRequest, LoginEvent loginEvent) {
        if (loginEvent.getLogout() != null && loginEvent.getLogout().before(Timestamp.valueOf(LocalDateTime.now()))) {
            String message = String.format("GatewaySessionToken=%s for OperatorAccountId=%s is expired",
                                           endRequest.getGatewaySessionToken(), endRequest.getOperatorAccountId());

            throw new GatewaySessionTokenExpiredException(message, Constants.ZERO_AMOUNT, endRequest.getCurrencyCode());
        }
    }

    public static void checkCurrencyValidity(GameRoundRequest gameRoundRequest, Domain domain, Supplier<BigDecimal> balanceSupplier) {
        String domainCurrencyCode = NullSafetyUtils.getCurrency(domain);
        if (!domainCurrencyCode.equals(gameRoundRequest.getCurrencyCode())) {

            String currencyMismatchMessage = String.format("domain.currency=%s does not match " +
                                                                   "%s.currencyCode=%s",
                                                           domainCurrencyCode,
                                                           gameRoundRequest.getClass().getSimpleName(),
                                                           gameRoundRequest.getCurrencyCode());
            BigDecimal balance = balanceSupplier.get();
            throw new CurrencyMismatchException(currencyMismatchMessage, balance, domainCurrencyCode);
        }
    }

    public static <T extends Throwable, R extends GameRoundRequest> void checkUserGuidValidity(R gameRoundRequest, LoginEvent loginEvent,
                                                                                               Class<T> type) {
        checkUserGuidValidity(gameRoundRequest.getOperatorAccountId(), gameRoundRequest.getClass().getSimpleName(), loginEvent, type);
    }

    public static <T extends Throwable> void checkUserGuidValidity(EndRequest endRequest, LoginEvent loginEvent,
                                                                   Class<T> type) {
        checkUserGuidValidity(endRequest.getOperatorAccountId(), endRequest.getClass().getSimpleName(), loginEvent, type);
    }

    @SneakyThrows
    private static <T extends Throwable> void checkUserGuidValidity(String operatorAccountId, String className, LoginEvent loginEvent,
                                                                    Class<T> type) {
        String userGuid = NullSafetyUtils.getUserGuid(loginEvent);
        if (!userGuid.equals(operatorAccountId)) {

            throw (Throwable) type.getDeclaredConstructors()[0].newInstance(String.format("loginEvent.user.guid=%s does not match " +
                                                                                                  "%s.operatorAccountId=%s",
                                                                                          userGuid,
                                                                                          className,
                                                                                          operatorAccountId));
        }
    }

    public static void validateBalanceAdjustmentResponse(BalanceAdjustmentResponse balanceAdjustmentResponse, String currencyCode) {
        EBalanceAdjustmentResponseStatus result = balanceAdjustmentResponse.getResult();

        if (result == EBalanceAdjustmentResponseStatus.INSUFFICIENT_FUNDS) {
            String insufficientFundsMessage = "Player has insufficient funds";
            Long balanceCents = Optional.ofNullable(balanceAdjustmentResponse.getBalanceCents())
                                        .orElseThrow(() -> new InsufficientFundsException(insufficientFundsMessage));
            throw new InsufficientFundsException(insufficientFundsMessage, BalanceUtils.convertToCurrencyUnit(balanceCents),
                                                 currencyCode);
        }

        validateIfEBalanceAdjustmentResponseStatusIsInternalError(result);
    }

    public static void validateBalanceAdjustmentResponse(BalanceAdjustmentResponse balanceAdjustmentResponse) {
        EBalanceAdjustmentResponseStatus result = balanceAdjustmentResponse.getResult();

        validateIfEBalanceAdjustmentResponseStatusIsInternalError(result);
    }

    private static void validateIfEBalanceAdjustmentResponseStatusIsInternalError(EBalanceAdjustmentResponseStatus result) {
        if (result == EBalanceAdjustmentResponseStatus.INTERNAL_ERROR) {
            throw new InternalServerErrorException("Internal Server Error response status during multiBetV1 request");
        }else if(result == EBalanceAdjustmentResponseStatus.TRANSACTION_DATA_VALIDATION_ERROR) {
            throw new TransactionNotFoundException();
        }
    }

    public static PlaceBetResponse buildPlaceBetResponse(PlaceBetRequest placeBetRequest, BigDecimal balanceAmount) {
        PlaceBetResponse placeBetResponse = new PlaceBetResponse();
        placeBetResponse.setErrorCode(ErrorCodes.SUCCESS.getCode());
        placeBetResponse.setBalance(BalanceUtils.buildBalance(placeBetRequest.getCurrencyCode(), balanceAmount));
        placeBetResponse.setResult(
                new PlaceBetResult(placeBetRequest.getGameRoundTransactionId(), new OperatorTransactionSplit(Constants.ZERO_AMOUNT,
                                                                                                             placeBetRequest.getAmount())));

        return placeBetResponse;
    }

    public static <T extends GameRoundRequest> GameRoundResponse buildGameRoundResponse(T gameRoundRequest,
                                                                                        BigDecimal balanceAmount) {
        GameRoundResponse gameRoundResponse = new GameRoundResponse();
        gameRoundResponse.setErrorCode(ErrorCodes.SUCCESS.getCode());
        gameRoundResponse.setBalance(BalanceUtils.buildBalance(gameRoundRequest.getCurrencyCode(), balanceAmount));
        gameRoundResponse.setResult(new GameRoundResult(gameRoundRequest.getGameRoundTransactionId()));

        return gameRoundResponse;
    }

    public static GameRoundResponse buildGameRoundResponse(CreditRequest creditRequest, BigDecimal balanceAmount) {
        GameRoundResponse gameRoundResponse = new GameRoundResponse();
        gameRoundResponse.setErrorCode(ErrorCodes.SUCCESS.getCode());
        gameRoundResponse.setBalance(BalanceUtils.buildBalance(creditRequest.getCurrencyCode(), balanceAmount));
        gameRoundResponse.setResult(new GameRoundResult(creditRequest.getAccountTransactionId()));

        return gameRoundResponse;
    }

    public LoginEvent validGatewaySessionToken(LithiumClientUtils lithiumClientUtils,
                                               GameRoundRequest gameRoundRequest) throws LithiumServiceClientFactoryException {
        String sessionToken = gameRoundRequest.getGatewaySessionToken();
        if (sessionToken == null || sessionToken.isEmpty()) {
            return null;
        }

        try {
            LoginEvent loginEvent = lithiumClientUtils.getSystemLoginEventsClient().findBySessionKey(sessionToken);
            GameRoundUtils.checkGatewaySessionTokenExpiration(gameRoundRequest, loginEvent, null);

            return loginEvent;
        } catch (GatewaySessionTokenExpiredException | Status412LoginEventNotFoundException e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    public LoginEvent validGatewaySessionToken(LithiumClientUtils lithiumClientUtils,
                                               EndRequest endRequest) throws LithiumServiceClientFactoryException {
        String sessionToken = endRequest.getGatewaySessionToken();
        if (sessionToken == null || sessionToken.isEmpty()) {
            return null;
        }

        try {
            LoginEvent loginEvent = lithiumClientUtils.getSystemLoginEventsClient().findBySessionKey(sessionToken);
            GameRoundUtils.checkGatewaySessionTokenExpiration(endRequest, loginEvent);

            return loginEvent;
        } catch (GatewaySessionTokenExpiredException | Status412LoginEventNotFoundException e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    public static void validateAccountTransactionTypeId(CreditRequest creditRequest) {
        if (SHADOW_DEPOSIT_TRANSACTION_TYPE.equals(creditRequest.getAccountTransactionTypeId())) {
            throw new NotSupportedAccountTransactionTypeException(creditRequest.getAccountTransactionTypeId());
        }
    }
}
