package lithium.service.cashier.processor.paystack.controllers;

import lithium.service.cashier.processor.paystack.services.DepositService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequestMapping("/public")
public class RedirectReturnController {

    @Autowired
    DepositService depositService;

    @GetMapping("/redirectreturn")
    //both param have there sama value paystack reference
    public String redirectReturn(@RequestParam("trxref") String trxref,
                                 @RequestParam(value = "reference", required = false) String reference) throws Exception {

        try {

            log.info("RedirectReturn is received: trxref " + trxref + " reference " + reference);

            String returnUrl = depositService.processRedirect(trxref);

            log.info("Return url " + returnUrl);

            return "redirect:" + returnUrl;

        } catch (Exception e) {
            log.error("Exception during redirect: " + e.getMessage() + " Stack: " + ExceptionUtils.getRootCauseMessage(e));
            throw e;
        }

    }

    @GetMapping("/playercancel/{trxref}")
    public String redirectPlayerCancelTransaction(@PathVariable("trxref") Long trxref) throws Exception {

        String cancelUrl = "";
        try {
            log.info("Redirect Transaction Cancel is received: trxref " + trxref);
            cancelUrl = depositService.processPlayerCancelTransaction(trxref);
        } catch (Exception e) {
            log.error("Exception during cancel: " + e.getMessage() + " Stack: " + ExceptionUtils.getRootCauseMessage(e));
            throw e;
        }
        return  "redirect:" + cancelUrl;
    }
}
