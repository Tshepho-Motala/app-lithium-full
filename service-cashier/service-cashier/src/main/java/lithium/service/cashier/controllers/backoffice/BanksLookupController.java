package lithium.service.cashier.controllers.backoffice;

import lithium.service.Response;
import lithium.service.cashier.client.objects.Bank;
import lithium.service.cashier.services.BankAccountLookupService;
import lithium.service.cashier.services.CashierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/backoffice/cashier/banks")
public class BanksLookupController {

    @Autowired
    private BankAccountLookupService cashierService;

    @PostMapping
    public Response<List<Bank>> banksLookup(@RequestBody Map<String, String> processorProperties,
                                            @RequestParam("processorUrl") String processorUrl) throws Exception {
        Response<List<Bank>> banksLookupResponse = cashierService.banksLookup(processorUrl, processorProperties);
        return banksLookupResponse;
    }
}