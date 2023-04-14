package lithium.service.cashier.processor.nuvei.cc.controllers;

import lithium.exceptions.Status400BadRequestException;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoStateField;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.processor.nuvei.cc.services.NuveiCCApiService;
import lithium.service.cashier.processor.nuvei.cc.services.NuveiCCDepositApiService;
import lithium.service.cashier.processor.nuvei.cc.services.NuveiCCWithdrawApiService;
import lithium.service.cashier.processor.nuvei.data.Nuvei3DNotification;
import lithium.service.cashier.processor.nuvei.data.NuveiFingerprintNotification;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class NuveiCCCallbackController {
    @Autowired
    NuveiCCDepositApiService nuveiCCDepositApiService;
    @Autowired
    NuveiCCWithdrawApiService nuveiCCWithdrawApiService;

    @RequestMapping("/public/webhook")
    public ResponseEntity webhook(
        @RequestParam String TransactionID,
        @RequestParam(name="PPP_TransactionID") String pppOrderID,
        @RequestParam(name = "payment_method") String paymentMethod,
        @RequestParam(name = "merchant_unique_id") @Valid @NotEmpty String merchantUniqueId,
        @RequestParam String transactionType,
        @RequestParam String advanceResponseChecksum,
        @RequestParam Map<String, String> requestParams)
    {
        requestParams = requestParams.entrySet().stream().filter(e -> !StringUtil.isEmpty(e.getValue())).collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
        log.info("Nuvei webhook is called: ", requestParams);
        try {
            NuveiCCApiService apiService = transactionType.equalsIgnoreCase("Credit") ? nuveiCCWithdrawApiService : nuveiCCDepositApiService;
            apiService.handlePaymentWebhook(merchantUniqueId, requestParams);
        } catch (Exception e) {
            log.error("Failed to process Nuvei webhook notification: " + requestParams + " Exception: " + e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(path="/public/{transactionId}/threeD/v1", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void threeDNotificationV1(@PathVariable Long transactionId, Nuvei3DNotification data, HttpServletResponse httpServletResponse) {
        String threeDResult = data.getPaRes();
        try {
            log.info("Nuvei 3DS1 notification is received: ", threeDResult);
            httpServletResponse.sendRedirect(nuveiCCDepositApiService.handleThreeDSecureNotification(data.getPaRes(), transactionId, false));
        } catch (Exception e) {
            log.error("Failed to handle 3Dsecure V1 challenge notification: " + data + ". For transactionid: " + transactionId + "Exception: " + e.getMessage(), e);
            httpServletResponse.setStatus(400);
        }
    }

    @PostMapping(path="/public/{transactionId}/threeD/v2", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void threeDNotificationV2(@PathVariable Long transactionId, Nuvei3DNotification data, HttpServletResponse httpServletResponse) {
        String threeDResult = new String(Base64.getDecoder().decode(data.getCres()));
        try {
            log.info("Nuvei 3DS2 notification is received: ", threeDResult);
            httpServletResponse.sendRedirect(nuveiCCDepositApiService.handleThreeDSecureNotification(threeDResult, transactionId, true));
        } catch (Exception e) {
            log.error("Failed to handle 3Dsecure V2 challenge notification: " + data + ". For transactionid: " + transactionId + "Exception: " + e.getMessage(), e);
            httpServletResponse.setStatus(400);
        }
    }

    @PostMapping(path="/public/fingerprint/notification/{transactionId}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void fingerprint(@PathVariable Long transactionId,
                                    NuveiFingerprintNotification notification,
                                    HttpServletResponse httpServletResponse) {
        try {
            log.info("Nuvei payment fingerprint notification is received: ", notification);
            httpServletResponse.sendRedirect(nuveiCCDepositApiService.handleFingerprint(transactionId, notification));
        } catch (Exception e) {
            log.error("Failed to handle fingerprint notification: " + notification + ". For transactionid: " + transactionId + "Exception: " + e.getMessage(), e);
            httpServletResponse.setStatus(400);
        }
    }

}
