package lithium.service.cashier.processor.flutterwave.controllers;

import lithium.service.cashier.processor.flutterwave.services.DepositService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class RedirectReturnController {

    @Autowired
    DepositService depositService;

    @GetMapping("/public/redirectreturn")
    public String redirectReturn(@RequestParam("status") String status,
                                 @RequestParam("tx_ref") Long transactionId,
                                 @RequestParam(value = "transaction_id", required = false) String processorReferencce) throws Exception {

        try {

            log.info("RedirectReturn is received: status " + status + " tx_ref " + transactionId + " transaction_id " + processorReferencce);

            String returnUrl = (status.equalsIgnoreCase("cancelled") && processorReferencce == null)
                    ? depositService.processCancelRedirect(status, transactionId)
                    : depositService.processRedirect(status, transactionId, processorReferencce);

            log.info("Return url " + returnUrl);

            return "redirect:" + returnUrl;

        } catch (Exception e) {
            log.error("Exception during redirect (" + transactionId + "): ", e);
            throw e;
        }

    }


}
