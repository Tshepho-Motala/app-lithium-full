package lithium.service.cashier.processor.bluem.ideal.controllers;

import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.ProcessorAccountClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ProcessorAccountController implements ProcessorAccountClient {

    @Override
    @RequestMapping(path="/internal/processor-account/add", method=RequestMethod.POST)
    public ProcessorAccountResponse addProcessorAccount(@RequestBody AccountProcessorRequest request) throws Exception {
        throw new NotImplementedException();
    }
}
