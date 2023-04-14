package lithium.service.cashier.processor.interswitch.controllers;

import lithium.service.Response;
import lithium.service.cashier.client.CashierProcessorAccountInternalClient;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.client.internal.InitialValidateClient;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.interswitch.services.WithdrawService;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
public class ValidateController implements InitialValidateClient {

    @Autowired
    private CashierInternalClientService cashierService;
	@Autowired @Setter
	private LithiumServiceClientFactory services;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private RestTemplate restTemplate;

    private static final boolean IS_DEPOSIT = false;
    private static final String METHOD_NAME = "interswitch";

    @Override
    @RequestMapping(path = "/internal/initial-validate/{domainName}/{type}", method = RequestMethod.POST)
    public Response<Boolean> validate(@RequestBody Map<String, DoStateFieldGroup> inputFieldGroups,
                                      @PathVariable("domainName") String domainName,
                                      @PathVariable("type") String type) throws Exception {
        DoStateFieldGroup doStateFieldGroup = inputFieldGroups.get("2");
        if (!doStateFieldGroup.getFields().containsKey("account_number") || !doStateFieldGroup.getFields().containsKey("bank_code")) {
            log.warn("Missing 'account_number' or 'bank_code' in withdraw request");
            return Response.<Boolean>builder().data(false).message("Missing 'account_number' or 'bank_code' in withdraw request").build();
        }

	    String accountNumber ;
	    String bankCode;

		if (doStateFieldGroup.getFields().get("processorAccountId").getValue()!=null) {
			ProcessorAccount processorAccount = getProcessorAccount(doStateFieldGroup.getFields().get("processorAccountId").getValue());
			accountNumber = processorAccount.getData().get("account_number");
			bankCode = processorAccount.getData().get("bank_code");
		} else {
			accountNumber = doStateFieldGroup.getFields().get("account_number").getValue();
			bankCode = doStateFieldGroup.getFields().get("bank_code").getValue();
		}

        DomainMethodProcessor dmp = cashierService.processorByMethodCodeAndProcessorDescription(
                domainName, IS_DEPOSIT,
                METHOD_NAME, METHOD_NAME);

        if (dmp == null || !dmp.getEnabled()) {
            log.warn("Cant get properties from DomainMethodProcessor = " + METHOD_NAME);
            return Response.<Boolean>builder().data(false).message("Cant get properties from DomainMethodProcessor = " + METHOD_NAME).build();
        }

        return withdrawService.validateAccount(accountNumber, bankCode, dmp.getProperties(), restTemplate);
    }

	private ProcessorAccount getProcessorAccount(String processorAccountId) throws Exception {
		CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);
		Response<ProcessorAccount> response = client.getProcessorAccountById(Long.valueOf(processorAccountId));
		if (!response.isSuccessful()) {
			throw new Exception("Cant get processor account by Id: " + processorAccountId + " Response: "+ response.getStatus().id() + " " + response.getMessage());
		}
		return response.getData();
	}
}
