package lithium.service.cashier.processor.mvend.api.controllers;

import lithium.service.cashier.processor.mvend.api.exceptions.Status900InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901UserNotFoundException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.mvend.api.schema.deposit.DepositResponse;
import lithium.service.cashier.processor.mvend.api.schema.deposit.DepositRequest;
import lithium.service.cashier.processor.mvend.api.schema.deposit.OpayDepositStatusRequest;
import lithium.service.cashier.processor.mvend.api.schema.deposit.OpayDepositStatusResponse;
import lithium.service.cashier.processor.mvend.context.DepositRequestContext;
import lithium.service.cashier.processor.mvend.context.DepositStatusRequestContext;
import lithium.service.cashier.processor.mvend.services.DepositRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DepositController {

    @Autowired
    DepositRequestService depositService;

    @PostMapping("/public/mpapi-web-rest/deposit.json")
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

            depositService.deposit(context, true);

            DepositResponse response = new DepositResponse();
            response.setReference(context.getCashierReferenceNumber().toString());
            response.setMessage("Success");
            return response;

        } finally {
            log.info("deposit " + context);
        }
    }

    @PostMapping("/public/mpapi-web-rest/opayDepositStatus")
    public OpayDepositStatusResponse deposit(@RequestBody OpayDepositStatusRequest request)
            throws Status901InvalidOrMissingParameters, Status900InvalidHashException, Status999GeneralFailureException {

        DepositStatusRequestContext context = new DepositStatusRequestContext();
        context.setGroupRef("livescore_nigeria");
        context.setTimestamp(request.getDate());
        context.setHash(request.getSignature());
        context.setNetwork_ref(request.getNetwork_ref());

        depositService.opayDepositStatus(context);

        OpayDepositStatusResponse response = new OpayDepositStatusResponse();
        response.setStatus(context.getDepositStatus().getStatus());
        response.setDate(context.getDepositStatus().getDate());
        return response;

    }
}
