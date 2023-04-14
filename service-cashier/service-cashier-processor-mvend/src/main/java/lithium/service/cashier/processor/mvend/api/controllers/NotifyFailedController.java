package lithium.service.cashier.processor.mvend.api.controllers;

import lithium.service.cashier.processor.mvend.api.exceptions.Status900InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901UserNotFoundException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.mvend.api.schema.deposit.DepositResponse;
import lithium.service.cashier.processor.mvend.api.schema.deposit.DepositRequest;
import lithium.service.cashier.processor.mvend.context.DepositRequestContext;
import lithium.service.cashier.processor.mvend.services.DepositRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class NotifyFailedController {

    @Autowired
    DepositRequestService depositService;

    @PostMapping("/public/mpapi-web-rest/notify-failed.json")
    public DepositResponse deposit(@RequestBody DepositRequest request,
                                   @RequestParam String groupRef,
                                   @RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String msisdn,
                                   @RequestParam String timestamp,
                                   @RequestParam String hash)
            throws Status900InvalidHashException,
            Status901InvalidOrMissingParameters, Status901UserNotFoundException, Status999GeneralFailureException {

        DepositRequestContext context = new DepositRequestContext();

        try {

            context.setGroupRef(groupRef);
            context.setUsername(username);
            context.setPassword(password);
            context.setMsisdn(msisdn);
            context.setTimestamp(timestamp);
            context.setHash(hash);
            context.setRequest(request);

            depositService.deposit(context, false);

            DepositResponse response = new DepositResponse();
            context.setResponse(response);
            response.setReference(context.getCashierReferenceNumber().toString());
            response.setMessage("Success");

            return response;

        } finally {
            log.info("notifyfailed " + context);
        }
    }
}
