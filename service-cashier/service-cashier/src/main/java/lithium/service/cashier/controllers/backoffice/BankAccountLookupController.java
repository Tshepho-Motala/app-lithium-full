package lithium.service.cashier.controllers.backoffice;

import lithium.service.Response;
import lithium.service.cashier.client.objects.BankAccountLookupRequest;
import lithium.service.cashier.client.objects.BankAccountLookupResponse;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.services.BankAccountLookupService;
import lithium.service.cashier.services.CashierService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/backoffice/cashier/bank-account-lookup")
public class BankAccountLookupController {

    @Autowired
    private CashierService cashierService;

	@Autowired
	private BankAccountLookupService bankAccountLookupService;

    @GetMapping(value = "/find-by-transaction-id")
    public Response<BankAccountLookupResponse> bankAccountLookup(@RequestParam("domainName") String domainName,
                                                                 @RequestParam("processorCode") String processorCode,
                                                                 @RequestParam("processorDescription") String processorDescription,
                                                                 @RequestParam("processorUrl") String processorUrl,
                                                                 @RequestParam("transactionId") long transactionId,
                                                                 LithiumTokenUtil token, HttpServletRequest request) throws Exception {
        Response<BankAccountLookupResponse> bankAccountLookupResponse = bankAccountLookupService.bankAccountLookup(processorUrl, transactionId, domainName, processorCode,
                token.guid(), request.getRemoteAddr(), request.getHeader("User-Agent"));
        return bankAccountLookupResponse;
    }

    @PostMapping(value = "/find-by-bank-account-lookup-request")
    public Response<BankAccountLookupResponse> bankAccountLookup(@RequestBody BankAccountLookupRequest bankAccountLookupRequest,
                                                                 @RequestParam("processorUrl") String processorUrl) throws Exception {
        Response<BankAccountLookupResponse> bankAccountLookupResponse = bankAccountLookupService.bankAccountLookup(processorUrl, bankAccountLookupRequest);
        return bankAccountLookupResponse;
    }

    @GetMapping(value = "/find-by-domain-name/withdrawal-dmps")
    private Response<List<DomainMethodProcessor>> findWithdrawalDmpsByDomainName(@RequestParam String domainName) throws Exception {
        Response<List<DomainMethodProcessor>> domainMethodProcessorResponse = bankAccountLookupService.getProcessorsByDomainNameAndDeposit(domainName, false);
        return domainMethodProcessorResponse;
    }
}
