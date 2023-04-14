package lithium.service.casino.provider.roxor.gameplay;

import static org.junit.Assert.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.UUID;
import lithium.service.casino.provider.roxor.api.controllers.GamePlayController;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status402InsufficientFundsException;
import lithium.service.casino.provider.roxor.api.exceptions.Status404NotFoundException;
import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
import lithium.service.casino.provider.roxor.api.exceptions.Status440LossLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status441TurnoverLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status442LifetimeDepositException;
import lithium.service.casino.provider.roxor.api.exceptions.Status443TimeLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status444DepositLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status445GeoLocationException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lithium.service.casino.provider.roxor.api.schema.SuccessStatus.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CucumberGamePlayStepDefinitions {

  @Autowired
  private GamePlayController gamePlayController;

  private String gamePlayId = UUID.randomUUID().toString();
  private String sessionKey = UUID.randomUUID().toString();
  private String locale = "en_US";
  private String xForwardFor = null;
  private String gamePlayRequestJsonString = null;
  private SuccessResponse response = null;

  @Given( "normal casino bet request" )
  public void normalBetRequest() {
    gamePlayRequestJsonString =
        "" + "{\"playerId\":\"27cb968a-13fb-4ea0-9ecc-bbfb22c8b583\"," + "\"website\":\"livescore_uk\"," + "\"gameKey\":\"play-banghai\","
            + "\"gamePlayId\":" + gamePlayId + "," + "\"operations\":[" + "{\"operationType\":\"START_GAME_PLAY\"},"
            + "{\"operationType\":\"TRANSFER\",\"amount\":{\"currency\":\"GBP\",\"amount\":9000},\"transferId\":\"91f40a8d-b56f-4304-a6ec-0023a50cbd00\",\"type\":\"DEBIT\"},"
            + "{\"operationType\":\"TRANSFER\",\"amount\":{\"currency\":\"GBP\",\"amount\":4500},\"transferId\":\"fe66c46f-cdff-4fa4-a733-d0cbca7a9608\",\"type\":\"CREDIT\"},"
            + "{\"operationType\":\"FINISH_GAME_PLAY\"}]}";
  }

  @When( "RGP places a normal bet request" )
  public void placeNormalBet()
  throws
      Status404NotFoundException,
      Status500RuntimeException,
      Status401NotLoggedInException,
      Status400BadRequestException,
      Status444DepositLimitException,
      Status443TimeLimitException,
      Status406DisabledGameException,
      Status441TurnoverLimitException,
      Status445GeoLocationException,
      Status402InsufficientFundsException,
      Status442LifetimeDepositException,
      Status440LossLimitException
  {
    response = gamePlayController.gamePlay(gamePlayId, sessionKey, xForwardFor, gamePlayRequestJsonString, locale);
  }

  @Then( "Lithium should have a successful response" )
  public void successResponse(String expectedAnswer) {

    assertEquals(response, SuccessResponse.builder().status(SuccessStatus.builder().code(Code.OK).build()).build());
  }
}
