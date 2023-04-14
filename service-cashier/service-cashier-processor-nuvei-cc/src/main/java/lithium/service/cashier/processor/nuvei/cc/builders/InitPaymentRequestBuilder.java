package lithium.service.cashier.processor.nuvei.cc.builders;

import com.safecharge.model.InitPaymentCard;
import com.safecharge.model.InitPaymentPaymentOption;
import com.safecharge.model.InitPaymentThreeD;
import com.safecharge.request.InitPaymentRequest;
import com.safecharge.request.SafechargeBaseRequest;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InitPaymentRequestBuilder extends RequestBuilder {

    private static final String TRANSACTION_ID = "{{trn_id}}";

    private InitPaymentPaymentOption getInitPaymentOptions(DoProcessorRequest request) throws Exception{
        InitPaymentPaymentOption initPaymentPaymentOption = new InitPaymentPaymentOption();
        InitPaymentCard card = new InitPaymentCard();
        if (request.getProcessorAccount() == null) {
            card.setCcTempToken(request.stageInputData(1, "card_token"));
            card.setCardHolderName(request.stageInputData(1, "nameoncard"));
        } else {
            card.setCVV(request.stageInputData(1, "cvv"));
            initPaymentPaymentOption.setUserPaymentOptionId(request.getProcessorAccount().getReference());
        }
        card.setThreeD(getInitThreeDRequest(request));
        initPaymentPaymentOption.setCard(card);
        return initPaymentPaymentOption;
    }

    private InitPaymentThreeD getInitThreeDRequest(DoProcessorRequest request) throws Exception {
        InitPaymentThreeD initPaymentThreeDRequest = new InitPaymentThreeD();
        //Fingerprint
        if (!StringUtil.isEmpty(request.stageInputData(1).get("method_notification_url")) && Boolean.parseBoolean(request.getProperties().get("fingerprint_enabled"))) {
            initPaymentThreeDRequest.setMethodNotificationUrl(gatewayPublicUrl() + "/public/fingerprint/notification/" + request.getTransactionId());
        }
        return initPaymentThreeDRequest;
    }

    public SafechargeBaseRequest getRequest(DoProcessorRequest request, String sessionToken) throws Exception {
        return InitPaymentRequest.builder()
            .addSessionToken(sessionToken)
            .addMerchantInfo(getMerchantInfo(request))
            .addClientUniqueId(request.getTransactionId().toString())
            .addClientRequestId(environment + request.getTransactionId().toString())
            .addCurrency(request.getUser().getCurrency())
            .addAmount(request.inputAmount().toString())
            .addInitPaymentPaymentOption(getInitPaymentOptions(request))
            .addUrlDetails(getUrlDetails(request))
            .addBillingAddress(getBillingAddress(request))
            .addDeviceDetails(getDeviceDetails(request))
            .addUserTokenId(request.getUser().getRealGuid())
            .build();
    }
}
