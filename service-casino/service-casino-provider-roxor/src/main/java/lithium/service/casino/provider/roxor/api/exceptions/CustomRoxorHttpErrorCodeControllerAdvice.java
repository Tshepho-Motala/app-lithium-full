package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.exceptions.ErrorCodeException;
import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;
import lithium.service.casino.provider.roxor.api.schema.error.ErrorCode;
import lithium.service.casino.provider.roxor.api.schema.error.ErrorObject;
import lithium.service.casino.provider.roxor.api.schema.error.ErrorStatus;
import lithium.service.casino.provider.roxor.api.schema.error.RoxorHttpErrorCodeResponse;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.GamePlayRequest;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRequestRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * A configuration bean that registers a custom HTTP status code on the response if the
 * exception raised is a base of {@link ErrorCodeException}.
 *
 * @see ErrorCodeException
 */
@RestControllerAdvice
@Slf4j
public class CustomRoxorHttpErrorCodeControllerAdvice {
    @Autowired
    GamePlayRequestRepository gamePlayRequestRepository;
    @Autowired
    OperationRepository operationRepository;
    @Autowired
    ValidationHelper validationHelper;

    @ExceptionHandler({RoxorErrorCodeException.class})
    public RoxorHttpErrorCodeResponse handleErrorCodeException(RoxorErrorCodeException ex, HttpServletResponse response) {
        response.setStatus(ex.getCode());
        return buildRoxorHttpErrorCodeResponse(ex.getCode(), ex.getContext(), ex);
    }

    @ExceptionHandler({ErrorCodeException.class})
    public RoxorHttpErrorCodeResponse handleErrorCodeException(ErrorCodeException ex, HttpServletResponse response) {
        response.setStatus(ex.getCode());
        return buildRoxorHttpErrorCodeResponse(ex.getCode(), ex.getContext(), ex);
    }

    private RoxorHttpErrorCodeResponse buildRoxorHttpErrorCodeResponse(int code, Object context, Exception ex) {
        String logMsg = "ErrorCodeException " + code + " " + ex.getMessage() + (context != null ? " " + context : "");

        RoxorHttpErrorCodeResponse r = RoxorHttpErrorCodeResponse.builder().build();
        ErrorCode errorCode;
        if (code >= 500) {
            errorCode = ErrorCode.SERVER_ERROR;
            log.error(logMsg, ex);
        } else {
            errorCode = ErrorCode.CLIENT_ERROR;
            log.debug(logMsg);
        }

        ErrorCategory ec = ErrorCategory.fromCategory(code);
        if(ec == null){
            ec = ErrorCategory.EC_500;
        }
        r.setStatus(ErrorStatus.builder()
                .code(errorCode.name())
                .error(ErrorObject.builder()
                        .category(ec.category())
                        .displayMessage(ec.displayMessage())
                        .build()
                ).build()
        );

        if (context != null) {
            GamePlayContext gamePlayContext = (GamePlayContext) context;
            if (gamePlayContext.getGamePlayRequestEntity() != null) {
                gamePlayContext.getGamePlayRequestEntity().setStatus(GamePlayRequest.Status.ERROR);
                gamePlayContext.getGamePlayRequestEntity().setStatusReason(gamePlayContext.getGamePlayRequestErrorReason());
                gamePlayContext.setGamePlayRequestEntity(gamePlayRequestRepository.save(gamePlayContext.getGamePlayRequestEntity()));

                gamePlayContext.getOperationEntityList().forEach(
                        o -> o.setStatus(Operation.Status.ERROR)
                );

                gamePlayContext.setOperationEntityList(operationRepository.saveAll(gamePlayContext.getOperationEntityList()));

                if ((ec.category() >= 400) && (ec.category() < 500)) {
                    log.info("GamePlay Context : " + gamePlayContext);
                } else {
                    log.error("GamePlay Context : " + gamePlayContext);
                }

            }
        }

        return r;
    }
}
