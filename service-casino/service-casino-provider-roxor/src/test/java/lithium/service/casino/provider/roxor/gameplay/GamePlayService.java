//package lithium.service.casino.provider.roxor.gameplay;
//
//import org.springframework.context.annotation.Scope;
//import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status402InsufficientFundsException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status438NotFoundException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status440LossLimitException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status441TurnoverLimitException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status442LifetimeDepositException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status443TimeLimitException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status444DepositLimitException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status445GeoLocationException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
//import lithium.service.casino.provider.roxor.api.schema.Money;
//import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
//import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
//import lithium.service.casino.provider.roxor.context.GamePlayContext;
//import org.springframework.stereotype.Component;
//
//import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;
//
//@Component
//@Scope(SCOPE_CUCUMBER_GLUE)
//public class GamePlayService {
//  public SuccessResponse gamePlay(
//      String gamePlayId,
//      String sessionKey,
//      String xForwardFor,
//      String gamePlayRequestJsonString,
//      String locale
//  ) throws
//      Status400BadRequestException,
//      Status401NotLoggedInException,
//      Status438NotFoundException,
//      Status402InsufficientFundsException,
//      Status406DisabledGameException,
//      Status440LossLimitException,
//      Status441TurnoverLimitException,
//      Status442LifetimeDepositException, //TODO where do we validate this
//      Status443TimeLimitException,
//      Status444DepositLimitException, //TODO where do we validate this
//      Status445GeoLocationException, //TODO where do we validate this
//      Status500RuntimeException
//  {
//    GamePlayContext gamePlayContext = new GamePlayContext();
//    gamePlayContext.setRequestJsonString(gamePlayRequestJsonString);
//
//    SuccessResponse.SuccessResponseBuilder responseBuilder = SuccessResponse.builder();
//    responseBuilder.status(SuccessStatus.builder().code(SuccessStatus.Code.OK).build());
//    if (sessionKey != null) {
//      responseBuilder.balance(Money.builder()
//          .currency(gamePlayContext.getDomain().getCurrency())
//          .amount(gamePlayContext.getBalanceAfter())
//          .build());
//    }
//
//    gamePlayContext.setResponse(responseBuilder.build());
//
//    return gamePlayContext.getResponse();
//  }
//}