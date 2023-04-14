package lithium.service.cashier.processor.interswitch.controllers;

import lithium.exceptions.Status400BadRequestException;
import lithium.service.Response;
import lithium.service.cashier.client.internal.BanksLookupClient;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.interswitch.api.schema.Bank;
import lithium.service.cashier.processor.interswitch.services.WithdrawService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class BankListController implements BanksLookupClient {

    @Autowired
    private CashierInternalClientService cashier;
    @Autowired
    private WithdrawService withdrawService;

	private static final String METHOD_CODE = "interswitch";

    @GetMapping("/public/banks")
    public List<Bank> withdrawBanks(LithiumTokenUtil token, HttpServletRequest request) throws Exception {

	    try {
            Map<String, String> propertiesMap = cashier.propertiesMapOfFirstEnabledProcessor(METHOD_CODE, false,
		            token.guid(), token.domainName(), request.getRemoteAddr(), request.getHeader("User-Agent"));

            return withdrawService.getBankList(propertiesMap);
        } catch (Status400BadRequestException e) {
            log.warn("Failed to get banks list for interswitch", e);
            throw new Status400BadRequestException("Payment method is not supported.");
        } catch (Exception ex) {
            log.warn("Failed to get banks list for interswitch", ex);
            throw new Exception("Failed to get banks list for interswitch");
        }
    }

    @Override
    @RequestMapping(path = "/system/banks", method = RequestMethod.POST)
    public Response<List<lithium.service.cashier.client.objects.Bank>> banks(@RequestBody Map<String, String> processorProperties) throws Exception {
        List<lithium.service.cashier.client.objects.Bank> banks = withdrawService.getBankList(processorProperties).stream()
                .map(b -> lithium.service.cashier.client.objects.Bank.builder().code(b.getCbnCode()).name(b.getBankName()).build())
                .collect(Collectors.toList());
        return Response.<List<lithium.service.cashier.client.objects.Bank>>builder().data(banks).build();
    }
}
