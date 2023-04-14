package lithium.service.cashier.processor.bluem.ideal.controllers;

import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.bluem.ideal.BluemIdealService;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
public class CallbackController {

    @Autowired
    BluemIdealService bluemIdealService;

    @Autowired
    MessageSource messageSource;

    @PostMapping("/public/webhook")
    public ResponseEntity webhook(@RequestBody String data, HttpServletRequest request) {
        log.info("Bluem webhook is called: " + data);
        try {
            bluemIdealService.processBluemNotification(data);
        } catch (Exception e) {
            log.error("Failed to process Bluem webhook notification: " + data + " Exception: " + e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/public/redirect/{transactionId}")
    public void depositRedirect(
        @PathVariable("transactionId") Long transactionId, HttpServletResponse httpServletResponse) throws Exception  {
        try {
            log.debug("Received redirect from Bluem for transaction: " + transactionId);
            httpServletResponse.setHeader("Location", bluemIdealService.processBluemDepositRedirect(transactionId));
            httpServletResponse.setStatus(302);
        } catch (Exception ex) {
            log.error("Failed to process redirect from Bluem. TransactionId: " + transactionId, ex);
            throw ex;
        }
    }
}
