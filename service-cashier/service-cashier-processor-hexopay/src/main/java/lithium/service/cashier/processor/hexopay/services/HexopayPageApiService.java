package lithium.service.cashier.processor.hexopay.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AdditionData;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AvsCvcVerificationRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.data.ErrorDetails;
import lithium.service.cashier.processor.hexopay.api.gateway.data.VerificationRequest;
import lithium.service.cashier.processor.hexopay.api.page.PaymentTokenRequest;
import lithium.service.cashier.processor.hexopay.api.page.PaymentTokenResponse;
import lithium.service.cashier.processor.hexopay.api.page.data.Customer;
import lithium.service.cashier.processor.hexopay.api.page.data.Order;
import lithium.service.cashier.processor.hexopay.api.page.data.Settings;
import lithium.service.cashier.processor.hexopay.api.page.data.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;

@Service
@Slf4j
public class HexopayPageApiService {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MessageSource messageSource;
    @Value("${spring.application.name}")
    private String moduleName;

    @Autowired
    private LithiumConfigurationProperties lithiumProperties;

    public DoProcessorResponseStatus initiateWebDeposit(DoProcessorRequest request, DoProcessorResponse response, RestTemplate rest) {
        try
        {
            PaymentTokenRequest tokenRequest = PaymentTokenRequest.builder()
                    .checkout(PaymentTokenRequest.Checkout.builder()
                            .version("2.1")
                            .test(Boolean.parseBoolean(request.getProperty("test")))
                            .transactionType(TransactionType.payment)
                            .trackingId(request.getTransactionId().toString())
                            .attempts(1)
                            .order(Order.builder()
                                    .currency(request.getUser().getCurrency())
                                    .amount(request.inputAmountCents())
                                    .description(request.getProperty("payment_description"))
                                    .trackingId(request.getTransactionId().toString())
                                    .additionalData(AdditionData.builder()
                                            .contract(new String[]{"credit"}).build())
                                    .build())
                            .settings(Settings.builder()
                                    .notificationUrl(gatewayPublicUrl() + "/public/webhook")
                                    .language(request.getUser().getLanguage())
                                    .build())
                            .customer(Customer.builder()
                                    .email(request.getUser().getEmail())
                                    .firstName(request.getUser().getFirstName())
                                    .lastName(request.getUser().getLastName())
                                    .phone(request.getUser().getCellphoneNumber())
                                    .birthDate(new SimpleDateFormat("yyyy-MM-dd").format(request.getUser().getDateOfBirth().toDate()))
                                    .build())
                            .build())
                    .build();

            if (Boolean.parseBoolean(request.getProperty("handle_widget_redirect"))) {
                Settings settings = tokenRequest.getCheckout().getSettings();
                settings.setReturnUrl(gatewayPublicUrl() + "/public/redirect?trx_id=" + request.getTransactionId());
                settings.setFailUrl(gatewayPublicUrl() + "/public/redirect?trx_id=" + request.getTransactionId());
                settings.setCancelUrl(gatewayPublicUrl() + "/public/redirect?trx_id=" + request.getTransactionId());
            }

            if (!request.getProperty("expired_after").isEmpty()) {
                Integer expiredAfter = Integer.parseInt(request.getProperty("expired_after"));
                tokenRequest.getCheckout().getOrder().setExpiredAt(DateTime.now().plusSeconds(expiredAfter/1000).toString());
            }

            if (!request.getProperty("auto_return").isEmpty()) {
                tokenRequest.getCheckout().getSettings().setAutoReturn(Integer.parseInt(request.getProperty("auto_return")));
            }

            if (request.getUser().getResidentialAddress() != null && Boolean.parseBoolean(request.getProperty("send_billing_address"))) {
                tokenRequest.getCheckout().getCustomer().setAddress(request.getUser().getResidentialAddress().getAddressLine1());
                tokenRequest.getCheckout().getCustomer().setCity(request.getUser().getResidentialAddress().getCity());
                tokenRequest.getCheckout().getCustomer().setZip(request.getUser().getResidentialAddress().getPostalCode());
                tokenRequest.getCheckout().getCustomer().setCountry(request.getUser().getResidentialAddress().getCountryCode());
            }

            String avsResponseCodes = request.getProperty("avs_reject_codes");
            String cvvResponseCodes = request.getProperty("cvc_reject_codes");
            if (!avsResponseCodes.isEmpty() || !cvvResponseCodes.isEmpty()) {
                AvsCvcVerificationRequest verificationRequest = AvsCvcVerificationRequest.builder().build();
                verificationRequest.setAvsVerification(!avsResponseCodes.isEmpty() ? VerificationRequest.builder().rejectCodes(avsResponseCodes.trim().split("\\s*,\\s*")).build() : null);
                verificationRequest.setCvcVerification(!cvvResponseCodes.isEmpty() ? VerificationRequest.builder().rejectCodes(cvvResponseCodes.trim().split("\\s*,\\s*")).build() : null);
                tokenRequest.getCheckout().getOrder().getAdditionalData().setAvsCvcVerification(verificationRequest);
            }

            response.addRawRequestLog(tokenRequest.toString());
            response.setPaymentType("card");

            String auth =  request.getProperty("shop_id") + ":" + request.getProperty("secret_key");
            String token = new String(Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1)));

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", "Basic " + getAuthToken(request.getProperty("shop_id"), request.getProperty("secret_key")));
            headers.add("Content-type", "application/json");
            headers.add("X-API-Version","2");

            HttpEntity<PaymentTokenRequest> entity = new HttpEntity<>(tokenRequest, headers);

            log.info("Payment token request (transactionId: " + request.getTransactionId() + "): " + mapper.writeValueAsString(tokenRequest));

            ResponseEntity<Object> exchange = rest.exchange(request.getProperty("payments_page_url") + "/ctp/api/checkouts", HttpMethod.POST, entity, Object.class, new HashMap<>());
            response.addRawResponseLog("Payment token response: " + exchange.getBody());

            if (!exchange.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to initiate Hexopay Web deposit ("+ request.getTransactionId() + ") (" + exchange.getStatusCodeValue() + ") " + exchange.getBody());
                response.setDeclineReason(mapper.convertValue(exchange.getBody(), ErrorDetails.class).getMessage());
                response.setMessage(GeneralError.TRY_AGAIN_LATER.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                response.setErrorCode(GeneralError.TRY_AGAIN_LATER.getCode());
                return DoProcessorResponseStatus.DECLINED;
            }
            log.info("Payment token response (transactionId: " + request.getTransactionId() + "): " + exchange.getBody());

            PaymentTokenResponse tokenResponse = mapper.convertValue(exchange.getBody(), PaymentTokenResponse.class);
            response.setOutputData(1, "paymentToken", tokenResponse.getCheckout().getToken());
            response.setIframeUrl(tokenResponse.getCheckout().getRedirectUrl());
            response.setIframePostData(new HashMap<String, String>(){{put("token", tokenResponse.getCheckout().getToken());}});
            response.setIframeMethod("GET");
            return DoProcessorResponseStatus.IFRAMEPOST_NEXTSTAGE;

        } catch (Exception e) {
            log.error("Failed to initiate payment for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage(), e);
            response.addRawResponseLog(e.getMessage());
            return DoProcessorResponseStatus.FATALERROR;
        }
    }

    private String getAuthToken(String shopId, String secretKey) {
        String auth =  shopId + ":" + secretKey;
        return  new String(Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1)));
    }

    private String gatewayPublicUrl() {
        return lithiumProperties.getGatewayPublicUrl() + "/" + moduleName;
    }
}
