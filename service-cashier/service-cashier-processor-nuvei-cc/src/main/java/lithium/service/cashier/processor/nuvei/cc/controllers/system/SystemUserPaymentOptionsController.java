package lithium.service.cashier.processor.nuvei.cc.controllers.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.client.CashierProcessorUserCardRetrievalClient;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.processor.nuvei.cc.services.NuveiCCUserPaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/user-payment-options")
public class SystemUserPaymentOptionsController implements CashierProcessorUserCardRetrievalClient {
    private final NuveiCCUserPaymentMethodService service;

    @Autowired
    public SystemUserPaymentOptionsController(NuveiCCUserPaymentMethodService service) {
        this.service = service;
    }

    @PostMapping("/retrieve")
    public List<ProcessorAccount> retrieveUserPaymentOptions(@RequestParam("domainName") String domainName,
            @RequestParam("userTokenId") String userTokenId) throws Status500InternalServerErrorException {
        return service.retrieveFromNuvei(domainName, userTokenId);
    }
}
