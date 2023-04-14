package lithium.service.cashier.processor.nuvei.cc.controllers;

import lithium.service.cashier.processor.nuvei.data.NuveiInitializeData;
import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.client.objects.DomainMethodProcessorProperty;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.nuvei.cc.services.NuveiCCDepositApiService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class NuveiPaymentController {
    @Autowired
    CashierInternalClientService cashier;
    @Autowired
    NuveiCCDepositApiService nuveiCCDepositApiService;

    @GetMapping("/frontend/initialize/data")
    public NuveiInitializeData initData(@RequestHeader(value = "User-Agent") String userAgent,
                            LithiumTokenUtil token,
                            HttpServletRequest request) throws Status500InternalServerErrorException, Status400BadRequestException {
        try {
            log.debug("Get user init data for: [user=" + token.guid() + " userAgent=" + userAgent + ", userIp=" + request.getRemoteAddr() + ", token=" + token + "]");

            List<DomainMethodProcessorProperty> properties = cashier.propertiesOfFirstEnabledProcessor(
                    "nuvei-cc", true,
                    token.guid(), token.domainName(),
                    request.getRemoteAddr(), request.getHeader("User-Agent")
            );

            Map<String, String> propertiesMap = properties.stream()
                    .collect(Collectors.toMap(p -> p.getProcessorProperty().getName(), p -> p.getValue()));
            String merchantId = propertiesMap.get("merchant_id");
            String merchantSiteId = propertiesMap.get("merchant_site_id");
            String merchantKey = propertiesMap.get("merchant_key");
            boolean test = Boolean.parseBoolean(propertiesMap.get("test"));

            NuveiInitializeData data = NuveiInitializeData.builder()
                    .merchantId(merchantId)
                    .merchantSiteId(merchantSiteId)
                    .sessionToken(nuveiCCDepositApiService.getSessionToken(merchantId, merchantSiteId, merchantKey, test))
                    .env(test ? "int" : "prod")
                    .build();

            return data;
        } catch (Exception e) {
            log.error("Failed get nuvei initialization data.", e);
            if (e instanceof Status400BadRequestException) {
                throw new Status400BadRequestException("Payment method is not supported.");
            }
            throw new Status500InternalServerErrorException("Failed get nuvei initialization data.");
        }
    }
}
