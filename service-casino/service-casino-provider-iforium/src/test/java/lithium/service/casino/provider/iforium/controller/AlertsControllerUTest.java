package lithium.service.casino.provider.iforium.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.handler.MainExceptionHandler;
import lithium.service.casino.provider.iforium.model.request.AlertWalletCallbackNotificationRequest;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.service.AlertsService;
import lithium.service.casino.provider.iforium.service.impl.AlertsServiceImpl;
import lithium.service.casino.provider.iforium.util.TestMockUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static lithium.service.casino.provider.iforium.constant.TestConstants.AUTHORIZATION;
import static lithium.service.casino.provider.iforium.constant.TestConstants.INVALID_LONG_PARAMETER;
import static lithium.service.casino.provider.iforium.constant.TestConstants.WHITELISTED_IP;
import static lithium.service.casino.provider.iforium.model.response.ErrorCodes.API_AUTHENTICATION_FAILED;
import static lithium.service.casino.provider.iforium.util.Fixtures.fixture;
import static lithium.service.casino.provider.iforium.util.TestMockUtils.SECURITY_CONFIG_UTILS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
class AlertsControllerUTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @BeforeEach
    void setUp() {
        AlertsService alertsService = new AlertsServiceImpl();
        AlertsController alertsController = new AlertsController(alertsService, SECURITY_CONFIG_UTILS);

        mockMvc = MockMvcBuilders.standaloneSetup(alertsController)
                                 .setControllerAdvice(new MainExceptionHandler())
                                 .addPlaceholderValue(TestConstants.ALERT_WALLET_CALLBACK_NOTIFICATION_API_PATH_PROPERTY_NAME,
                                                      TestConstants.ALERT_WALLET_CALLBACK_NOTIFICATION_API_PATH)
                                 .build();

    }

    @SneakyThrows
    @Test
    void alertWalletCallbackNotification_Success() {
        TestMockUtils.mockSuccessIforiumConfig();

        performSuccessAlertWalletCallbackNotification(validAlertWalletCallbackNotificationRequest());
    }

    @SneakyThrows
    @Test
    void alertWalletCallbackNotification_SuccessWhenGatewaySessionTokenIsExpired() {
        TestMockUtils.mockSuccessIforiumConfig();

        performSuccessAlertWalletCallbackNotification(validAlertWalletCallbackNotificationRequest());
    }

    @SneakyThrows
    @Test
    void alertWalletCallbackNotification_SuccessWhenGatewaySessionTokenIsEmpty() {
        TestMockUtils.mockSuccessIforiumConfig();

        AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest = validAlertWalletCallbackNotificationRequest();
        alertWalletCallbackNotificationRequest.setGatewaySessionToken("");

        performSuccessAlertWalletCallbackNotification(alertWalletCallbackNotificationRequest);
    }

    @SneakyThrows
    @Test
    void alertWalletCallbackNotification_SuccessWhenGatewaySessionTokenIsAbsent() {
        TestMockUtils.mockSuccessIforiumConfig();

        AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest = validAlertWalletCallbackNotificationRequest();
        alertWalletCallbackNotificationRequest.setGatewaySessionToken(null);

        performSuccessAlertWalletCallbackNotification(alertWalletCallbackNotificationRequest);
    }

    @ParameterizedTest
    @MethodSource("notWellFormedRequest")
    @SneakyThrows
    void alertWalletCallbackNotification_FailureWhenRequestNotWellFormed(
            AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest) {
        performFailureAlertWalletCallbackNotification(alertWalletCallbackNotificationRequest, API_AUTHENTICATION_FAILED);
    }

    @SneakyThrows
    private void performSuccessAlertWalletCallbackNotification(
            AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest) {
        mockMvc.perform(post(TestConstants.ALERT_WALLET_CALLBACK_NOTIFICATION_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(alertWalletCallbackNotificationRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, ErrorCodes.SUCCESS.getCode())));
    }

    @SneakyThrows
    private void performFailureAlertWalletCallbackNotification(
            AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest, ErrorCodes errorCode) {
        mockMvc.perform(post(TestConstants.ALERT_WALLET_CALLBACK_NOTIFICATION_API_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                                .header(Constants.X_FORWARDED_FOR, WHITELISTED_IP)
                                .content(objectMapper.writeValueAsString(alertWalletCallbackNotificationRequest)))
               .andExpect(status().isOk())
               .andExpect(content().json(fixture(TestConstants.FAILURE_RESPONSE_FIXTURE_PATH, errorCode.getCode())));
    }

    private static Stream<Arguments> notWellFormedRequest() {
        AlertWalletCallbackNotificationRequest tooLongPlatformKey = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setPlatformKey(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongSequence = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setSequence(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongOperatorAccountId = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setOperatorAccountId(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongAlertActionId = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setAlertActionId(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongGamingRegulatorCode = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setGamingRegulatorCode(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongType = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setType(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongMethod = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setMethod(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongData = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setData(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongOperatorAlertReference = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setOperatorAlertReference(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongOperatorAlertActionReference = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setOperatorAlertActionReference(INVALID_LONG_PARAMETER));
        AlertWalletCallbackNotificationRequest tooLongGatewaySessionToken = getAndUpdateAlertWalletCallbackNotificationRequest(
                r -> r.setGatewaySessionToken(INVALID_LONG_PARAMETER));

        return Stream.of(
                Arguments.of(tooLongPlatformKey),
                Arguments.of(tooLongSequence),
                Arguments.of(tooLongOperatorAccountId),
                Arguments.of(tooLongAlertActionId),
                Arguments.of(tooLongGamingRegulatorCode),
                Arguments.of(tooLongType),
                Arguments.of(tooLongMethod),
                Arguments.of(tooLongData),
                Arguments.of(tooLongOperatorAlertReference),
                Arguments.of(tooLongOperatorAlertActionReference),
                Arguments.of(tooLongGatewaySessionToken)
        );
    }

    private static AlertWalletCallbackNotificationRequest getAndUpdateAlertWalletCallbackNotificationRequest(
            Consumer<AlertWalletCallbackNotificationRequest> consumer) {
        AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest = validAlertWalletCallbackNotificationRequest();
        consumer.accept(alertWalletCallbackNotificationRequest);
        return alertWalletCallbackNotificationRequest;
    }

    private static AlertWalletCallbackNotificationRequest validAlertWalletCallbackNotificationRequest() {
        return AlertWalletCallbackNotificationRequest.builder()
                                                     .platformKey(TestConstants.PLATFORM_KEY)
                                                     .sequence(TestConstants.SEQUENCE)
                                                     .timestamp(new Date())
                                                     .gatewaySessionToken(TestConstants.GATEWAY_SESSION_TOKEN)
                                                     .operatorAccountId(TestConstants.OPERATOR_ACCOUNT_ID)
                                                     .source(TestConstants.SOURCE)
                                                     .alertActionId(TestConstants.ALERT_ACTION_ID)
                                                     .operatorAlertActionReference(TestConstants.OPERATOR_ALERT_ACTION_REFERENCE)
                                                     .operatorAlertReference(TestConstants.OPERATOR_ALERT_REFERENCE)
                                                     .gamingRegulatorCode(TestConstants.GAMING_REGULATOR_CODE)
                                                     .type(TestConstants.TYPE)
                                                     .method(TestConstants.METHOD)
                                                     .data(TestConstants.DATA)
                                                     .build();
    }
}
