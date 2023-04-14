package lithium.service.cashier.controllers.frontend;

import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.exceptions.NoMethodWithCodeException;
import lithium.service.cashier.services.CashierFrontendService;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lithium.service.cashier.services.DomainMethodService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping("/frontend")
public class CashierFrontendConfigController {
    @Autowired
    CashierFrontendService cashierFrontendService;
    @Autowired
    DomainMethodService dmService;
    @Autowired
    DomainMethodProcessorService domainMethodProcessorsService;

    @GetMapping("/method/properties")
    public Map<String,String> getClientProperties(
            @RequestParam String methodCode,
            @RequestParam boolean isDeposit,
            LithiumTokenUtil token,
            HttpServletRequest httpServletRequest
    ) throws Status400BadRequestException, Status500InternalServerErrorException {
        String ipAddress = (httpServletRequest.getHeader("X-Forwarded-For") != null) ? httpServletRequest.getHeader("X-Forwarded-For") : httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("user-agent");
        try {
            DomainMethodProcessor processor = cashierFrontendService.firstEnabledProcessor(token.domainName(), methodCode, isDeposit, token.guid(), ipAddress, userAgent);
            return domainMethodProcessorsService.properties(processor.getId())
                    .stream()
                    .filter(p -> p.getProcessorProperty().isAvailableForClient())
                    .filter(p -> nonNull(p.getValue()))
                    .collect(Collectors.toMap(p -> p.getProcessorProperty().getName(), DomainMethodProcessorProperty::getValue));
        } catch (Exception e) {
            if (e instanceof NoMethodWithCodeException) {
                log.info("Method with code " + methodCode + " is not configured/disabled for domain: " + token.domainName());
                throw new Status400BadRequestException("Payment method is not supported.");
            }
            log.error("Failed to get processor properties for domainName " + token.domainName() + " method " + (isDeposit? "deposit": "withdrawal") +
                    " methodCode " + methodCode + " userGuid " + token.guid() + " ipAddress " +  ipAddress + " userAgent" + userAgent + "Exception: " + e.getMessage(), e);
            throw new Status500InternalServerErrorException("Failed to get processor properties.");
        }
    }

    @GetMapping("/method/property/{name}")
    public String getClientPropertyByName(
            @PathVariable(name = "name") String propertyName,
            @RequestParam String methodCode,
            @RequestParam boolean isDeposit,
            LithiumTokenUtil token,
            HttpServletRequest httpServletRequest
    )  throws Status400BadRequestException, Status500InternalServerErrorException {
        return getClientProperties(methodCode, isDeposit, token, httpServletRequest).get(propertyName);
    }
}
