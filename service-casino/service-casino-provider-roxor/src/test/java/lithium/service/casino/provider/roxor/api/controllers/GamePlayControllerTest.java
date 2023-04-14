package lithium.service.casino.provider.roxor.api.controllers;

import java.util.UUID;
import lithium.service.casino.provider.roxor.ServiceCasinoProviderRoxorApplication;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.services.GamePlayService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ActiveProfiles( "test" )
//@RunWith( SpringRunner.class )
//@SpringBootTest( classes = ServiceCasinoProviderRoxorApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT )
public class GamePlayControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @MockBean
  private GamePlayService gamePlayService;

  @Before
  public void setup() {
//    given(this.gamePlayService.
    //        gamePlay()
    //    ).willReturn(
    //        new VehicleDetails("Honda", "Civic"));
  }

//  @Test
  public void successNormalGamePlay()
  throws Exception
  {
    String gamePlayId = UUID.randomUUID().toString();
    String sessionKey = UUID.randomUUID().toString();
    String xForwardFor = null;
    String gamePlayRequestJsonString =
        "" + "{\"playerId\":\"27cb968a-13fb-4ea0-9ecc-bbfb22c8b583\"," + "\"website\":\"livescore_uk\"," + "\"gameKey\":\"play-banghai\","
            + "\"gamePlayId\":" + gamePlayId + "," + "\"operations\":[" + "{\"operationType\":\"START_GAME_PLAY\"},"
            + "{\"operationType\":\"TRANSFER\",\"amount\":{\"currency\":\"GBP\",\"amount\":9000},\"transferId\":\"91f40a8d-b56f-4304-a6ec-0023a50cbd00\",\"type\":\"DEBIT\"},"
            + "{\"operationType\":\"TRANSFER\",\"amount\":{\"currency\":\"GBP\",\"amount\":4500},\"transferId\":\"fe66c46f-cdff-4fa4-a733-d0cbca7a9608\",\"type\":\"CREDIT\"},"
            + "{\"operationType\":\"FINISH_GAME_PLAY\"}]}";
    String locale = "en_US";

    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    headers.add("GameplayId", gamePlayId);
    headers.add("SessionKey", sessionKey);
    headers.add("X-Forward-For", xForwardFor);
    headers.add("locale", locale);
    HttpEntity<String> entity = new HttpEntity<>(gamePlayRequestJsonString, headers);

//    this.restTemplate.postForEntity("/rgp/game-play", entity, SuccessResponse.class);
  }
}
