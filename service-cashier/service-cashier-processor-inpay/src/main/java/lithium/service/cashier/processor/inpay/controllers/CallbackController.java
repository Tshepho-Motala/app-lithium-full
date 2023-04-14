package lithium.service.cashier.processor.inpay.controllers;

import lithium.service.cashier.processor.inpay.api.data.InpayWebhookData;
import lithium.service.cashier.processor.inpay.services.InpayCryptoService;
import lithium.service.cashier.processor.inpay.services.WithdrawService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class CallbackController {

	@Autowired
	private WithdrawService withdrawService;

	@PostMapping(value="/public/webhook", consumes="application/x-www-form-urlencoded")
	public ResponseEntity webhook(InpayWebhookData inpayWebhookData) {
		log.info("Webhook is called: " + inpayWebhookData);
		try {
			withdrawService.proceedWithdrawWebhook(inpayWebhookData);
		} catch (Exception ex) {
			log.error("Got exception during process webhook: ", ex);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.ok().build();
	}


	@PostMapping(value="/public/{domain}/webhook/v2")
	public ResponseEntity webhookV2(
			@PathVariable("domain") String domain,
			@RequestBody String inpayWebhookData) {
		try {
			withdrawService.proceedWithdrawWebhookV2(domain, inpayWebhookData);
		} catch (Exception ex) {
			log.error("Got exception during process webhook(v2): ", ex);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.ok().build();
	}

}
