package lithium.service.cashier.processor.opay.api.controllers;

import lithium.service.Response;
import lithium.service.cashier.client.internal.BankAccountLookupClient;
import lithium.service.cashier.client.objects.BankAccountLookupResponse;
import lithium.service.cashier.client.objects.BankAccountLookupRequest;
import lithium.service.cashier.processor.opay.services.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class BankAccountLookupController implements BankAccountLookupClient {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    @RequestMapping(path = "/system/bank-account-lookup", method = RequestMethod.POST)
    public Response<BankAccountLookupResponse> bankAccountLookup(@RequestBody BankAccountLookupRequest bankAccountLookupRequest) throws Exception {
        Response<BankAccountLookupResponse> bankAccountLookup = withdrawService.bankAccountLookup(bankAccountLookupRequest, restTemplate);
        log.info("Opay Bank Account Lookup: {}", bankAccountLookup);
        return bankAccountLookup;
    }
}
