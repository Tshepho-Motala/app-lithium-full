package lithium.service.cashier.processor.trustly.controllers;

import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.ProcessorAccountClient;
import lithium.service.cashier.processor.trustly.TrustlyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ProcessorAccountController implements ProcessorAccountClient {
    @Autowired
    private TrustlyService service;

    @Override
    @RequestMapping(path="/internal/processor-account/add", method=RequestMethod.POST)
    public ProcessorAccountResponse addProcessorAccount(@RequestBody AccountProcessorRequest request) throws Exception {
        return service.addProcessorAccount(request);
    }
}
