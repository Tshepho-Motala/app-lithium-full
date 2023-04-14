package lithium.service.cashier.controllers.internal;

import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.cashier.client.CashierInternalAccessClient;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.services.AccessRuleService;
import lithium.service.cashier.services.DomainMethodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal")
public class CheckAccessController implements CashierInternalAccessClient {

    @Autowired
    DomainMethodService dmService;

    @Autowired
    AccessRuleService accessRuleService;

    @TimeThisMethod
    @GetMapping("/check-authorisation")
    @Override
    public Response<Boolean> isAuthorised(
            @RequestParam String domainName,
            @RequestParam String methodCode,
            @RequestParam String ipAddress,
            @RequestParam String userAgent,
            @RequestParam String userGuid,
            @RequestParam Boolean deposit) throws Exception {

        DomainMethod dm = dmService.findOneEnabledByCode(domainName, methodCode, deposit);
        boolean resp = accessRuleService.checkAuthorization(dm, ipAddress, userAgent, null ,userGuid);

        return new Response().<Boolean>builder()
                .data(resp).status(Response.Status.OK).build();
    }
}
