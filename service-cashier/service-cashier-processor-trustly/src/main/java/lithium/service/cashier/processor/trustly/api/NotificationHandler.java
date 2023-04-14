
package lithium.service.cashier.processor.trustly.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.processor.trustly.api.data.Method;
import lithium.service.cashier.processor.trustly.api.data.ResponseStatus;
import lithium.service.cashier.processor.trustly.api.data.notification.Notification;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.AccountNotificationData;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.CancelNotificationData;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.CreditData;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.DebitNotificationData;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.PayoutConfirmationData;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.PendingNotificationData;
import lithium.service.cashier.processor.trustly.api.data.requestbuilders.NotificationResponse;
import lithium.service.cashier.processor.trustly.api.data.response.TrustlyResponse;
import lithium.service.cashier.processor.trustly.api.exceptions.TrustlySignatureException;
import lithium.service.cashier.processor.trustly.api.security.SignatureHandler;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

public class NotificationHandler {

    public static Notification handleNotification(final String notificationJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        final JsonNode requestNode = mapper.readTree(notificationJson);
        final JsonNode paramsNode = requestNode.get("params");
        final JsonNode dataNode = paramsNode.get("data");

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Notification notification = mapper.readValue(notificationJson, Notification.class);

        if (notification.getMethod() == Method.CREDIT) notification.getParams().setData(mapper.readValue(dataNode.toString(), CreditData.class));
        else if (notification.getMethod() == Method.ACCOUNT) notification.getParams().setData(mapper.readValue(dataNode.toString(), AccountNotificationData.class));
        else if (notification.getMethod() == Method.CANCEL) notification.getParams().setData(mapper.readValue(dataNode.toString(), CancelNotificationData.class));
        else if (notification.getMethod() == Method.DEBIT) notification.getParams().setData(mapper.readValue(dataNode.toString(), DebitNotificationData.class));
        else if (notification.getMethod() == Method.PENDING) notification.getParams().setData(mapper.readValue(dataNode.toString(), PendingNotificationData.class));
        else if (notification.getMethod() == Method.PAYOUT_CONFIRMATION) notification.getParams().setData(mapper.readValue(dataNode.toString(), PayoutConfirmationData.class));
        else throw new Exception("Unsupported notification method received " + notification.getMethod().toString());

        if (dataNode.has("attributes")) {
            if (dataNode.get("attributes").isNull()) {
                notification.getParams().getData().setAttributes(new HashMap<>());
            }
        }
        return notification;
    }

    public static void verifyNotification(final Notification notification, PublicKey publicKey) {
        if (!SignatureHandler.verifyNotificationSignature(notification,publicKey)) {
            throw new TrustlySignatureException("Incoming data signature is not valid");
        }
    }

    public static TrustlyResponse prepareNotificationResponse(final Method method, final String uuid, final ResponseStatus status, PrivateKey privateKey) throws Exception {
        final TrustlyResponse response = new NotificationResponse.Build(method, uuid, status)
                .getResponse();

        SignatureHandler.signNotificationResponse(response, privateKey);

        return response;
    }
}
