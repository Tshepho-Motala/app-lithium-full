package lithium.service.cashier.processor.opay.api.controllers;

import lithium.service.Response;
import lithium.service.cashier.client.CashierProcessorAccountInternalClient;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.client.internal.InitialValidateClient;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.processor.opay.services.WithdrawService;
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
public class UserController implements InitialValidateClient {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private RestTemplate restTemplate;

	@Autowired @Setter
	private LithiumServiceClientFactory services;

    @Override
    @RequestMapping(path = "/internal/initial-validate/{domainName}/{type}", method = RequestMethod.POST)
    public Response<Boolean> validate(@RequestBody Map<String, DoStateFieldGroup> inputFieldGroups,
                                      @PathVariable("domainName") String domainName,
                                      @PathVariable("type") String type) throws Exception {
        DoStateFieldGroup doStateFieldGroup = inputFieldGroups.get("2");
        if (!doStateFieldGroup.getFields().containsKey("account_number")) {
            log.warn("Missing 'account_number' in withdraw request");
            return Response.<Boolean>builder().data(false).message("Missing 'account_number' in withdraw request").build();
        }

	    String phoneNumber;
	    String processorAccountId = doStateFieldGroup.getFields().get("processorAccountId").getValue();

	    if (processorAccountId != null ) {
		    CashierProcessorAccountInternalClient client = services.target(CashierProcessorAccountInternalClient.class);
		    Response<ProcessorAccount> response = client.getProcessorAccountById(Long.valueOf(processorAccountId));
			if (!response.isSuccessful()) {
				return Response.<Boolean>builder().data(false).message("Incorrect value 'processorAccountId':"+processorAccountId).build();
			}
		    phoneNumber = response.getData().getDescriptor();
	    } else {
		    phoneNumber = doStateFieldGroup.getFields().get("account_number").getValue();
	    }
        return withdrawService.validateUser(phoneNumber, domainName, restTemplate);
    }
}
