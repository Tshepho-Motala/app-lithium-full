package lithium.service.cashier.processor.mvend.api.controllers;

import lithium.service.cashier.processor.mvend.api.exceptions.Status101UserNotFoundException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status106InvalidUserIDOrKeyException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status107InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.mvend.api.schema.balance.BalanceResponse;
import lithium.service.cashier.processor.mvend.context.BalanceRequestContext;
import lithium.service.cashier.processor.mvend.services.BalanceRequestService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.math.CurrencyAmount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BalanceController {

    @Autowired
    BalanceRequestService balanceRequestService;

    @GetMapping("/public/mpapi-web-rest/balance.json")
    public BalanceResponse balance(@RequestParam String groupRef,
                                   @RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String msisdn,
                                   @RequestParam String timestamp,
                                   @RequestParam String hash)
            throws  Status101UserNotFoundException, Status999GeneralFailureException,
                    Status106InvalidUserIDOrKeyException, Status107InvalidHashException,
                    Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
                    Status496PlayerCoolingOffException {

        BalanceRequestContext context = new BalanceRequestContext();

        try {

            context.setGroupRef(groupRef);
            context.setUsername(username);
            context.setPassword(password);
            context.setMsisdn(msisdn);
            context.setTimestamp(timestamp);
            context.setHash(hash);

            balanceRequestService.balance(context);

            BalanceResponse response = new BalanceResponse();
            context.setResponse(response);
            response.setGroupRef(groupRef);
            response.setMsisdn(msisdn);
            response.setCurrency(context.getCurrencyCode());
            response.setBalance(CurrencyAmount.fromCents(context.getBalanceInCents()).toAmount().doubleValue());
            response.setFirstName(context.getFirstName());
            response.setMessage("Success");
            return response;

        } finally {
            log.info("balance " + context);
        }
    }

}
