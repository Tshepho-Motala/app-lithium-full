package lithium.service.cashier.processor.smartcash.controllers;

import lithium.service.cashier.processor.smartcash.services.SmartcashApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/public")
public class CallbackController {
    @Autowired
    SmartcashApiService smartcashApiService;

    @RequestMapping("/webhook")
    public ResponseEntity webhook(@RequestBody String data) {
        try {
            log.info("Smartcash webhook is called: " + data);

            smartcashApiService.handlePaymentWebhook(data);

        } catch(Exception e) {
            log.error("Failed to proceed Smartcash webhook. Request: " + data + " Exception: "+ String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }
}
