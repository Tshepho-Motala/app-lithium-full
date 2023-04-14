package lithium.service.casino.provider.iforium.handler;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.controller.AlertsController;
import lithium.service.casino.provider.iforium.controller.BalanceController;
import lithium.service.casino.provider.iforium.controller.GameRoundController;
import lithium.service.casino.provider.iforium.controller.SessionController;
import lithium.service.casino.provider.iforium.exception.AccountNotFoundException;
import lithium.service.casino.provider.iforium.exception.CurrencyMismatchException;
import lithium.service.casino.provider.iforium.exception.GameRoundNotFoundException;
import lithium.service.casino.provider.iforium.exception.GatewaySessionTokenExpiredException;
import lithium.service.casino.provider.iforium.exception.InsufficientFundsException;
import lithium.service.casino.provider.iforium.exception.InvalidGameException;
import lithium.service.casino.provider.iforium.exception.InvalidGatewaySessionTokenException;
import lithium.service.casino.provider.iforium.exception.LossLimitReachedException;
import lithium.service.casino.provider.iforium.exception.NotSupportedAccountTransactionTypeException;
import lithium.service.casino.provider.iforium.exception.PropertyNotConfiguredException;
import lithium.service.casino.provider.iforium.exception.SessionKeyExpiredException;
import lithium.service.casino.provider.iforium.exception.SessionLengthLimitReachedException;
import lithium.service.casino.provider.iforium.exception.SessionTokenExpiredException;
import lithium.service.casino.provider.iforium.exception.TransactionNotFoundException;
import lithium.service.casino.provider.iforium.exception.UpstreamValidationFailedException;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.Response;
import lithium.service.casino.provider.iforium.util.BalanceUtils;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {BalanceController.class, GameRoundController.class, SessionController.class,
                                         AlertsController.class})
@ResponseStatus(HttpStatus.OK)
public class MainExceptionHandler {

    @ExceptionHandler({InvalidGameException.class})
    public Response handleGameNotFoundExceptions(InvalidGameException e) {
        logException(e);
        return new BalanceResponse(ErrorCodes.GAME_NOT_FOUND.getCode(),
                                   BalanceUtils.buildBalance(e.getCurrencyCode(), e.getBalanceAmount()));
    }

    @ExceptionHandler({GameRoundNotFoundException.class})
    public Response handleGameRoundNotFound(GameRoundNotFoundException e) {
        logException(e);
        return new BalanceResponse(ErrorCodes.GAME_ROUND_NOT_FOUND.getCode(),
                                   BalanceUtils.buildBalance(e.getCurrencyCode(), e.getBalanceAmount()));
    }

    @ExceptionHandler({CurrencyMismatchException.class})
    public Response handleCurrencyMismatchExceptions(CurrencyMismatchException e) {
        logException(e);
        return new BalanceResponse(ErrorCodes.CURRENCY_MISMATCH.getCode(),
                                   BalanceUtils.buildBalance(e.getCurrencyCode(), e.getBalanceAmount()));
    }

    @ExceptionHandler({TransactionNotFoundException.class})
    public Response handleCurrencyMismatchExceptions(TransactionNotFoundException e) {
        logException(e);
        return new BalanceResponse(ErrorCodes.TRANSACTION_NOT_FOUND.getCode(),
                                   BalanceUtils.buildBalance(e.getCurrencyCode(), e.getBalanceAmount()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, NumberFormatException.class,
                       UpstreamValidationFailedException.class, AuthorizationServiceException.class, AuthenticationServiceException.class,
                       NotSupportedAccountTransactionTypeException.class})
    public Response handleAuthenticationFailedExceptions(Exception e) {
        logException(e);
        return new Response(ErrorCodes.API_AUTHENTICATION_FAILED.getCode());
    }

    @ExceptionHandler({Status512ProviderNotConfiguredException.class, Status550ServiceDomainClientException.class,
                       Status411UserNotFoundException.class, SessionKeyExpiredException.class, AccountNotFoundException.class})
    public Response handleAccountNotFoundExceptions(Exception e) {
        logException(e);
        return new Response(ErrorCodes.ACCOUNT_NOT_FOUND.getCode());
    }

    @ExceptionHandler({SessionTokenExpiredException.class, Status412LoginEventNotFoundException.class,
                       InvalidGatewaySessionTokenException.class})
    public Response handleSessionNotFoundException(Exception e) {
        logException(e);
        return new Response(ErrorCodes.SESSION_NOT_FOUND.getCode());
    }

    @ExceptionHandler({GatewaySessionTokenExpiredException.class})
    public Response handleSessionTokenExpiredException(GatewaySessionTokenExpiredException e) {
        logException(e);
        return new BalanceResponse(ErrorCodes.SESSION_NOT_FOUND.getCode(),
                                   BalanceUtils.buildBalance(e.getCurrencyCode(), e.getBalanceAmount()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public Response handleInsufficientFundsException(InsufficientFundsException e) {
        logException(e);
        if (e.getBalanceAmount() == null) {
            return new Response(ErrorCodes.INSUFFICIENT_FUNDS.getCode());
        }

        return new BalanceResponse(ErrorCodes.INSUFFICIENT_FUNDS.getCode(),
                                   BalanceUtils.buildBalance(e.getCurrencyCode(), e.getBalanceAmount()));
    }

    @ExceptionHandler(LossLimitReachedException.class)
    public Response handleLossLimitReachedException(LossLimitReachedException e) {
        logException(e);
        return new BalanceResponse(ErrorCodes.LOSS_LIMIT.getCode(),
                                   BalanceUtils.buildBalance(e.getCurrencyCode(), e.getBalanceAmount()));
    }

    @ExceptionHandler(SessionLengthLimitReachedException.class)
    public Response handleSessionLengthLimitReachedException(SessionLengthLimitReachedException e) {
        logException(e);
        return new BalanceResponse(ErrorCodes.SESSION_LIMIT.getCode(),
                                   BalanceUtils.buildBalance(e.getCurrencyCode(), e.getBalanceAmount()));
    }

    @ExceptionHandler({PropertyNotConfiguredException.class, Exception.class})
    public Response handleException(Exception e) {
        logException(e);
        return new Response(ErrorCodes.UNKNOWN_ERROR.getCode());
    }

    private static void logException(Exception e) {
        log.error("Exception is occurred, exception=" + e.getClass().getSimpleName(), e);
    }
}
