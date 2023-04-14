package lithium.service.casino.provider.roxor.services;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.ServiceCasinoProviderRoxorModuleInfo;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.api.schema.balance.GetPlayerBalanceRequest;
import lithium.service.casino.provider.roxor.services.mocks.BalanceServiceTestMock;
import lithium.service.casino.provider.roxor.services.mocks.CasinoClientServiceTestMock;
import lithium.service.client.LithiumServiceClientFactoryException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
@Ignore
public class BalanceServiceTest {

    public BalanceService balanceService;
    private String sessionKey;
    private GetPlayerBalanceRequest request;

    @Before
    public void setUp() {
        balanceService = new BalanceService();
        sessionKey = "2121";
        request = new GetPlayerBalanceRequest();
    }

    @Test
    public void testWhenUnhandledExceptionOccurs() throws Exception {
        request.setGameKey("gameKey123");
        request.setPlayerId("gamerTester");
        request.setWebsite("www.google.com");

        //when

        //then
        //
        Assertions.assertThatThrownBy(
                () -> {
                    balanceService.balance(sessionKey, null, request);
                }).isInstanceOf(Status500RuntimeException.class);

    }

    @Test
    public void testWhenNoLastLoginEventForSessionKeyFound() throws Status401NotLoggedInException, Status500RuntimeException, Status400BadRequestException, Status512ProviderNotConfiguredException, LithiumServiceClientFactoryException, Status406DisabledGameException {
        request.setGameKey("gameKey123");
        request.setPlayerId("gamerTester");
        request.setWebsite("www.google.com");

        balanceService.setValidationHelper(BalanceServiceTestMock.mockValidationHelper(true));
        balanceService.validationHelper.setModuleInfo(new ServiceCasinoProviderRoxorModuleInfo());
        balanceService.validationHelper.setCachingDomainClientService(null);
        balanceService.validationHelper.setProviderConfigService(null);
        balanceService.validationHelper.setServices(null);

        //when

        //then
        //
        Assertions.assertThatThrownBy(
                () -> {
                    balanceService.balance(sessionKey, null, request);
                }).isInstanceOf(Status401NotLoggedInException.class);

    }


    @Test
    public void givenValidTestGetBalanceSucessfullResponse() throws Status401NotLoggedInException, Status500RuntimeException, Status400BadRequestException, Status512ProviderNotConfiguredException, LithiumServiceClientFactoryException, Status500UnhandledCasinoClientException, Status406DisabledGameException {

        request.setGameKey("gameKey123");
        request.setPlayerId("gamerTester");
        request.setWebsite("www.google.com");

        balanceService.setValidationHelper(BalanceServiceTestMock.mockValidationHelper(false));
        balanceService.validationHelper.setModuleInfo(new ServiceCasinoProviderRoxorModuleInfo());
        balanceService.validationHelper.setCachingDomainClientService(null);
        balanceService.validationHelper.setProviderConfigService(null);
        balanceService.validationHelper.setServices(null);
        balanceService.validationHelper.setUserRepository(null);
        balanceService.setCasinoService(CasinoClientServiceTestMock.mockCasinoClient());


        //when
        SuccessResponse successResponse = balanceService.balance(sessionKey, null, request);

        //then
        Assertions.assertThat(successResponse).isNotNull();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(successResponse.getBalance()).isNotNull();
        softly.assertThat(successResponse.getStatus()).isNotNull();
        softly.assertThat(successResponse.getStatus().getCode()).isNotNull();
        softly.assertThat(successResponse.getBalance().getAmount()).isEqualTo(10L);
        softly.assertAll();
    }
}
