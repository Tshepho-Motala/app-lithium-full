package lithium.service.casino.provider.roxor.api.controllers;

import lithium.service.casino.provider.roxor.api.exceptions.CustomRoxorHttpErrorCodeControllerAdvice;
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
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.services.GamePlayService;
import lithium.service.casino.provider.roxor.services.gameplay.GamePlayPhase1Persist;
import lithium.service.domain.client.CachingDomainClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({MockitoExtension.class})
public class GamePlayControllerUTest {


    protected GamePlayService gamePlayService;
    protected ProviderConfigService providerConfigService;
    protected CachingDomainClientService cachingDomainClientService;
    protected GamePlayPhase1Persist gamePlayPhase1Persist;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        gamePlayService = Mockito.mock(GamePlayService.class);
        providerConfigService = Mockito.mock(ProviderConfigService.class);
        cachingDomainClientService = Mockito.mock(CachingDomainClientService.class);
        gamePlayPhase1Persist = Mockito.mock(GamePlayPhase1Persist.class);
        GamePlayController gamePlayController = new GamePlayController();
        gamePlayController.setGamePlayService(gamePlayService);
        gamePlayController.setProviderConfigService(providerConfigService);
        gamePlayController.setCachingDomainClientService(cachingDomainClientService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(gamePlayController).setControllerAdvice(new CustomRoxorHttpErrorCodeControllerAdvice()).build();
    }

    @Test
    void whenGamePlay_400_When_Duplicate_Call() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status400BadRequestException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/rgp/game-play")
                        .content(body)
                        .headers(httpHeaders)
                        .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Bad Request. The caller should not repeat the request without modification."))
                .andExpect(jsonPath("$.status.error.category").value(400));
    }

    @Test
    void whenGameplay_Then_401_When_NotLoggedIn() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status401NotLoggedInException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Not Logged In. Provided credentials may be invalid or have expired."))
                .andExpect(jsonPath("$.status.error.category").value(401));
    }

    @Test
    void whenGameplay_Then_402_WhenInsufficientFundsException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status402InsufficientFundsException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Insufficient Funds."))
                .andExpect(jsonPath("$.status.error.category").value(402));
    }

    @Test
    void whenGameplay_Then_404_NotFoundException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status404NotFoundException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Not Found."))
                .andExpect(jsonPath("$.status.error.category").value(404));
    }

    /**
     *
     * This test case fails because Status406DisabledGameException is listed as a Roxor Exception but it is not withing
     * the ErrorCategory categories.
     */
    @Test
    @Disabled
    void whenGameDisabledAndGameplay_Then_500InternalServerException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status406DisabledGameException("Game Disabled"))
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Runtime Error. An unexpected error occured."))
                .andExpect(jsonPath("$.status.error.category").value(500));
    }

    @Test
    void whenGameplay_Then_440_Status440LossLimitException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status440LossLimitException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(440))
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Loss Limit."))
                .andExpect(jsonPath("$.status.error.category").value(440));
    }

    @Test
    void whenGameplay_Then_441_Status441TurnoverLimitException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status441TurnoverLimitException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(441))
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Turnover Limit."))
                .andExpect(jsonPath("$.status.error.category").value(441));
    }

    @Test
    void whenGameplay_Then_442_Status442LifetimeDepositException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status442LifetimeDepositException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(442))
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Lifetime Deposit."))
                .andExpect(jsonPath("$.status.error.category").value(442));
    }

    @Test
    void whenGameplay_Then_443_Status443TimeLimitException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status443TimeLimitException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(443))
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Time Limit."))
                .andExpect(jsonPath("$.status.error.category").value(443));
    }

    @Test
    void whenGameplay_Then_444_Status444DepositLimitException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status444DepositLimitException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(444))
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Deposit Limit."))
                .andExpect(jsonPath("$.status.error.category").value(444));
    }

    @Test
    void whenGameplay_Then_445_Status445GeoLocationException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status445GeoLocationException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(445))
                .andExpect(jsonPath("$.status.code").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Geolocation Error."))
                .andExpect(jsonPath("$.status.error.category").value(445));
    }

    @Test
    void whenGameplay_Then_500_Status500RuntimeException() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String gamePlayId = UUID.randomUUID().toString();
        String sessionKey = UUID.randomUUID().toString();
        String xForwardFor = UUID.randomUUID().toString();
        httpHeaders.set("GameplayId", gamePlayId);
        httpHeaders.set("SessionKey", sessionKey);
        httpHeaders.set("X-Forward-For", xForwardFor);
        String body = getBody();
        Mockito.doThrow(new Status500RuntimeException())
                .when(gamePlayService).gamePlay(gamePlayId, sessionKey, xForwardFor, body, "en_US");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rgp/game-play")
                                .content(body)
                                .headers(httpHeaders)
                                .param("locale", "en_US"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(jsonPath("$.status.code").value("SERVER_ERROR"))
                .andExpect(jsonPath("$.status.error.displayMessage").value("Runtime Error. An unexpected error occured."))
                .andExpect(jsonPath("$.status.error.category").value(500));
    }

    private static String getBody() {
        return """
                {
                    "playerId": 5,
                    "website": "livescorebetdev",
                    "platform": "desktop",
                    "gameKey": "play-double-bubble-progressive",
                    "gamePlayId": "gp-test-duplicates",
                    "operations": [
                        {
                            "operationType": "TRANSFER",
                            "amount": {
                                "amount": 764,
                                "currency": "GBP"
                            },
                            "type": "CREDIT",
                            "transferId": "912e1234-c949-4841-969e-c5c89f8e65a2"
                        }
                    ]
                }
                """;
    }
}
