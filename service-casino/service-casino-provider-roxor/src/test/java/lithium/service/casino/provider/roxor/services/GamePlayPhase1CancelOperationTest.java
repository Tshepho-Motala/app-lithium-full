//package lithium.service.casino.provider.roxor.services;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.tngtech.java.junit.dataprovider.DataProvider;
//import com.tngtech.java.junit.dataprovider.DataProviderRunner;
//import com.tngtech.java.junit.dataprovider.UseDataProvider;
//import lithium.exceptions.Status405UserDisabledException;
//import lithium.service.casino.provider.roxor.ServiceCasinoProviderRoxorModuleInfo;
//import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
//import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
//import lithium.service.casino.provider.roxor.api.schema.gameplay.GamePlayRequest;
//import lithium.service.casino.provider.roxor.config.Status500ProviderNotConfiguredException;
//import lithium.service.casino.provider.roxor.context.GamePlayContext;
//import lithium.service.casino.provider.roxor.services.gameplay.GamePlayPhase2Validate;
//import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
//import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
//import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
//import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
//import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
//import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
//import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
//import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
//import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
//import org.assertj.core.api.Assertions;
//import org.assertj.core.api.SoftAssertions;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//
//@RunWith(DataProviderRunner.class)
//public class GamePlayPhase1CancelOperationTest {
//
//
//    private String sessionKey;
//    private String gamePlayId;
//    private String xForwardFor;
//    private GamePlayRequest gamePlayRequest;
//    private String locale;
//    private ObjectMapper objectMapper = new ObjectMapper();
//    public GamePlayPhase2Validate gamePlayPhase1Validate;
//
//    @Before
//    public void setUp() throws IOException {
//        gamePlayPhase1Validate = new GamePlayPhase2Validate();
//        sessionKey = "291";
//        gamePlayId = "game-play-01";
//        xForwardFor = "";
//        locale = "en_US";
//    }
//
//    @DataProvider
//    public static Object[][] positiveData() {
//        // @formatter:off
//        return new Object[][] {
//                { "ValidRequests/valid2.json", "Valid Request"},
//                /* ... */
//        };
//        // @formatter:on
//    }
//
//    private GamePlayRequest parseData(String input) throws IOException {
//        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
//        objectMapper.configure(
//                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        return objectMapper.readValue(input, GamePlayRequest.class);
//    }
//
//    @Test
//    @UseDataProvider("positiveData")
//    public void givenValidDataWhenValidateGameplayRequestWithOperationTypeCancelThenReceiveValidContext(String filePath, String description) throws Status492DailyLossLimitReachedException, Status495MonthlyWinLimitReachedException, Status400BadRequestException, Status494DailyWinLimitReachedException, Status496PlayerCoolingOffException, Status493MonthlyLossLimitReachedException, Status401NotLoggedInException, Status491PermanentSelfExclusionException, Status405UserDisabledException, Status500ProviderNotConfiguredException, Status500LimitInternalSystemClientException, Status500RuntimeException, Status500UserInternalSystemClientException, Status490SoftSelfExclusionException, IOException {
//        //given
//        File resource = new ClassPathResource(filePath).getFile();
//        String fullGamePlayRequest = new String(Files.readAllBytes(resource.toPath()));
//        GamePlayRequest initialInput = parseData(fullGamePlayRequest);
//        gamePlayPhase1Validate.setModuleInfo(new ServiceCasinoProviderRoxorModuleInfo());
//        gamePlayPhase1Validate.setGamePlayRepository(GamePlayPhase1PositiveMocks.mockGamePlayRepository());
//        gamePlayPhase1Validate.setValidationHelper(GamePlayPhase1TestNegativeMocks.mockValidationHelper());
//        gamePlayPhase1Validate.setUserApiInternalClientService(GamePlayPhase1TestNegativeMocks.mockUserApiInternalClientService());
//        gamePlayPhase1Validate.setLimits(GamePlayPhase1TestNegativeMocks.mockLimitInternalSystemService());
//        gamePlayPhase1Validate.setOperationTypeRepository(GamePlayPhase1PositiveMocks.mockOperationTypeRepository());
//        gamePlayPhase1Validate.setOperationRepository(GamePlayPhase1PositiveMocks.mockOperationRepository());
//        gamePlayPhase1Validate.setTypeRepository(GamePlayPhase1PositiveMocks.mockTypeRepository());
//
//        Assertions.assertThatThrownBy(()->{gamePlayPhase1Validate.validateGamePlayRequest(sessionKey, gamePlayId, xForwardFor, initialInput, locale);}).isInstanceOf(Status400BadRequestException.class);
//    }
//
//    @Test
//    @UseDataProvider("positiveData")
//    public void givenValidDataWhenValidateGameplayRequestWithOperationTypeCancelThenReceiveValidContextPt2(String filePath, String description) throws Status492DailyLossLimitReachedException, Status495MonthlyWinLimitReachedException, Status400BadRequestException, Status494DailyWinLimitReachedException, Status496PlayerCoolingOffException, Status493MonthlyLossLimitReachedException, Status401NotLoggedInException, Status491PermanentSelfExclusionException, Status405UserDisabledException, Status500ProviderNotConfiguredException, Status500LimitInternalSystemClientException, Status500RuntimeException, Status500UserInternalSystemClientException, Status490SoftSelfExclusionException, IOException {
//        //given
//        File resource = new ClassPathResource(filePath).getFile();
//        String fullGamePlayRequest = new String(Files.readAllBytes(resource.toPath()));
//        GamePlayRequest initialInput = parseData(fullGamePlayRequest);
//        gamePlayPhase1Validate.setModuleInfo(new ServiceCasinoProviderRoxorModuleInfo());
//        gamePlayPhase1Validate.setGamePlayRepository(GamePlayPhase1PositiveMocks.mockGamePlayRepository());
//        gamePlayPhase1Validate.setValidationHelper(GamePlayPhase1TestNegativeMocks.mockValidationHelper());
//        gamePlayPhase1Validate.setUserApiInternalClientService(GamePlayPhase1TestNegativeMocks.mockUserApiInternalClientService());
//        gamePlayPhase1Validate.setLimits(GamePlayPhase1TestNegativeMocks.mockLimitInternalSystemService());
//        gamePlayPhase1Validate.setOperationTypeRepository(GamePlayPhase1PositiveMocks.mockOperationTypeRepository());
//        gamePlayPhase1Validate.setOperationRepository(GamePlayPhase1PositiveMocks.mockOperationRepositoryPt2());
//        gamePlayPhase1Validate.setTypeRepository(GamePlayPhase1PositiveMocks.mockTypeRepository());
//
//        //when
//        GamePlayContext gamePlayContext = gamePlayPhase1Validate.validateGamePlayRequest(sessionKey, gamePlayId, xForwardFor, initialInput, locale);
//        //then
//
//        Assertions.assertThat(gamePlayContext).isNotNull();
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(gamePlayContext.getRequest()).isNotNull();
//        softly.assertThat(gamePlayContext.getUserGuid()).isNotNull();
//        softly.assertThat(gamePlayContext.getResponse()).isNotNull();
//        softly.assertThat(gamePlayContext.getGamePlayId()).isNotNull();
//        softly.assertThat(gamePlayContext.getLithiumGame()).isNotNull();
//        softly.assertThat(gamePlayContext.getSessionKey()).isNotNull();
//        softly.assertThat(gamePlayContext.getLoginEvent()).isNotNull();
//        softly.assertThat(gamePlayContext.getDomain()).isNotNull();
//        softly.assertThat(gamePlayContext.getLocale()).isNotNull();
//        softly.assertThat(gamePlayContext.getLoginEvent().getUser().getGuid()).isNotNull();
//        softly.assertAll();
//    }
//}
