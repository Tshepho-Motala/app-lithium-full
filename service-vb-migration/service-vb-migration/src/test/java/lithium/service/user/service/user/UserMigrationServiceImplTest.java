/*
package lithium.service.user.service.user;

import com.google.gson.Gson;
import lithium.service.Response;
import lithium.service.user.client.SystemHistoricRegistrationIngestionClient;
import lithium.service.user.client.objects.PlayerBasic;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(properties = { "big.query.users=query" })
class UserMigrationServiceImplTest {

  @InjectMocks
  UserMigrationServiceImpl userMigrationService = new UserMigrationServiceImpl();

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void queueListenerShouldCreateUserTest() {
    SystemHistoricRegistrationIngestionClient systemPlayerBasicClient = Mockito.mock(SystemHistoricRegistrationIngestionClient.class);
    OAuth2AccessToken oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);

    PlayerBasic playerBasic = new PlayerBasic();
    Gson gson = new Gson();

    Mockito.when(systemPlayerBasicClient.createBasicUser(any())).thenReturn(new Response<>(oAuth2AccessToken));
    userMigrationService.setSystemPlayerBasicClient(systemPlayerBasicClient);
    userMigrationService.setGson(gson);
    userMigrationService.queueListerner(playerBasic);

    verify(systemPlayerBasicClient,times(1)).createBasicUser(playerBasic);
  }
}
*/
