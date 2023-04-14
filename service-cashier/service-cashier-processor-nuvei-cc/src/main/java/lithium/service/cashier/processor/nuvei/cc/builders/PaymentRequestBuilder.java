package lithium.service.cashier.processor.nuvei.cc.builders;

import com.safecharge.model.BrowserDetails;
import com.safecharge.model.Card;
import com.safecharge.model.DynamicDescriptor;
import com.safecharge.model.InitPaymentCard;
import com.safecharge.model.InitPaymentThreeD;
import com.safecharge.model.PaymentOption;
import com.safecharge.model.ThreeD;
import com.safecharge.model.V2AdditionalParams;
import com.safecharge.request.InitPaymentRequest;
import com.safecharge.request.PaymentRequest;
import com.safecharge.request.SafechargeBaseRequest;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.util.StringUtil;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentRequestBuilder extends RequestBuilder {

    private PaymentOption getPaymentOptions(DoProcessorRequest request) throws Exception{
        PaymentOption paymentOption = new PaymentOption();
        Card card = new Card();
        if (request.getProcessorAccount() == null) {
            card.setCcTempToken(request.stageInputData(1, "card_token"));
            card.setCardHolderName(request.stageInputData(1, "nameoncard"));
        } else {
            card.setCVV(request.stageInputData(1).get("cvv"));
            paymentOption.setUserPaymentOptionId(request.getProcessorAccount().getReference());
        }
        card.setThreeD(getThreeDRequest(request));
        paymentOption.setCard(card);
        return paymentOption;
    }

    private BrowserDetails getBrowserDetails(DoProcessorRequest request) throws Exception {
        BrowserDetails browserDetails = new BrowserDetails();
        browserDetails.setAcceptHeader(Optional.ofNullable(request.stageInputData(1).get("browser_accept")).orElse("*/*"));
        browserDetails.setIp(request.getUser().getLastKnownIP());
        browserDetails.setJavaEnabled(request.stageInputData(1,"java_enabled"));
        browserDetails.setJavaScriptEnabled(request.stageInputData(1,"java_script_enabled"));
        browserDetails.setLanguage(request.getUser().getLanguage());
        browserDetails.setColorDepth(request.stageInputData(1,"color_depth"));
        browserDetails.setScreenHeight(request.stageInputData(1,"screen_height"));
        browserDetails.setScreenWidth(request.stageInputData(1,"screen_width"));
        browserDetails.setTimeZone(request.stageInputData(1,"time_zone"));
        browserDetails.setUserAgent(request.getUser().getLastKnownUserAgent());
        return browserDetails;
    }

    private V2AdditionalParams getV2AdditionalParams(DoProcessorRequest request) throws Exception {
        V2AdditionalParams additionalParams = new V2AdditionalParams();
        additionalParams.setChallengeWindowSize("05");
        additionalParams.setChallengePreference(request.getProperty("challenge_preference"));
        return additionalParams;
    }

    private ThreeD getThreeDRequest(DoProcessorRequest request) throws Exception  {
        ThreeD threeDRequest = new ThreeD();
        Boolean three3DS2 = Boolean.parseBoolean(request.stageOutputData(1).get("3DS2_supported"));
        if (BooleanUtils.isTrue(three3DS2) && !Boolean.parseBoolean(request.stageOutputData(2).get("ChallengeFlow"))) {
            threeDRequest.setMethodCompletionInd(Optional.ofNullable(request.stageInputData(2).get("method_completion_ind")).orElse(request.stageOutputData(1).get("method_completion_ind")));
            threeDRequest.setVersion(request.stageOutputData(1).get("3DSecure_version"));
            threeDRequest.setNotificationURL(gatewayPublicUrl() + "/public/" + request.getTransactionId() + "/threeD/v2");
            threeDRequest.setMerchantURL(request.stageInputData(1).get("merchant_url"));
            //device channel 02 Browser. there is 01 – App-based (only for SDK implementation, not in the scope of this document) to check
            threeDRequest.setPlatformType("02");
            threeDRequest.setBrowserDetails(getBrowserDetails(request));
            threeDRequest.setV2AdditionalParams(getV2AdditionalParams(request));
            return threeDRequest;
        } else if (BooleanUtils.isFalse(three3DS2)) {
            if (Boolean.parseBoolean(request.stageOutputData(2).get("ChallengeFlow"))) {
                threeDRequest.setPaResponse(request.stageOutputData(2).get("PaRes"));
            }
            return threeDRequest;
        }
        return null;
    }

    public SafechargeBaseRequest getRequest(DoProcessorRequest request) throws Exception {
        PaymentRequest.Builder paymentRequestBuilder = PaymentRequest.builder()
            .addSessionToken(request.stageOutputData(1, "session_token"))
            .addMerchantInfo(getMerchantInfo(request))
            .addClientUniqueId(request.getTransactionId().toString())
            .addClientRequestId(environment + request.getTransactionId().toString())
            .addCurrency(request.getUser().getCurrency())
            .addAmount(request.inputAmount().toString())
            .addPaymentOption(getPaymentOptions(request))
            .addBillingDetails(getBillingAddress(request))
            .addDeviceDetails(getDeviceDetails(request))
            .addUserTokenId(request.getUser().getRealGuid())
            .addUserDetails(getUserDetails(request))
            .addProductId(request.getProperties().get("product_id"))
            .addCustomSiteName(request.getProperties().get("merchant_site_name"));

        if (!StringUtil.isEmpty(request.getProperties().get("merchant_name"))) {
            paymentRequestBuilder.addDynamicDescriptor(new DynamicDescriptor(request.getProperties().get("merchant_name"), request.getProperties().get("merchant_phone")));
        }

        if (request.getProcessorReference() != null) {
            //liability shift handling payment transaction id
            paymentRequestBuilder = paymentRequestBuilder.addRelatedTransactionId(request.getProcessorReference());
        } else if (request.getAdditionalReference() != null) {
            //init payment transaction id
            paymentRequestBuilder = paymentRequestBuilder.addRelatedTransactionId(request.getAdditionalReference());
        }
        return paymentRequestBuilder.build();
    }
}
