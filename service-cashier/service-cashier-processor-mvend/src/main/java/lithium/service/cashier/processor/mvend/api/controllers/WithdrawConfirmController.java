package lithium.service.cashier.processor.mvend.api.controllers;

import lithium.service.cashier.processor.mvend.api.exceptions.Status900InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901UserNotFoundException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.mvend.api.schema.Response;
import lithium.service.cashier.processor.mvend.api.schema.withdraw.WithdrawConfirmRequest;
import lithium.service.cashier.processor.mvend.context.WithdrawConfirmationContext;
import lithium.service.cashier.processor.mvend.services.DepositRequestService;
import lithium.service.cashier.processor.mvend.services.WithdrawConfirmRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WithdrawConfirmController {

    @Autowired
    WithdrawConfirmRequestService service;

    @PostMapping("/public/mpapi-web-rest/complete-withdraw.json")
    public Response withdrawConfirmation(@RequestBody WithdrawConfirmRequest request,
                                   @RequestParam String groupRef,
                                   @RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String msisdn,
                                   @RequestParam String timestamp,
                                   @RequestParam String hash)
            throws Status900InvalidHashException,
            Status901InvalidOrMissingParameters, Status901UserNotFoundException, Status999GeneralFailureException {

        WithdrawConfirmationContext context = new WithdrawConfirmationContext();

        try {
            context.setGroupRef(groupRef);
            context.setUsername(username);
            context.setPassword(password);
            context.setMsisdn(msisdn);
            context.setTimestamp(timestamp);
            context.setHash(hash);
            context.setRequest(request);

            service.withdrawConfirm(context);

            Response response = new Response();
            response.setMessage("Success");
            context.setResponse(response);

            return response;
        } finally {
            log.info("complete-withdraw " + context);
        }
    }
}
