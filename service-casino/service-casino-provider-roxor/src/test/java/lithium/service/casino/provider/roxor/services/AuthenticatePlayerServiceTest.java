package lithium.service.casino.provider.roxor.services;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.ServiceCasinoProviderRoxorModuleInfo;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.auth.AuthenticatePlayerRequest;
import lithium.service.casino.provider.roxor.api.schema.auth.AuthenticatePlayerResponse;
import lithium.service.casino.provider.roxor.services.mocks.AuthenticatePlayerServiceTestMock;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AuthenticatePlayerServiceTest {

    public AuthenticatePlayerService authenticatePlayerService;
    private String sessionKey;
    private String playerId;
    private String xForwardFor;


    @Before
    public void setUp(){
        authenticatePlayerService = new AuthenticatePlayerService();
        sessionKey = "3002";
        playerId = "game-play-01";
        xForwardFor = "";
    }

@Test
public void givenValidDataWhenUnhandledExceptionOccurs() throws Exception {

    String description = "";
    AuthenticatePlayerRequest request = new AuthenticatePlayerRequest();
    request.setPlayerId(playerId);
    request.setWebsite("wwww.playsafesa.com");

    //when

    Assertions.assertThatThrownBy(
            () -> {
                 authenticatePlayerService.authenticatePlayer(sessionKey,xForwardFor,request);
            }

            //then
    ).isInstanceOf(Status500RuntimeException.class).as(description);


}

    @Test
    public void givenValidDataWhenNoLoginDataFoundForSessionKey() throws Exception {

        String description = "";
        AuthenticatePlayerRequest request = new AuthenticatePlayerRequest();
        authenticatePlayerService.setValidationHelper(AuthenticatePlayerServiceTestMock.mockNoLastLoginEvent());
        authenticatePlayerService.validationHelper.setModuleInfo(new ServiceCasinoProviderRoxorModuleInfo());
        authenticatePlayerService.validationHelper.setCachingDomainClientService(null);
        authenticatePlayerService.validationHelper.setProviderConfigService(null);
        authenticatePlayerService.validationHelper.setServices(null);
        request.setPlayerId(playerId);
        request.setWebsite("wwww.playsafesa.com");

        //when

        Assertions.assertThatThrownBy(
                () -> {
                    authenticatePlayerService.authenticatePlayer(sessionKey,xForwardFor,request);
                }

                //then
        ).isInstanceOf(Status401NotLoggedInException.class).as(description);


    }

    @Test
    public void givenInValidDataWhenNoPlayerIdProvided() {

        String description = "";
        AuthenticatePlayerRequest request = new AuthenticatePlayerRequest();

        request.setPlayerId(null);
        request.setWebsite("wwww.playsafesa.com");

        //when

        Assertions.assertThatThrownBy(
                () -> {
                    authenticatePlayerService.authenticatePlayer(sessionKey,xForwardFor,request);
                }

                //then
        ).isInstanceOf(Status400BadRequestException.class).as(description);


    }

    @Test
    public void givenValidDataWhenLastLoginIsFound() throws Status401NotLoggedInException, Status500RuntimeException, Status400BadRequestException, Status512ProviderNotConfiguredException, Status406DisabledGameException {

        String description = "";
        AuthenticatePlayerRequest request = new AuthenticatePlayerRequest();
        authenticatePlayerService.setValidationHelper(AuthenticatePlayerServiceTestMock.mockValidationHelper());
        authenticatePlayerService.validationHelper.setModuleInfo(new ServiceCasinoProviderRoxorModuleInfo());
        authenticatePlayerService.validationHelper.setCachingDomainClientService(null);
        authenticatePlayerService.validationHelper.setProviderConfigService(null);
        authenticatePlayerService.validationHelper.setServices(null);
        authenticatePlayerService.validationHelper.setUserRepository(null);
        request.setPlayerId(playerId);
        request.setWebsite("wwww.playsafesa.com");

        //when
        AuthenticatePlayerResponse playerResponse = authenticatePlayerService.authenticatePlayer(sessionKey,xForwardFor,request);

        //then
        Assertions.assertThat(playerResponse).isNotNull();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(playerResponse.getPlayer()).isNotNull();
        softly.assertThat(playerResponse.getStatus()).isNotNull();

        softly.assertAll();

    }


























    @DataProvider
    public static Object[][] authPositiveData() {
        // @formatter:off
        return new Object[][] {
                { "ValidRequests/valid2.json", "Valid Request"},
                { "ValidRequests/valid3.json", "Valid Request"},
                /* ... */
        };
        // @formatter:on
    }
}
