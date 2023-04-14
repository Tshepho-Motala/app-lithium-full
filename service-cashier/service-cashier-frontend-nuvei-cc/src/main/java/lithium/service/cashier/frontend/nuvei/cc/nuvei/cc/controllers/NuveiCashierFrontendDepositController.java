package lithium.service.cashier.processor.nuvei.cc.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.frontend.nuvei.cc.nuvei.cc.model.Fingerprint;
import lithium.service.cashier.frontend.nuvei.cc.nuvei.cc.model.QuickDeposit;
import lithium.service.cashier.frontend.nuvei.cc.nuvei.cc.model.ThreeDSecure;
import lithium.service.cashier.processor.nuvei.data.NuveiFingerprintNotification;
import lithium.service.cashier.processor.nuvei.data.NuveiInitializeData;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.frontend.DoStateField;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class NuveiCashierFrontendDepositController {
    @Autowired
    LithiumConfigurationProperties lithiumProperties;

    @Autowired
    CashierInternalClientService cashier;

    @Autowired
    private RestTemplate restTemplate;
    @Value("${spring.application.name}")
    private String moduleName;
    @Autowired
    private ObjectMapper mapper;

    @GetMapping("/deposit")
    public ModelAndView deposit(
        LithiumTokenUtil token,
        HttpServletRequest request
    ) throws Exception {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization", request.getHeader("Authorization"));
        headers.add("User-Agent", request.getHeader("User-Agent"));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<NuveiInitializeData> response = restTemplate.exchange(lithiumProperties.getGatewayPublicUrl() + "/service-cashier-processor-nuvei-cc/frontend/initialize/data", HttpMethod.GET, entity, NuveiInitializeData.class);

        NuveiInitializeData model = response.getBody();

        log.debug("Deposit [model=" + model + "]");
        return new ModelAndView("deposit", "model", model);
    }

    @GetMapping("/quickdeposit")
    public ModelAndView quickdeposit(
        LithiumTokenUtil token,
        HttpServletRequest request
    ) throws Exception {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization", request.getHeader("Authorization"));
        headers.add("User-Agent", request.getHeader("User-Agent"));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(lithiumProperties.getGatewayPublicUrl() + "/service-cashier/frontend/processor-accounts?isDeposit=true", HttpMethod.GET, entity, String.class);

        JsonNode dataNode = Optional.ofNullable(mapper.readTree(response.getBody()))
            .map(n -> n.findValue("data")).orElse(null);

        List<ProcessorAccount> processorAccounts = null;

        if(dataNode != null) {
            ObjectReader reader = mapper.readerFor(new TypeReference<List<ProcessorAccount>>() {});
            processorAccounts = reader.readValue(dataNode);
        }

        if (processorAccounts == null || processorAccounts.isEmpty()) {
            return new ModelAndView("redirect:/" + moduleName + "/deposit");
        } else {
            return new ModelAndView("quickdeposit", "model", QuickDeposit.builder().processorAccounts(processorAccounts).build());
        }
    }

    @RequestMapping(value="/deposit/do", method= RequestMethod.POST)
    public ModelAndView doDeposit(
        @RequestParam String amount,
        @RequestParam(required = false) String cvv,
        @RequestParam(required = false) String nameoncard,
        @RequestParam(required = false) String sessionToken,
        @RequestParam(required = false) String ccTempToken,
        @RequestParam(required = false) String proccesorAccountId,
        @RequestParam String javaEnabled,
        @RequestParam String javaScriptEnabled,
        @RequestParam String colorDepth,
        @RequestParam String screenHeight,
        @RequestParam String screenWidth,
        @RequestParam String timeZone,
        @RequestHeader(value = "User-Agent") String userAgent,
        LithiumTokenUtil litiumToken,
        HttpServletRequest request
    ) throws Exception {
        log.debug("Payment post [amount=" + amount + ", userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", ccTempToken=" + ccTempToken + ", sessionToken=" + sessionToken + ", ccTempToken=" + ccTempToken + ", proccesorAccountId=" + proccesorAccountId +"]");
        try {

            DoRequest depositRequest = DoRequest.builder()
                .stage(1)
                .state("VALIDATEINPUT").build();

            DoStateField ammountField = DoStateField.builder().value(amount).build();
            DoStateFieldGroup commonStateFields = new DoStateFieldGroup();
            commonStateFields.getFields().put("amount", ammountField);
            depositRequest.getInputFieldGroups().put("1", commonStateFields);

            DoStateFieldGroup stateFields = new DoStateFieldGroup();
            DoStateField cardRefField;
            if (ccTempToken != null && !ccTempToken.isEmpty()) {
                cardRefField = DoStateField.builder().value(ccTempToken).build();
                stateFields.getFields().put("card_token", cardRefField);
            } else {
                cardRefField = DoStateField.builder().value(proccesorAccountId).build();
                stateFields.getFields().put("processorAccountId", cardRefField);
                if (cvv != null && !cvv.isEmpty()) {
                    DoStateField cvvField = DoStateField.builder().value(cvv).build();
                    stateFields.getFields().put("cvv", cvvField);
                }
            }

            if (!StringUtil.isEmpty(sessionToken)) {
                DoStateField sessionTokenField = DoStateField.builder().value(sessionToken).build();
                stateFields.getFields().put("session_token", sessionTokenField);
            }

            if (nameoncard != null && !nameoncard.isEmpty()) {
                DoStateField nameField = DoStateField.builder().value(nameoncard).build();
                stateFields.getFields().put("nameoncard", nameField);
            }

            stateFields.getFields().put("save_card", DoStateField.builder().value("true").build());

            stateFields.getFields().put("return_url",
                DoStateField.builder().value(gatewayPublicUrl() + "/public/result").build());

            stateFields.getFields().put("merchant_url",
                DoStateField.builder().value("https://www.livescorebet.com").build());

            stateFields.getFields().put("method_notification_url",
                DoStateField.builder().value(gatewayPublicUrl() + "/public/fingerprint/notification/{{trn_id}}").build());

            stateFields.getFields().put("java_enabled", DoStateField.builder().value(javaEnabled).build());
            stateFields.getFields().put("java_script_enabled", DoStateField.builder().value(javaScriptEnabled).build());
            stateFields.getFields().put("color_depth", DoStateField.builder().value(colorDepth).build());
            stateFields.getFields().put("screen_height", DoStateField.builder().value(screenHeight).build());
            stateFields.getFields().put("screen_width", DoStateField.builder().value(screenWidth).build());
            stateFields.getFields().put("time_zone", DoStateField.builder().value(timeZone).build());

            depositRequest.getInputFieldGroups().put("2", stateFields);

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Authorization", request.getHeader("Authorization"));
            headers.add("User-Agent", userAgent);
            HttpEntity<DoRequest> entity = new HttpEntity<>(depositRequest, headers);

            HttpEntity<DoResponse> response = restTemplate.postForEntity(lithiumProperties.getGatewayPublicUrl() + "/service-cashier/frontend/deposit/v2?methodCode=nuvei-cc",
                entity, DoResponse.class);

            DoResponse doResponse = response.getBody();
            if (doResponse != null && doResponse.getState().equals("WAITFORPROCESSOR")
                && doResponse.getIframeUrl() != null && !doResponse.getIframeUrl().isEmpty()) {
                if (doResponse.getIframePostData().get("creq") != null) {
                    return new ModelAndView("threedsecurev2", "model", ThreeDSecure.builder().acsUrl(doResponse.getIframeUrl()).creq(doResponse.getIframePostData().get("creq")).build());
                } else if (doResponse.getIframePostData().get("PaReq") != null) {
                    return new ModelAndView("threedsecurev1", "model", ThreeDSecure.builder()
                        .acsUrl(doResponse.getIframeUrl())
                        .paRequest(doResponse.getIframePostData().get("PaReq"))
                        .tempUrl(doResponse.getIframePostData().get("TermUrl"))
                        .build());
                } else {
                    return new ModelAndView("fingerprint", "model", Fingerprint.builder()
                        .methodUrl(doResponse.getIframeUrl())
                        .threeDSMethodData(doResponse.getIframePostData().get("threeDSMethodData"))
                        .transactionId(doResponse.getTransactionId().toString())
                        .build());
                }
            }
        } catch (Exception ex) {
            log.error("Failed  to process deposit request. User: " + litiumToken.guid() + "[amount=" + amount + ", userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", ccTempToken=" + ccTempToken + ", sessionToken=" + sessionToken + ", ccTempToken=" + ccTempToken + ", proccesorAccountId=" + proccesorAccountId +"]", ex);
        }
        return new ModelAndView("redirect:" + gatewayPublicUrl() + "/public/result");
    }

    @GetMapping(path="/public/fingerprint/notification/{transactionId}")
    public ModelAndView fingerprint(@PathVariable Long transactionId,
                                      @RequestHeader(value = "User-Agent") String userAgent,
                                      LithiumTokenUtil litiumToken,
                                      HttpServletRequest request) {
        log.info("Nuvei fingerprint notification is received for transactionid: " + transactionId);
        return deposit2(transactionId, "Y", userAgent, litiumToken, request);
    }

    @RequestMapping(value="/fingerprint/do", method= RequestMethod.POST)
    public ModelAndView doDepositFingerprint(
        @RequestParam Long transactionId,
        @RequestParam String methodCompletionInd,
        @RequestHeader(value = "User-Agent") String userAgent,
        LithiumTokenUtil litiumToken,
        HttpServletRequest request
    ) {
        log.info("Nuvei time out on fingerprint. TransactionId: " + transactionId);
        return deposit2(transactionId, methodCompletionInd, userAgent, litiumToken, request);
    }

    public ModelAndView deposit2(
        Long transactionId,
        String methodCompletionInd,
        String userAgent,
        LithiumTokenUtil litiumToken,
        HttpServletRequest request
    ) {
        try {

            DoRequest depositRequest = DoRequest.builder()
                .stage(2)
                .state("WAITFORPROCESSOR")
                .transactionId(transactionId).build();

            DoStateFieldGroup stateFields = new DoStateFieldGroup();

            DoStateField methodCompletionIndDF = DoStateField.builder().value(methodCompletionInd).build();
            stateFields.getFields().put("method_completion_ind", methodCompletionIndDF);
            depositRequest.getInputFieldGroups().put("1", stateFields);

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Authorization", request.getHeader("Authorization"));
            headers.add("User-Agent", userAgent);
            HttpEntity<DoRequest> entity = new HttpEntity<>(depositRequest, headers);

            HttpEntity<DoResponse> response = restTemplate.postForEntity(lithiumProperties.getGatewayPublicUrl() + "/service-cashier/frontend/deposit/v2?methodCode=nuvei-cc",
                entity, DoResponse.class);

            DoResponse doResponse = response.getBody();
            if (doResponse != null && doResponse.getState().equals("WAITFORPROCESSOR")
                && doResponse.getIframeUrl() != null && !doResponse.getIframeUrl().isEmpty()) {
                if (doResponse.getIframePostData().get("creq") != null) {
                    return new ModelAndView("threedsecurev2", "model", ThreeDSecure.builder().acsUrl(doResponse.getIframeUrl()).creq(doResponse.getIframePostData().get("creq")).build());
                } else {
                    return new ModelAndView("threedsecurev1", "model", ThreeDSecure.builder()
                        .acsUrl(doResponse.getIframeUrl())
                        .paRequest(doResponse.getIframePostData().get("PaReq"))
                        .tempUrl(doResponse.getIframePostData().get("TermUrl"))
                        .build());
                }
            }
        } catch (Exception ex) {
            log.error("Failed  to process second deposit request. User: " + litiumToken.guid() + "[userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", transactionId=" + transactionId + "]", ex);
        }
        return new ModelAndView("redirect:" + gatewayPublicUrl() + "/public/result");
    }

    public String gatewayPublicUrl() {
        return lithiumProperties.getGatewayPublicUrl()  + "/" + moduleName;
    }

    @RequestMapping("/public/result")
    public String result()
    {
        return "Call get transaction API here. Or check in LBO manually.";
    }
}
