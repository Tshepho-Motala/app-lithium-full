package lithium.service.cashier.processor.trustly;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.frontend.ProcessorAccountResponseStatus;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.trustly.api.SignedAPI;
import lithium.service.cashier.processor.trustly.api.data.TrustlyAccountErrors;
import lithium.service.cashier.processor.trustly.api.data.TrustlyDepositErrors;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.requestbuilders.AccountPayout;
import lithium.service.cashier.processor.trustly.api.data.requestbuilders.Deposit;
import lithium.service.cashier.processor.trustly.api.data.requestbuilders.SelectAccount;
import lithium.service.cashier.processor.trustly.api.data.response.TrustlyResponse;
import lithium.service.cashier.processor.trustly.api.security.SignatureHandler;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_NOTIFICATION_METHOD;
import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@Slf4j
@Service
public class TrustlyService {

    @Autowired
    LithiumConfigurationProperties lithiumProperties;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CashierInternalClientService cashierService;

	@Autowired
	UserApiInternalClientService userService;
    @Value("${spring.application.name}")
    private String moduleName;
    @Autowired
    private ObjectMapper mapper;

    private final static String TRANSACTION_ID_PLACEHOLDER = "{{trn_id}}";
    private final static String STATUS_PLACEHOLDER = "{{status}}";

    public final static String URL_PROPERTY_UNSPECIFIED = "";

    public ProcessorAccountResponse addProcessorAccount(AccountProcessorRequest request) throws Exception {
        ProcessorAccountResponse response = ProcessorAccountResponse.builder().build();
        try {
            SelectAccount.Build builder = new SelectAccount.Build
                    (gatewayPublicUrl() + "/public/account/notify",
                            request.getUser().getRealGuid(), "patx_" + request.getAccountTransactionId(),
                            request.getUser().getFirstName(), request.getUser().getLastName(), request.getUser().getEmail(),
                            request.getUser().getDateOfBirth() != null ? new SimpleDateFormat("yyyy-MM-dd").format(request.getUser().getDateOfBirth().toDate()) : null)
                    .locale(request.getUser().getLocale().replace("-", "_"))
                    .country(request.getUser().getCountryCode())
                    .mobilePhone(request.getUser().getCellphoneNumber())
                    .urlTarget("_self");

            String urlScheme = request.getMetadata() != null ? request.getMetadata().get("url_scheme") : null;

            builder.successURL(getRedirectUrl(request.getRedirectUrl(), request.getProperties().get("success_url_account"), request.getAccountTransactionId().toString(), "success", true, !StringUtil.isEmpty(urlScheme)));
            builder.failURL(getRedirectUrl(request.getRedirectUrl(), request.getProperties().get("fail_url_account"), request.getAccountTransactionId().toString(), "failed", true, !StringUtil.isEmpty(urlScheme)));

            builder.urlScheme(getSchemeUrl(urlScheme, request.getProperties().get("url_scheme_account"), request.getAccountTransactionId().toString()));

            Request selectAccountRequest = builder.getRequest();
            selectAccountRequest.getParams().getData().setUsername(request.getProperty("username"));
            selectAccountRequest.getParams().getData().setPassword(request.getProperty("api_password"));

            PrivateKey privateKey = SignatureHandler.getPrivateKey(Base64.getDecoder().decode(request.getProperty("rsa_private_key")), request.getProperty("rsa_private_key_password"));
            PublicKey publicKey = SignatureHandler.getPublicKey(Base64.getDecoder().decode(request.getProperty("rsa_public_key")));
            SignatureHandler.signRequest(selectAccountRequest, privateKey);

            TrustlyResponse selectAccountResponse = SignedAPI.sendRequest(selectAccountRequest, request.getProperty("payments_api_url"), publicKey);

            if (selectAccountResponse.successfulResult()) {
                Map data = (Map) selectAccountResponse.getResult().getData();
                response.setRedirectUrl(data.get("url").toString());
                response.setStatus(ProcessorAccountResponseStatus.PENDING);
                response.setProcessorReference(data.get("orderid").toString());
            } else {
                response.setStatus(ProcessorAccountResponseStatus.FAILED);
                response.setErrorCode(Integer.toString(selectAccountResponse.getError().getCode()));
                response.setErrorMessage(selectAccountResponse.getError().getMessage());
                response.setGeneralError(TrustlyAccountErrors.fromErrorCode(selectAccountResponse.getError().getCode()).getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                log.error("Failed to select trustly account. Trustly responded with error: " + selectAccountResponse.getError().getCode() + " : " + selectAccountResponse.getError().getMessage());
            }
        } catch (Exception e) {
            log.error("Failed select trustly account. Exception" + e.getMessage(), e);
            response.setStatus(ProcessorAccountResponseStatus.FAILED);
            response.setErrorCode(Integer.toString(500));
            response.setErrorMessage("Internal server error");
            response.setGeneralError(GeneralError.GENERAL_ERROR.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
        }
        return response;
    }

    public Set<Placeholder> constructPlaceholders(DoProcessorRequest processorRequest, String notificationMethod) {
        Set<Placeholder> placeholders = new HashSet<>();
        placeholders.add(CASHIER_NOTIFICATION_METHOD.from(notificationMethod));
        return placeholders;
    }

    public DoProcessorResponseStatus initiateDeposit(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");

            Deposit.Build builder = new Deposit.Build
                    (gatewayPublicUrl() + "/public/notify",
                            request.getUser().getRealGuid(), request.getTransactionId().toString(), request.getUser().getCurrency(),
                            request.getUser().getFirstName(), request.getUser().getLastName(), request.getUser().getEmail())
                    .locale(request.getUser().getLocale().replace("-", "_"))
                    .country(request.getUser().getCountryCode())
                    .amount(df.format(request.inputAmount()))
                    .mobilePhone(request.getUser().getCellphoneNumber())
                    .urlTarget("_self");

            if (request.getMethodCode().equalsIgnoreCase("ideal")) {
                builder.method("deposit.bank.netherlands.ideal");
            }
            //if url_scheme is sent by client it means that it is native IOS
            //disable redirect to LITHIUM in that case, and use client return url instead
            String urlScheme = request.stageInputData(1).get("url_scheme");

            builder.successURL(getRedirectUrl(request.stageInputData(1).get("return_url"), request.getProperties().get("success_url"), request.getTransactionId().toString(), "success", false, !StringUtil.isEmpty(urlScheme)));
            builder.failURL(getRedirectUrl(request.stageInputData(1).get("return_url"), request.getProperties().get("fail_url"), request.getTransactionId().toString(), "failed", false, !StringUtil.isEmpty(urlScheme)));

            builder.urlScheme(getSchemeUrl(urlScheme, request.getProperties().get("url_scheme"), request.getTransactionId().toString()));

            Request depositRequest = builder.getRequest();

            depositRequest.getParams().getData().setUsername(request.getProperty("username"));
            depositRequest.getParams().getData().setPassword(request.getProperty("api_password"));

            PrivateKey privateKey = SignatureHandler.getPrivateKey(Base64.getDecoder().decode(request.getProperty("rsa_private_key")), request.getProperty("rsa_private_key_password"));
            PublicKey publicKey = SignatureHandler.getPublicKey(Base64.getDecoder().decode(request.getProperty("rsa_public_key")));
            SignatureHandler.signRequest(depositRequest, privateKey);

            TrustlyResponse depositResponse = SignedAPI.sendRequest(depositRequest, request.getProperty("payments_api_url"), publicKey);

            response.addRawRequestLog(objectToPrettyString(depositRequest));
            response.addRawResponseLog(objectToPrettyString(depositResponse));
            response.setPaymentType("BANK");

            if (depositResponse.successfulResult()) {
                Map data = (Map) depositResponse.getResult().getData();
                response.setProcessorReference(data.get("orderid").toString());
                response.setIframeUrl(data.get("url").toString());
                response.setIframeMethod("GET");
                return DoProcessorResponseStatus.IFRAMEPOST_NEXTSTAGE;
            } else {
                response.setDeclineReason(depositResponse.getError().getCode() + ": " + depositResponse.getError().getMessage());
                response.setErrorCode(TrustlyDepositErrors.fromErrorCode(depositResponse.getError().getCode()).getGeneralError().getCode());
                response.setMessage(TrustlyDepositErrors.fromErrorCode(depositResponse.getError().getCode()).getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                log.error("Failed to initiate deposit. Trustly responded with error: " + depositResponse.getError().getCode() + ": " + depositResponse.getError().getMessage());
                return DoProcessorResponseStatus.DECLINED;
            }
        } catch (Exception e) {
            log.error("Failed to initiate payment for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage(), e);
            response.addRawResponseLog( "Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return DoProcessorResponseStatus.FATALERROR;
        }
    }

    public DoProcessorResponseStatus initiateWithdraw(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");

            Request withdrawRequest = new AccountPayout.Build
                    (gatewayPublicUrl() + "/public/notify",
                            request.getUser().getRealGuid(), request.getTransactionId().toString(), request.getUser().getCurrency(),
                            df.format(request.inputAmount()),
                            request.getProcessorAccount().getReference())
                    .shopperStatement("Some statement")
                    .country(request.getUser().getCountryCode())
                    .getRequest();

            withdrawRequest.getParams().getData().setUsername(request.getProperty("username"));
            withdrawRequest.getParams().getData().setPassword(request.getProperty("api_password"));

            PrivateKey privateKey = SignatureHandler.getPrivateKey(Base64.getDecoder().decode(request.getProperty("rsa_private_key")), request.getProperty("rsa_private_key_password"));
            PublicKey publicKey = SignatureHandler.getPublicKey(Base64.getDecoder().decode(request.getProperty("rsa_public_key")));
            SignatureHandler.signRequest(withdrawRequest, privateKey);

            TrustlyResponse withdrawResponse = SignedAPI.sendRequest(withdrawRequest, request.getProperty("payments_api_url"), publicKey);

            response.addRawRequestLog(objectToPrettyString(withdrawRequest));
            response.addRawResponseLog(objectToPrettyString(withdrawResponse));
            response.setPaymentType("BANK");

            if (withdrawResponse.successfulResult()) {
                Map data = (Map) withdrawResponse.getResult().getData();
                response.setProcessorReference(data.get("orderid").toString());
                return DoProcessorResponseStatus.NEXTSTAGE_NOPROCESS;
            } else {
                response.setDeclineReason(withdrawResponse.getError().getCode() + ": " +  withdrawResponse.getError().getMessage());
                response.setErrorCode(TrustlyDepositErrors.fromErrorCode(withdrawResponse.getError().getCode()).getGeneralError().getCode());
                response.setMessage(TrustlyDepositErrors.fromErrorCode(withdrawResponse.getError().getCode()).getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()));
                log.error("Failed to initiate withdraw. Trustly responded with error: " + withdrawResponse.getError().getCode() + " : " + withdrawResponse.getError().getName());
                return DoProcessorResponseStatus.DECLINED;
            }
        } catch (Exception e) {
            log.error("Failed to initiate payment for the transaction with id: " + request.getTransactionId() + ". " + e.getMessage(), e);
            response.addRawResponseLog( "Exception: " + e.getMessage() + "\\r\\n" + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return DoProcessorResponseStatus.DECLINED;
        }
    }

    private static String getSchemeUrl(String requestUrl, String propertyUrl, String transactionId) {
        if (StringUtil.isEmpty(requestUrl)) {
            return null;
        }
        if (!URL_PROPERTY_UNSPECIFIED.equals(propertyUrl)) {
            requestUrl = propertyUrl;
        }
        return Optional.ofNullable(requestUrl)
                .map(url -> url.replace(TRANSACTION_ID_PLACEHOLDER, transactionId))
                .orElse(null);
    }

    private String getRedirectUrl(String requestUrl, String propertyUrl, String transactionId, String status, boolean addAccount, boolean isNatives) {
        if (isNatives) {
            String redirectUrl = URL_PROPERTY_UNSPECIFIED.equals(propertyUrl) ? requestUrl : propertyUrl;
            return redirectUrl.replace(TRANSACTION_ID_PLACEHOLDER, transactionId).replace(STATUS_PLACEHOLDER, status);
        }  else {
            return gatewayPublicUrl() + "/public" + (addAccount ? "/account/" : "/") + "redirect/" + transactionId + "/" + status;
        }
    }

    private String gatewayPublicUrl() {
        return lithiumProperties.getGatewayPublicUrl() + "/" + moduleName;
    }
}
