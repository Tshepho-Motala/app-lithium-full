package lithium.service.casino.provider.iforium.service;

import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.model.request.AlertWalletCallbackNotificationRequest;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.Response;
import lithium.service.casino.provider.iforium.service.impl.AlertsServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({MockitoExtension.class})
class AlertsServiceUTest {

    private AlertsService alertsService;

    @BeforeEach
    void setUp() {
        alertsService = new AlertsServiceImpl();
    }

    @SneakyThrows
    @Test
    void alertWalletCallbackNotification_Success() {
        Response actualResponse = alertsService.alertWalletCallbackNotification(validAlertWalletCallbackNotificationRequest());
        Response expectedResponse = validResponse(ErrorCodes.SUCCESS);
        assertEquals(expectedResponse, actualResponse);
    }

    @SneakyThrows
    @Test
    void alertWalletCallbackNotification_SuccessWhenGatewaySessionTokenIsExpired() {
        Response actualResponse = alertsService.alertWalletCallbackNotification(validAlertWalletCallbackNotificationRequest());
        Response expectedResponse = validResponse(ErrorCodes.SUCCESS);
        assertEquals(expectedResponse, actualResponse);
    }

    @SneakyThrows
    @Test
    void alertWalletCallbackNotification_SuccessWhenGatewaySessionTokenIsEmpty() {
        AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest = validAlertWalletCallbackNotificationRequest();
        alertWalletCallbackNotificationRequest.setGatewaySessionToken("");

        Response actualResponse = alertsService.alertWalletCallbackNotification(alertWalletCallbackNotificationRequest);
        Response expectedResponse = validResponse(ErrorCodes.SUCCESS);
        assertEquals(expectedResponse, actualResponse);
    }

    @SneakyThrows
    @Test
    void alertWalletCallbackNotification_SuccessWhenGatewaySessionTokenIsAbsent() {
        AlertWalletCallbackNotificationRequest alertWalletCallbackNotificationRequest = validAlertWalletCallbackNotificationRequest();
        alertWalletCallbackNotificationRequest.setGatewaySessionToken(null);

        Response actualResponse = alertsService.alertWalletCallbackNotification(alertWalletCallbackNotificationRequest);
        Response expectedResponse = validResponse(ErrorCodes.SUCCESS);

        assertEquals(expectedResponse, actualResponse);
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

    private static Response validResponse(ErrorCodes errorCode) {
        return new Response(errorCode.getCode());
    }
}